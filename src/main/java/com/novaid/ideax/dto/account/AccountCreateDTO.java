package com.novaid.ideax.dto.account;

import com.novaid.ideax.enums.Role;
import com.novaid.ideax.enums.Status;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCreateDTO {
    private String email;
    private String password;
    private Role role; // STARTUP / INVESTOR / ADMIN
    private Status status; // ACTIVE / INACTIVE
}
