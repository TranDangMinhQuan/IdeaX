package com.novaid.ideax.service.project.impl;

import com.novaid.ideax.dto.project.MilestoneRequestDTO;
import com.novaid.ideax.dto.project.MilestoneResponseDTO;
import com.novaid.ideax.entity.project.Milestone;
import com.novaid.ideax.entity.project.Project;
import com.novaid.ideax.repository.project.MilestoneRepository;
import com.novaid.ideax.repository.project.ProjectRepository;
import com.novaid.ideax.service.project.MilestoneService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MilestoneServiceImpl implements MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;

    private MilestoneResponseDTO mapToResponse(Milestone milestone) {
        return MilestoneResponseDTO.builder()
                .id(milestone.getId())
                .title(milestone.getTitle())
                .description(milestone.getDescription())
                .dueDate(milestone.getDueDate())
                .status(milestone.getStatus())
                .createdAt(milestone.getCreatedAt())
                .updatedAt(milestone.getUpdatedAt())
                .build();
    }

    @Override
    public MilestoneResponseDTO createMilestone(Long projectId, MilestoneRequestDTO dto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        Milestone milestone = Milestone.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .dueDate(dto.getDueDate())
                .status(dto.getStatus())
                .project(project)
                .build();

        Milestone saved = milestoneRepository.save(milestone);
        return mapToResponse(saved);
    }

    @Override
    public MilestoneResponseDTO updateMilestone(Long milestoneId, MilestoneRequestDTO dto) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new EntityNotFoundException("Milestone not found"));

        milestone.setTitle(dto.getTitle());
        milestone.setDescription(dto.getDescription());
        milestone.setDueDate(dto.getDueDate());
        milestone.setStatus(dto.getStatus());

        Milestone updated = milestoneRepository.save(milestone);
        return mapToResponse(updated);
    }

    @Override
    public void deleteMilestone(Long milestoneId) {
        if (!milestoneRepository.existsById(milestoneId)) {
            throw new EntityNotFoundException("Milestone not found");
        }
        milestoneRepository.deleteById(milestoneId);
    }

    @Override
    public List<MilestoneResponseDTO> getMilestonesByProject(Long projectId) {
        return milestoneRepository.findByProjectId(projectId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MilestoneResponseDTO getMilestoneById(Long milestoneId) {
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new EntityNotFoundException("Milestone not found"));
        return mapToResponse(milestone);
    }
}
