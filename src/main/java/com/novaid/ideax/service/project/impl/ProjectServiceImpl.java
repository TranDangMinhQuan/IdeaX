package com.novaid.ideax.service.project.impl;

import com.novaid.ideax.dto.project.ProjectFileDTO;
import com.novaid.ideax.dto.project.ProjectRequestDTO;
import com.novaid.ideax.dto.project.ProjectResponseDTO;
import com.novaid.ideax.entity.auth.Account;
import com.novaid.ideax.entity.project.FileStorage;
import com.novaid.ideax.entity.project.Project;
import com.novaid.ideax.entity.project.ProjectFile;
import com.novaid.ideax.enums.FileType;
import com.novaid.ideax.enums.ProjectStatus;
import com.novaid.ideax.repository.auth.AccountRepository;
 // Quan trọng: Import service đã tạo
import com.novaid.ideax.repository.project.ProjectFileRepository;
import com.novaid.ideax.repository.project.ProjectRepository;
import com.novaid.ideax.service.project.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;
    private final AccountRepository accountRepository;
    private final FileStorageService fileStorageService; // ✨ Inject service xử lý file

    // Định nghĩa thư mục con để lưu file của project, giúp tổ chức gọn gàng
    private static final String PROJECT_FILES_SUBDIR = "project-files";

    @Override
    public ProjectResponseDTO createProject(Long accountId, ProjectRequestDTO dto) {
        Account startup = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with id: " + accountId));

        Project project = Project.builder()
                .projectName(dto.getProjectName())
                .category(dto.getCategory())
                .customCategory(dto.getCustomCategory())
                .fundingStage(dto.getFundingStage())
                .fundingRange(dto.getFundingRange())
                .fundingAmount(dto.getFundingAmount())
                .teamSize(dto.getTeamSize())
                .location(dto.getLocation())
                .website(dto.getWebsite())
                .description(dto.getDescription())
                .status(Optional.ofNullable(dto.getStatus()).orElse(ProjectStatus.DRAFT))
                .startup(startup)
                .build();

        Project savedProject = projectRepository.save(project);

        // Ủy quyền việc xử lý file cho helper method
        handleFileUpload(savedProject, startup, dto);

        return mapToResponse(savedProject);
    }

    @Override
    public ProjectResponseDTO updateProject(Long accountId, Long projectId, ProjectRequestDTO dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));
        Account uploader = project.getStartup(); // Người upload luôn là chủ dự án

        if (!uploader.getId().equals(accountId)) {
            throw new SecurityException("Unauthorized: You are not the owner of this project.");
        }

        // Cập nhật các trường thông tin
        Optional.ofNullable(dto.getProjectName()).ifPresent(project::setProjectName);
        Optional.ofNullable(dto.getCategory()).ifPresent(project::setCategory);
        Optional.ofNullable(dto.getCustomCategory()).ifPresent(project::setCustomCategory);
        Optional.ofNullable(dto.getFundingStage()).ifPresent(project::setFundingStage);
        Optional.ofNullable(dto.getFundingRange()).ifPresent(project::setFundingRange);
        Optional.ofNullable(dto.getTeamSize()).ifPresent(project::setTeamSize);
        Optional.ofNullable(dto.getLocation()).ifPresent(project::setLocation);
        Optional.ofNullable(dto.getWebsite()).ifPresent(project::setWebsite);
        Optional.ofNullable(dto.getDescription()).ifPresent(project::setDescription);
        Optional.ofNullable(dto.getStatus()).ifPresent(project::setStatus);
        Optional.ofNullable(dto.getFundingAmount()).ifPresent(project::setFundingAmount);

        // Ủy quyền việc xử lý file cho helper method
        handleFileUpload(project, uploader, dto);

        Project updatedProject = projectRepository.save(project);
        return mapToResponse(updatedProject);
    }

    @Override
    public void deleteProject(Long accountId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));

        if (!project.getStartup().getId().equals(accountId)) {
            throw new SecurityException("Unauthorized: You are not the owner of this project.");
        }

        // ✨ Xóa các file vật lý trước khi xóa project khỏi DB
        for (ProjectFile pf : project.getFiles()) {
            fileStorageService.deletePhysicalFile(pf.getFileStorage().getFilePath());
        }

        projectRepository.delete(project);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getMyProjects(Long accountId) {
        return projectRepository.findByStartup_Id(accountId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectResponseDTO approveProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        project.setStatus(ProjectStatus.APPROVED);
        return mapToResponse(projectRepository.save(project));
    }

    @Override
    public ProjectResponseDTO rejectProject(Long projectId, String note) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));
        project.setStatus(ProjectStatus.REJECTED);
        project.setAdminNote(note);
        return mapToResponse(projectRepository.save(project));
    }

    // --- Private Helper Methods ---

    /**
     * Điều phối việc lưu các loại file khác nhau cho một dự án.
     */
    private void handleFileUpload(Project project, Account uploader, ProjectRequestDTO dto) {
        saveOrUpdateFile(project, uploader, dto.getPitchDeck(), FileType.PITCH_DECK);
        saveOrUpdateFile(project, uploader, dto.getPitchVideo(), FileType.PITCH_VIDEO);
        saveOrUpdateFile(project, uploader, dto.getBusinessPlan(), FileType.BUSINESS_PLAN);
        saveOrUpdateFile(project, uploader, dto.getFinancialProjection(), FileType.FINANCIAL_PROJECTION);
    }

    /**
     * Logic chính để lưu hoặc cập nhật một file cụ thể.
     */

    private void saveOrUpdateFile(Project project, Account uploader, MultipartFile file, FileType type) {
        if (file == null || file.isEmpty()) return;

        project.getFiles().stream()
                .filter(pf -> pf.getType() == type)
                .findFirst()
                .ifPresent(existingPf -> {
                    fileStorageService.deletePhysicalFile(existingPf.getFileStorage().getFilePath());
                    projectFileRepository.delete(existingPf);
                });

        // 1. Lưu file vào "kho" và lấy về bản ghi FileStorage
        FileStorage storedFile = fileStorageService.storeFile(file, PROJECT_FILES_SUBDIR, uploader);

        // 2. Tạo "nhãn dán" ProjectFile để nối Project và FileStorage
        ProjectFile projectFile = ProjectFile.builder()
                .project(project)
                .fileStorage(storedFile)
                .type(type)
                .build();
        projectFileRepository.save(projectFile);
    }

    /**
     * Ánh xạ từ Project entity sang ProjectResponseDTO.
     */
    private ProjectResponseDTO mapToResponse(Project project) {
        return ProjectResponseDTO.builder()
                .id(project.getId())
                .projectName(project.getProjectName())
                .category(project.getCategory())
                .customCategory(project.getCustomCategory())
                .fundingStage(project.getFundingStage())
                .fundingRange(project.getFundingRange())
                .fundingAmount(project.getFundingAmount())
                .teamSize(project.getTeamSize())
                .location(project.getLocation())
                .website(project.getWebsite())
                .description(project.getDescription())
                .status(project.getStatus())
                .adminNote(project.getAdminNote())
                .startupId(project.getStartup().getId())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .files(project.getFiles().stream()
                        .map(pf -> new ProjectFileDTO(pf.getType(), pf.getFileStorage().getFilePath()))
                        .collect(Collectors.toList()))
                .build();
    }
}