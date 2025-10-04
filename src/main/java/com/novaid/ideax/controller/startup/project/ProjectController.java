package com.novaid.ideax.controller.startup.project;

import com.novaid.ideax.dto.startup.project.ProjectRequestDTO;
import com.novaid.ideax.dto.startup.project.ProjectResponseDTO;
import com.novaid.ideax.entity.auth.Account;
import com.novaid.ideax.service.startup.project.ProjectService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/projects")
@SecurityRequirement(name = "api")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ProjectResponseDTO> createProject(
            @ModelAttribute ProjectRequestDTO dto,
            Authentication authentication) {

        Account account = (Account) authentication.getPrincipal();
        return ResponseEntity.ok(projectService.createProject(account.getId(), dto));
    }


    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ProjectResponseDTO> updateProject(
            @PathVariable Long id,
            @ModelAttribute ProjectRequestDTO dto,
            Authentication authentication) {

        Account account = (Account) authentication.getPrincipal();
        return ResponseEntity.ok(projectService.updateProject(account.getId(), id, dto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            @PathVariable Long id,
            Authentication authentication) {

        Account account = (Account) authentication.getPrincipal();
        projectService.deleteProject(account.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ProjectResponseDTO>> getMyProjects(Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        return ResponseEntity.ok(projectService.getMyProjects(account.getId()));
    }

    // --- Admin APIs ---
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectResponseDTO> approveProject(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.approveProject(id));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProjectResponseDTO> rejectProject(
            @PathVariable Long id,
            @RequestParam String note) {
        return ResponseEntity.ok(projectService.rejectProject(id, note));
    }
}
