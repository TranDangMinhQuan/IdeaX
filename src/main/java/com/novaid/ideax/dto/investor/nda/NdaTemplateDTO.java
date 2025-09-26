package com.novaid.ideax.dto.investor.nda;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NdaTemplateDTO {
    private Long id;
    private String fileUrl;
    private LocalDateTime uploadedAt;
}