package com.novaid.ideax.service.startup;

import com.novaid.ideax.dto.startup.ProjectRequestDTO;
import com.novaid.ideax.dto.startup.ProjectResponseDTO;

import java.util.List;

public interface ProjectService {
    ProjectResponseDTO createProject(Long accountId, ProjectRequestDTO dto);
    ProjectResponseDTO updateProject(Long accountId, Long projectId, ProjectRequestDTO dto);
    void deleteProject(Long accountId, Long projectId);
    ProjectResponseDTO getProjectById(Long projectId);
    List<ProjectResponseDTO> getMyProjects(Long accountId);

    // --- Admin ---
    ProjectResponseDTO approveProject(Long projectId);
    ProjectResponseDTO rejectProject(Long projectId, String note);
}
