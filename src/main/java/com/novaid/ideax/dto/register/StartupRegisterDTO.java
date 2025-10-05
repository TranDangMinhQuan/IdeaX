package com.novaid.ideax.dto.register;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
    private MultipartFile companyLogo;
    private String aboutUs;
}
