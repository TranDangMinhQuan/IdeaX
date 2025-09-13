package com.novaid.ideax.service.auth;

import com.novaid.ideax.dto.email.EmailDetailForForgotPassword;
import com.novaid.ideax.dto.email.EmailDetailForRegister;


import java.time.LocalDate;

public interface EmailService {
    void sendRegisterSuccessEmail(EmailDetailForRegister emailDetail);
    void sendResetPasswordEmail(EmailDetailForForgotPassword emailDetailForForgotPassword);

}
