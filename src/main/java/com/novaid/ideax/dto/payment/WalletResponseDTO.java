package com.novaid.ideax.dto.payment;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletResponseDTO {
    private Long accountId;
    private String email;
    private BigDecimal balance;
}