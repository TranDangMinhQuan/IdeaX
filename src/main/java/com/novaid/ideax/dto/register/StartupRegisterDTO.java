package com.novaid.ideax.dto.register;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartupRegisterDTO {

    // Account info
    private String email;
    private String password;
    private String confirmPassword;

    // Startup profile info
    private String fullName;
    private String startupName;
    private String companyWebsite;
    private String companyLogo;
    private String aboutUs;
}
