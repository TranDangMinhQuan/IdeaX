package com.novaid.ideax.dto.register;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRegisterDTO {
    private String email;
    private String password;
    private String confirmPassword;
}
