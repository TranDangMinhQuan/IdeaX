package com.novaid.ideax.dto.account;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartupProfileUpdateDTO {
    private String fullName;
    private String phoneNumber;
    private String linkedInProfile;
    private String companyWebsite;
    private MultipartFile profilePictureUrl;
    private String startupName;
    private String industryCategory;
    private String fundingStage;
    private String location;
    private Integer numberOfTeamMembers;
    private String aboutUs;
}
