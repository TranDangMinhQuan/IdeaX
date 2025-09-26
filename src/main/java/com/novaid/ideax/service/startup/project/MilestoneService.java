package com.novaid.ideax.service.startup.project;


import com.novaid.ideax.dto.startup.project.MilestoneRequestDTO;
import com.novaid.ideax.dto.startup.project.MilestoneResponseDTO;

import java.util.List;

public interface MilestoneService {
    MilestoneResponseDTO createMilestone(Long projectId, MilestoneRequestDTO dto);
    MilestoneResponseDTO updateMilestone(Long milestoneId, MilestoneRequestDTO dto);
    void deleteMilestone(Long milestoneId);
    List<MilestoneResponseDTO> getMilestonesByProject(Long projectId);
    MilestoneResponseDTO getMilestoneById(Long milestoneId);
}
