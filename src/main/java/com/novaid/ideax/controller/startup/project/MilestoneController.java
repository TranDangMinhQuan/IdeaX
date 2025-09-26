package com.novaid.ideax.controller.startup.project;


import com.novaid.ideax.dto.startup.project.MilestoneRequestDTO;
import com.novaid.ideax.dto.startup.project.MilestoneResponseDTO;
import com.novaid.ideax.service.startup.project.MilestoneService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/milestones")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class MilestoneController {

    private final MilestoneService milestoneService;

    // STARTUP tạo milestone cho dự án của họ
    @PreAuthorize("hasRole('STARTUP')")
    @PostMapping("/project/{projectId}")
    public ResponseEntity<MilestoneResponseDTO> createMilestone(
            @PathVariable Long projectId,
            @Valid @RequestBody MilestoneRequestDTO dto) {
        return ResponseEntity.ok(milestoneService.createMilestone(projectId, dto));
    }

    // STARTUP update milestone
    @PreAuthorize("hasRole('STARTUP')")
    @PutMapping("/{id}")
    public ResponseEntity<MilestoneResponseDTO> updateMilestone(
            @PathVariable Long id,
            @Valid @RequestBody MilestoneRequestDTO dto) {
        return ResponseEntity.ok(milestoneService.updateMilestone(id, dto));
    }

    // STARTUP delete milestone
    @PreAuthorize("hasRole('STARTUP')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMilestone(@PathVariable Long id) {
        milestoneService.deleteMilestone(id);
        return ResponseEntity.noContent().build();
    }

    // STARTUP và INVESTOR đều xem được milestones theo project
    @PreAuthorize("hasAnyRole('STARTUP','INVESTOR')")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<MilestoneResponseDTO>> getMilestonesByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(milestoneService.getMilestonesByProject(projectId));
    }

    // STARTUP và INVESTOR đều xem chi tiết milestone
    @PreAuthorize("hasAnyRole('STARTUP','INVESTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<MilestoneResponseDTO> getMilestoneById(@PathVariable Long id) {
        return ResponseEntity.ok(milestoneService.getMilestoneById(id));
    }
}