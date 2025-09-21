package com.novaid.ideax.dto.startup;

import com.novaid.ideax.enums.Category;
import com.novaid.ideax.enums.FundingRange;
import com.novaid.ideax.enums.FundingStage;
import com.novaid.ideax.enums.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Schema(description = "Ngành tùy chỉnh (nếu không có trong enum)", example = "GreenTech")
    private String customCategory;

    @Schema(description = "Giai đoạn gọi vốn (dropdown)", example = "IDEA", implementation = FundingStage.class)
    private FundingStage fundingStage;

    @Schema(description = "Khoảng vốn cần (dropdown)", example = "UNDER_50K", implementation = FundingRange.class)
    private FundingRange fundingRange;

    @Schema(description = "Số lượng thành viên", example = "5")
    private int teamSize;

    @Schema(description = "Địa điểm", example = "Hà Nội, Việt Nam")
    private String location;

    @Schema(description = "Website", example = "https://ideax.vn")
    private String website;

    @Schema(description = "Mô tả dự án", example = "Nền tảng kết nối startup và nhà đầu tư")
    private String description;

    @Schema(description = "Trạng thái dự án (dropdown)", example = "DRAFT", implementation = ProjectStatus.class)
    private ProjectStatus status;

    // --- Các file upload ---
    @Schema(description = "Pitch Deck file (PDF/PowerPoint)")
    private MultipartFile pitchDeck;

    @Schema(description = "Pitch Video file (MP4)")
    private MultipartFile pitchVideo;

    @Schema(description = "Business Plan file (DOC/PDF)")
    private MultipartFile businessPlan;

    @Schema(description = "Financial Projection file (Excel/PDF)")
    private MultipartFile financialProjection;
}
