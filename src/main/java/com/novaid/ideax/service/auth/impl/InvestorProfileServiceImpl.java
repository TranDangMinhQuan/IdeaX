package com.novaid.ideax.service.auth.impl;

import com.novaid.ideax.dto.account.InvestorProfileUpdateDTO;
import com.novaid.ideax.entity.auth.InvestorProfile;
import com.novaid.ideax.repository.auth.AccountRepository;
import com.novaid.ideax.repository.auth.InvestorProfileRepository;
import com.novaid.ideax.service.auth.InvestorProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvestorProfileServiceImpl implements InvestorProfileService {

    private final InvestorProfileRepository investorProfileRepository;
    private final AccountRepository accountRepository;

    @Override
    public InvestorProfile updateProfile(Long accountId, InvestorProfileUpdateDTO dto) {
        InvestorProfile profile = investorProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Investor profile not found"));

        // Update fields
        profile.setFullName(dto.getFullName());
        profile.setPhoneNumber(dto.getPhoneNumber());
        profile.setCountry(dto.getCountry());
        profile.setLinkedInUrl(dto.getLinkedInUrl());
        profile.setTwoFactorEnabled(dto.getTwoFactorEnabled());

        return investorProfileRepository.save(profile);
    }

    @Override
    public InvestorProfile getProfile(Long accountId) {
        return investorProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Investor profile not found"));
    }
}
