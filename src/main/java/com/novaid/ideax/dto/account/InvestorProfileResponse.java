package com.novaid.ideax.dto.account;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestorProfileResponse {
    private String fullName;
    private String organization;
    private String investmentFocus;
    private String investmentRange;
    private String investmentExperience;
    // New fields exposed to clients
    private String country;
    private String phoneNumber;
    private String linkedInUrl;
    private Boolean twoFactorEnabled;
}