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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;
    private final AccountRepository accountRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

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
                .fundingAmount(dto.getFundingAmount())
                .teamSize(dto.getTeamSize())
                .location(dto.getLocation())
                .website(dto.getWebsite())
                .description(dto.getDescription())
                .status(Optional.ofNullable(dto.getStatus()).orElse(ProjectStatus.DRAFT))
                .startup(startup)
                .build();

        Project saved = projectRepository.save(project);
        handleFileUpload(saved, dto, false);
        return mapToResponse(saved);
    }

    @Override
    public ProjectResponseDTO updateProject(Long accountId, Long projectId, ProjectRequestDTO dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getStartup().getId().equals(accountId)) {
            throw new RuntimeException("Unauthorized to update this project");
        }

        // update non-null fields
        if (dto.getProjectName() != null) project.setProjectName(dto.getProjectName());
        if (dto.getCategory() != null) project.setCategory(dto.getCategory());
        if (dto.getCustomCategory() != null) project.setCustomCategory(dto.getCustomCategory());
        if (dto.getFundingStage() != null) project.setFundingStage(dto.getFundingStage());
        if (dto.getFundingRange() != null) project.setFundingRange(dto.getFundingRange());
        if (dto.getTeamSize() != null) project.setTeamSize(dto.getTeamSize());
        if (dto.getLocation() != null) project.setLocation(dto.getLocation());
        if (dto.getWebsite() != null) project.setWebsite(dto.getWebsite());
        if (dto.getDescription() != null) project.setDescription(dto.getDescription());
        if (dto.getStatus() != null) project.setStatus(dto.getStatus());

        handleFileUpload(project, dto, true);
        return mapToResponse(project);
    }

    @Override
    public void deleteProject(Long accountId, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        if (!project.getStartup().getId().equals(accountId)) {
            throw new RuntimeException("Unauthorized");
        }

        // Xóa file vật lý
        project.getFiles().forEach(f -> deleteOldFileIfExists(f.getFileUrl()));
        projectRepository.delete(project);
    }

    @Override
    public ProjectResponseDTO getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    @Override
    public List<ProjectResponseDTO> getMyProjects(Long accountId) {
        return projectRepository.findByStartup_Id(accountId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

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

    // === FILE HANDLING ===
    private void handleFileUpload(Project project, ProjectRequestDTO dto, boolean isUpdate) {
        saveFile(project, dto.getPitchDeck(), FileType.PITCH_DECK, isUpdate);
        saveFile(project, dto.getPitchVideo(), FileType.PITCH_VIDEO, isUpdate);
        saveFile(project, dto.getBusinessPlan(), FileType.BUSINESS_PLAN, isUpdate);
        saveFile(project, dto.getFinancialProjection(), FileType.FINANCIAL_PROJECTION, isUpdate);
    }

    private void saveFile(Project project, MultipartFile file, FileType type, boolean isUpdate) {
        if (file == null || file.isEmpty()) return;

        // Nếu update, xóa file cũ cùng loại
        if (isUpdate) {
            project.getFiles().stream()
                    .filter(f -> f.getFileType() == type)
                    .findFirst()
                    .ifPresent(existing -> {
                        deleteOldFileIfExists(existing.getFileUrl());
                        projectFileRepository.delete(existing);
                    });
        }

        // tạo thư mục
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            String ext = Optional.ofNullable(file.getOriginalFilename())
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(f.lastIndexOf(".")))
                    .orElse("");
            String newName = UUID.randomUUID() + ext;
            Path path = uploadPath.resolve(newName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            String url = "/uploads/" + newName;
            ProjectFile pf = ProjectFile.builder()
                    .project(project)
                    .fileType(type)
                    .fileUrl(url)
                    .build();
            projectFileRepository.save(pf);
            project.getFiles().add(pf);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage());
        }
    }

    private void deleteOldFileIfExists(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;
        try {
            Path path = Paths.get(uploadDir).resolve(fileUrl.replace("/uploads/", ""));
            if (Files.exists(path)) Files.delete(path);
        } catch (IOException e) {
            System.out.println("⚠️ Failed to delete file: " + e.getMessage());
        }
    }
    @Override
    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

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
                        .map(f -> new ProjectFileDTO(f.getFileType(), f.getFileUrl()))
                        .collect(Collectors.toList()))
                .build();
    }
}
