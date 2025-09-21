package com.novaid.ideax.service.startup;


import com.novaid.ideax.dto.startup.MilestoneRequestDTO;
import com.novaid.ideax.dto.startup.MilestoneResponseDTO;

import java.util.List;

public interface MilestoneService {
    MilestoneResponseDTO createMilestone(Long projectId, MilestoneRequestDTO dto);
    MilestoneResponseDTO updateMilestone(Long milestoneId, MilestoneRequestDTO dto);
    void deleteMilestone(Long milestoneId);
    List<MilestoneResponseDTO> getMilestonesByProject(Long projectId);
    MilestoneResponseDTO getMilestoneById(Long milestoneId);
}
