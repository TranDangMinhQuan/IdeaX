package com.novaid.ideax.dto.payment;

import com.novaid.ideax.enums.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {
    private Long id;
    private Long projectId;
    private String projectName;
    private Long payerId;
    private String payerName;
    private Long recipientId;
    private String recipientName;
    private BigDecimal amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
}
