package com.novaid.ideax.dto.account;

import com.novaid.ideax.enums.Role;
import com.novaid.ideax.enums.Status;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountUpdateDTO {
    private String email; // chỉ khi cần đổi
    private String password; // đổi mật khẩu
    private Status status;   // admin/staff có thể chỉnh
    private Role role;
}