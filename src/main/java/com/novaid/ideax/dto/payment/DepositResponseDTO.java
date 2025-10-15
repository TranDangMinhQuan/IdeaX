package com.novaid.ideax.dto.payment;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositResponseDTO {
    private Long transactionId; // ID giao dịch trên hệ thống của bạn
    private String paymentUrl; // URL để chuyển hướng người dùng đến trang thanh toán
}