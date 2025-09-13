package com.novaid.ideax.dto.register;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestorRegisterDTO {

    // Account info
    private String email;
    private String password;
    private String confirmPassword;

    // Investor profile info
    private String fullName;
    private String organization;
    private String investmentFocus;
    private String investmentRange;
    private String investmentExperience;
}
