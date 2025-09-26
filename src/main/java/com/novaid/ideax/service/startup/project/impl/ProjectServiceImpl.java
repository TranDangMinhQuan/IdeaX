package com.novaid.ideax.service.startup.project.impl;

import com.novaid.ideax.dto.startup.project.ProjectRequestDTO;
import com.novaid.ideax.dto.startup.project.ProjectResponseDTO;
import com.novaid.ideax.dto.startup.project.ProjectFileDTO;
import com.novaid.ideax.entity.auth.Account;
import com.novaid.ideax.entity.startup.project.Project;
import com.novaid.ideax.entity.startup.project.ProjectFile;
import com.novaid.ideax.enums.FileType;
import com.novaid.ideax.enums.ProjectStatus;
import com.novaid.ideax.repository.auth.AccountRepository;
import com.novaid.ideax.repository.startup.project.ProjectRepository;
import com.novaid.ideax.repository.startup.project.ProjectFileRepository;
import com.novaid.ideax.service.startup.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;
    private final AccountRepository accountRepository;

    @Override
    public ProjectResponseDTO createProject(Long accountId, ProjectRequestDTO dto) {
        Account startup = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Project project = Project.builder()
                .projectName(dto.getProjectName())
                .category(dto.getCategory())
                .customCategory(dto.getCustomCategory())
                .fundingStage(dto.getFundingStage())
                .fundingRange(dto.getFundingRange())
                .teamSize(dto.getTeamSize())
                .location(dto.getLocation())
                .website(dto.getWebsite())
                .description(dto.getDescription())
                .status(dto.getStatus() != null ? dto.getStatus() : ProjectStatus.DRAFT)
                .startup(startup)
                .build();

        // Lưu entity
        Project saved = projectRepository.save(project);

        // Xử lý file upload
        handleFileUpload(saved, dto);

        return mapToResponse(saved);
    }

    @Override
    public ProjectResponseDTO updateProject(Long accountId, Long projectId, ProjectRequestDTO dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getStartup().getId().equals(accountId)) {
            throw new RuntimeException("Unauthorized to update this project");
        }

        project.setProjectName(dto.getProjectName());
        project.setCategory(dto.getCategory());
        project.setCustomCategory(dto.getCustomCategory());
        project.setFundingStage(dto.getFundingStage());
        project.setFundingRange(dto.getFundingRange());
        project.setTeamSize(dto.getTeamSize());
        project.setLocation(dto.getLocation());
        project.setWebsite(dto.getWebsite());
        project.setDescription(dto.getDescription());
        project.setStatus(dto.getStatus());

        handleFileUpload(project, dto);

        return mapToResponse(project);
    }

    @Override
    public void deleteProject(Long accountId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getStartup().getId().equals(accountId)) {
            throw new RuntimeException("Unauthorized to delete this project");
        }

        projectRepository.delete(project);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDTO getProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return mapToResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> getMyProjects(Long accountId) {
        return projectRepository.findByStartup_Id(accountId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // --- Admin ---
    @Override
    public ProjectResponseDTO approveProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.setStatus(ProjectStatus.APPROVED);
        return mapToResponse(project);
    }

    @Override
    public ProjectResponseDTO rejectProject(Long projectId, String note) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        project.setStatus(ProjectStatus.REJECTED);
        project.setAdminNote(note);
        return mapToResponse(project);
    }

    // --- Helpers ---
    private void handleFileUpload(Project project, ProjectRequestDTO dto) {
        saveFileIfPresent(project, dto.getPitchDeck(), FileType.PITCH_DECK);
        saveFileIfPresent(project, dto.getPitchVideo(), FileType.PITCH_VIDEO);
        saveFileIfPresent(project, dto.getBusinessPlan(), FileType.BUSINESS_PLAN);
        saveFileIfPresent(project, dto.getFinancialProjection(), FileType.FINANCIAL_PROJECTION);
    }

    private void saveFileIfPresent(Project project, MultipartFile file, FileType type) {
        if (file != null && !file.isEmpty()) {
            try {
                // 👉 Ở đây bạn có thể upload lên AWS S3 / local storage, mình tạm lưu đường dẫn giả
                String fileUrl = "/uploads/" + file.getOriginalFilename();

                ProjectFile projectFile = ProjectFile.builder()
                        .project(project)
                        .fileType(type)
                        .fileUrl(fileUrl)
                        .build();

                projectFileRepository.save(projectFile);
            } catch (Exception e) {
                throw new RuntimeException("Failed to save file: " + type, e);
            }
        }
    }

    private ProjectResponseDTO mapToResponse(Project project) {
        return ProjectResponseDTO.builder()
                .id(project.getId())
                .projectName(project.getProjectName())
                .category(project.getCategory())
                .customCategory(project.getCustomCategory())
                .fundingStage(project.getFundingStage())
                .fundingRange(project.getFundingRange())
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
                        .map(f -> ProjectFileDTO.builder()
                                .fileType(f.getFileType())
                                .fileUrl(f.getFileUrl())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
