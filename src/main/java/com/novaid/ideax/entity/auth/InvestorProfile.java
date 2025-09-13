package com.novaid.ideax.entity.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "investor_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nationalized
    @NotBlank(message = "Full name is required")
    @Size(max = 255)
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Nationalized
    @Size(max = 255)
    @Column(name = "organization", length = 255)
    private String organization;

    @Nationalized
    @Size(max = 500)
    @Column(name = "investment_focus", length = 500)
    private String investmentFocus;

    @Nationalized
    @Size(max = 255)
    @Column(name = "investment_range", length = 255)
    private String investmentRange;

    @Nationalized
    @Size(max = 100)
    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "linkedin_url", length = 255)
    private String linkedInUrl;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "two_factor_enabled")
    private Boolean twoFactorEnabled = false;

    @Column(name = "email", length = 255)
    private String email;

    @Nationalized
    @Column(name = "investment_experience", length = 1000)
    private String investmentExperience;

    // Quan hệ 1-1 với Account
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    @JsonIgnore
    private Account account;
}
