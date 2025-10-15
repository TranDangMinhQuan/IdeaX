package com.novaid.ideax.dto.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequestDTO {

    @NotBlank(message = "Reason for refund cannot be blank")
    private String reason;
}