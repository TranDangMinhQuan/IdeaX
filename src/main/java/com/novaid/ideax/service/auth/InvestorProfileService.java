package com.novaid.ideax.service.auth;

import com.novaid.ideax.dto.account.InvestorProfileUpdateDTO;
import com.novaid.ideax.entity.auth.InvestorProfile;

public interface InvestorProfileService {
    InvestorProfile updateProfile(Long accountId, InvestorProfileUpdateDTO dto);
    InvestorProfile getProfile(Long accountId);
}
