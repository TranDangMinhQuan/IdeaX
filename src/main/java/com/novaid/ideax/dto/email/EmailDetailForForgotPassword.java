package com.novaid.ideax.dto.email;

import com.novaid.ideax.entity.auth.Account;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailDetailForForgotPassword {
    private Account account;
    private String subject;
    private String link;
}
