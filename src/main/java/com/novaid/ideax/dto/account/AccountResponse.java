package com.novaid.ideax.dto.account;
import com.novaid.ideax.enums.Role;
import com.novaid.ideax.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    private Long id;
    private String email;
    private Role role;
    private Status status;
    private String token;
    // account creation time to support frontend 'Joined' column
    private java.time.LocalDateTime createdAt;
}
