package com.novaid.ideax.dto.account;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestorProfileUpdateDTO {
    private String fullName;
    private String phoneNumber;
    private String country;
    private String linkedInUrl;

    private Boolean twoFactorEnabled;
}
