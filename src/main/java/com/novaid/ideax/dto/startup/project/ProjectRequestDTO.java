package com.novaid.ideax.dto.startup.project;

import com.novaid.ideax.enums.Category;
import com.novaid.ideax.enums.FundingRange;
import com.novaid.ideax.enums.FundingStage;
import com.novaid.ideax.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRequestDTO {

    @Schema(description = "Tên dự án", example = "IdeaX Platform")
    private String projectName;

    @Schema(description = "Ngành (dropdown)", example = "FINTECH", implementation = Category.class)
    private Category category;

    private String customCategory;
    private FundingStage fundingStage;
    private FundingRange fundingRange;
    private BigDecimal fundingAmount;
    private Double projectSize;
    private Integer teamSize;
    private String location;
    private String website;
    private String description;
    private ProjectStatus status;

    @Schema(type = "string", format = "binary", description = "Pitch Deck file")
    private MultipartFile pitchDeck;

    @Schema(type = "string", format = "binary", description = "Pitch Video file")
    private MultipartFile pitchVideo;

    @Schema(type = "string", format = "binary", description = "Business Plan file")
    private MultipartFile businessPlan;

    @Schema(type = "string", format = "binary", description = "Financial Projection file")
    private MultipartFile financialProjection;

}
