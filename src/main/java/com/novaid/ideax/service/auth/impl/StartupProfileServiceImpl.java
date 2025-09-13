package com.novaid.ideax.service.auth.impl;

import com.novaid.ideax.dto.account.StartupProfileUpdateDTO;
import com.novaid.ideax.entity.auth.StartupProfile;
import com.novaid.ideax.repository.auth.AccountRepository;
import com.novaid.ideax.repository.auth.StartupProfileRepository;
import com.novaid.ideax.service.auth.StartupProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StartupProfileServiceImpl implements StartupProfileService {

    private final StartupProfileRepository startupProfileRepository;
    private final AccountRepository accountRepository;

    @Override
    public StartupProfile updateProfile(Long accountId, StartupProfileUpdateDTO dto) {
        StartupProfile profile = startupProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Startup profile not found"));

        // Update fields
        profile.setFullName(dto.getFullName());
        profile.setPhoneNumber(dto.getPhoneNumber());
        profile.setLinkedInProfile(dto.getLinkedInProfile());
        profile.setCompanyWebsite(dto.getCompanyWebsite());
        profile.setProfilePictureUrl(dto.getProfilePictureUrl());

        profile.setStartupName(dto.getStartupName());
        profile.setIndustryCategory(dto.getIndustryCategory());
        profile.setFundingStage(dto.getFundingStage());
        profile.setLocation(dto.getLocation());
        profile.setNumberOfTeamMembers(dto.getNumberOfTeamMembers());
        profile.setAboutUs(dto.getAboutUs());

        return startupProfileRepository.save(profile);
    }

    @Override
    public StartupProfile getProfile(Long accountId) {
        return startupProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Startup profile not found"));
    }
}
