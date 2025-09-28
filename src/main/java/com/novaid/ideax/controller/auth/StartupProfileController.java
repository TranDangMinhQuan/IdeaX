package com.novaid.ideax.controller.auth;

import com.novaid.ideax.dto.account.StartupProfileUpdateDTO;
import com.novaid.ideax.entity.auth.StartupProfile;
import com.novaid.ideax.service.auth.StartupProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/startup/profile")
@SecurityRequirement(name = "api")
public class StartupProfileController {

    @Autowired
    private StartupProfileService startupProfileService;

    // GET profile theo accountId
    @GetMapping("/{accountId}")
    @PreAuthorize("hasAnyRole('ADMIN','START_UP')")
    public ResponseEntity<StartupProfile> getProfile(@PathVariable Long accountId) {
        return ResponseEntity.ok(startupProfileService.getProfile(accountId));
    }

    // UPDATE profile theo accountId
    @PutMapping("/{accountId}")
    @PreAuthorize("hasAnyRole('ADMIN','START_UP')")
    public ResponseEntity<StartupProfile> updateProfile(@PathVariable Long accountId,
                                                        @RequestBody StartupProfileUpdateDTO dto) {
        return ResponseEntity.ok(startupProfileService.updateProfile(accountId, dto));
    }
}
