package com.novaid.ideax.entity.startup.project;

import com.novaid.ideax.entity.auth.Account;
import com.novaid.ideax.enums.Category;
import com.novaid.ideax.enums.FundingRange;
import com.novaid.ideax.enums.FundingStage;
import com.novaid.ideax.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String projectName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    private String customCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FundingStage fundingStage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FundingRange fundingRange;

    private Integer teamSize;

    private String location;

    private String website;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private String pitchDeckUrl;
    private String pitchVideoUrl;
    private String businessPlanUrl;
    private String financialProjectionUrl;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status; // DRAFT or PUBLISHED

    @Column(columnDefinition = "TEXT")
    private String adminNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "startup_id", nullable = false)
    private Account startup;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Milestone> milestones = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
