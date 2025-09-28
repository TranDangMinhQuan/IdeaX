package com.novaid.ideax.controller.auth;


import com.novaid.ideax.dto.account.InvestorProfileUpdateDTO;
import com.novaid.ideax.entity.auth.InvestorProfile;

import com.novaid.ideax.service.auth.InvestorProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/investor/profile")
@SecurityRequirement(name = "api")
public class InvestorProfileController {

    @Autowired
    private InvestorProfileService investorProfileService;

    // GET profile theo accountId
    @GetMapping("/{accountId}")
    @PreAuthorize("hasAnyRole('ADMIN','INVESTOR')")
    public ResponseEntity<InvestorProfile> getProfile(@PathVariable Long accountId) {
        return ResponseEntity.ok(investorProfileService.getProfile(accountId));
    }

    // UPDATE profile theo accountId
    @PutMapping("/{accountId}")
    @PreAuthorize("hasAnyRole('ADMIN','INVESTOR')")
    public ResponseEntity<InvestorProfile> updateProfile(@PathVariable Long accountId,
                                                         @RequestBody InvestorProfileUpdateDTO dto) {
        return ResponseEntity.ok(investorProfileService.updateProfile(accountId, dto));
    }
}
