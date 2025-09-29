package com.novaid.ideax.service.auth.impl;

import com.novaid.ideax.dto.account.StartupProfileUpdateDTO;
import com.novaid.ideax.entity.auth.StartupProfile;
import com.novaid.ideax.repository.auth.AccountRepository;
import com.novaid.ideax.repository.auth.StartupProfileRepository;
import com.novaid.ideax.service.auth.StartupProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

    @Override
    public StartupProfile uploadProfilePicture(Long accountId, MultipartFile file, HttpServletRequest request) {
        StartupProfile profile = startupProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Startup profile not found"));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        try {
            // Simple local storage under /uploads
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
        String originalName = file.getOriginalFilename();
        // sanitize filename: replace whitespace and potentially problematic chars
        String safeOriginal = originalName == null ? "file" : originalName.replaceAll("\\s+", "_");
        String filename = "startup-" + accountId + "-" + System.currentTimeMillis() + "-" + safeOriginal;
        Path target = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), target);

        // Build a context-relative URL and let the builder encode it properly
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/uploads/")
            .path(filename)
            .toUriString();
        profile.setProfilePictureUrl(url);
            return startupProfileRepository.save(profile);
        } catch (IOException e) {
            throw new RuntimeException("Could not store file", e);
        }
    }
}
