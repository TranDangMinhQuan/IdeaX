package com.novaid.ideax.dto.investor.nda;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NdaAgreementDTO {
    private Long id;
    private boolean signed;
    private LocalDateTime signedAt;
    private Long userId;
    private Long ndaTemplateId;
}