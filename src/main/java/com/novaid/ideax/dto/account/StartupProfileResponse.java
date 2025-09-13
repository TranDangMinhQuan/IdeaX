package com.novaid.ideax.dto.account;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartupProfileResponse {
    private String fullName;
    private String startupName;
    private String companyWebsite;
    private String companyLogo;
    private String aboutUs;
}