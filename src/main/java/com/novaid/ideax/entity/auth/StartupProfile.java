package com.novaid.ideax.entity.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "startup_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartupProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @NotBlank(message = "Full name is required")
    @Size(max = 255)
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "linkedin_profile", length = 255)
    private String linkedInProfile;

    @Column(name = "company_website", length = 255)
    private String companyWebsite;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    @Column(name = "company_logo", length = 500)
    private String companyLogo;

    @Nationalized
    @Column(name = "startup_name", length = 255)
    private String startupName;

    @Nationalized
    @Column(name = "industry_category", length = 255)
    private String industryCategory;

    @Nationalized
    @Column(name = "funding_stage", length = 255)
    private String fundingStage;

    @Nationalized
    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "number_of_team_members")
    private Integer numberOfTeamMembers;

    @Nationalized
    @Size(max = 2000)
    @Column(name = "about_us", length = 2000)
    private String aboutUs;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Quan hệ 1-1 với Account
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    @JsonIgnore
    private Account account;
}
