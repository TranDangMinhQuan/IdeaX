package com.novaid.ideax.dto.account;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestorProfileDTO {
    private String fullName;
    private String phoneNumber;
    private String country;
    private String linkedInUrl;

    // có thể thêm
    private Boolean twoFactorEnabled;
    private String lastLogin;
}
