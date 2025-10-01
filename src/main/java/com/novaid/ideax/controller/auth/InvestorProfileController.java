package com.novaid.ideax.controller.auth;


import com.novaid.ideax.dto.account.InvestorProfileUpdateDTO;
import com.novaid.ideax.entity.auth.InvestorProfile;
import com.novaid.ideax.dto.account.InvestorProfileResponse;

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
    public ResponseEntity<InvestorProfileResponse> getProfile(@PathVariable Long accountId) {
        InvestorProfile profile = investorProfileService.getProfile(accountId);
        InvestorProfileResponse dto = InvestorProfileResponse.builder()
                .fullName(profile.getFullName())
                .organization(profile.getOrganization())
                .investmentFocus(profile.getInvestmentFocus())
                .investmentRange(profile.getInvestmentRange())
                .investmentExperience(profile.getInvestmentExperience())
                .country(profile.getCountry())
                .phoneNumber(profile.getPhoneNumber())
                .linkedInUrl(profile.getLinkedInUrl())
                .twoFactorEnabled(profile.getTwoFactorEnabled())
                .build();
        return ResponseEntity.ok(dto);
    }

    // UPDATE profile theo accountId
    @PutMapping("/{accountId}")
    @PreAuthorize("hasAnyRole('ADMIN','INVESTOR')")
    public ResponseEntity<InvestorProfileResponse> updateProfile(@PathVariable Long accountId,
                                                         @RequestBody InvestorProfileUpdateDTO dto) {
        InvestorProfile profile = investorProfileService.updateProfile(accountId, dto);
        InvestorProfileResponse response = InvestorProfileResponse.builder()
                .fullName(profile.getFullName())
                .organization(profile.getOrganization())
                .investmentFocus(profile.getInvestmentFocus())
                .investmentRange(profile.getInvestmentRange())
                .investmentExperience(profile.getInvestmentExperience())
                .country(profile.getCountry())
                .phoneNumber(profile.getPhoneNumber())
                .linkedInUrl(profile.getLinkedInUrl())
                .twoFactorEnabled(profile.getTwoFactorEnabled())
                .build();
        return ResponseEntity.ok(response);
    }
}
