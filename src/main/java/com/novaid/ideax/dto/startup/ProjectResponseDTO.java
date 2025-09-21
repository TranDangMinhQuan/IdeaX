package com.novaid.ideax.dto.startup;


import com.novaid.ideax.enums.Category;
import com.novaid.ideax.enums.FundingRange;
import com.novaid.ideax.enums.FundingStage;
import com.novaid.ideax.enums.ProjectStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponseDTO {
    private Long id;
    private String projectName;
    private Category category;
    private String customCategory;
    private FundingStage fundingStage;
    private FundingRange fundingRange;
    private Integer teamSize;
    private String location;
    private String website;
    private String description;
    private ProjectStatus status;
    private String adminNote;
    private Long startupId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<ProjectFileDTO> files;
}
