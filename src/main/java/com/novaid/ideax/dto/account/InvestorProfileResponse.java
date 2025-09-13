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
}