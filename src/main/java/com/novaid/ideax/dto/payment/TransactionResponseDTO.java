package com.novaid.ideax.dto.payment;


import com.novaid.ideax.enums.TransactionStatus;
import com.novaid.ideax.enums.TransactionType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {
    private Long id;
    private BigDecimal amount; // Số tiền dương hoặc âm
    private TransactionType type;
    private TransactionStatus status;
    private Long paymentId; // ID của khoản thanh toán dự án liên quan (nếu có)
    private LocalDateTime createdAt;
}