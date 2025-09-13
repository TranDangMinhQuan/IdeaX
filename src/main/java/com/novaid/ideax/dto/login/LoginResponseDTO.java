package com.novaid.ideax.dto.login;


import com.novaid.ideax.dto.account.AccountResponse;

import com.novaid.ideax.dto.account.InvestorProfileResponse;
import com.novaid.ideax.dto.account.StartupProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDTO {
    private AccountResponse account;
    private StartupProfileResponse startupProfile;
    private InvestorProfileResponse investorProfile;
}