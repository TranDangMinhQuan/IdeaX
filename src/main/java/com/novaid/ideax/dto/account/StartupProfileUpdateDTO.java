package com.novaid.ideax.dto.account;

import lombok.*;

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
    private String profilePictureUrl;

    private String startupName;
    private String industryCategory;
    private String fundingStage;
    private String location;
    private Integer numberOfTeamMembers;
    private String aboutUs;
}
