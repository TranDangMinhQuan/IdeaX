package com.novaid.ideax.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositRequestDTO {

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    // Ví dụ: "VNPAY", "MOMO". Dùng để chọn cổng thanh toán phù hợp
    private String paymentMethod;
}