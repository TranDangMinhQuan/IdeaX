package com.novaid.ideax.service.startup.project;

import com.novaid.ideax.dto.startup.project.ProjectRequestDTO;
import com.novaid.ideax.dto.startup.project.ProjectResponseDTO;

import java.util.List;

public interface ProjectService {
    ProjectResponseDTO createProject(Long accountId, ProjectRequestDTO dto);
    ProjectResponseDTO updateProject(Long accountId, Long projectId, ProjectRequestDTO dto);
    void deleteProject(Long accountId, Long projectId);
    ProjectResponseDTO getProjectById(Long projectId);
    List<ProjectResponseDTO> getMyProjects(Long accountId);
    List<ProjectResponseDTO> getAllProjects();
    // Admin
    ProjectResponseDTO approveProject(Long projectId);
    ProjectResponseDTO rejectProject(Long projectId, String note);
}
