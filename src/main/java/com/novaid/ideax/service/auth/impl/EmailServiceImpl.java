package com.novaid.ideax.service.auth.impl;

import com.novaid.ideax.dto.email.EmailDetailForForgotPassword;
import com.novaid.ideax.dto.email.EmailDetailForRegister;
import com.novaid.ideax.dto.register.AccountRegisterDTO;
import com.novaid.ideax.service.auth.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendRegisterSuccessEmail(EmailDetailForRegister emailDetail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("demofortest999@gmail.com");
        message.setTo(emailDetail.getToEmail());
        message.setSubject(emailDetail.getSubject());
        String loginLink = "http://localhost:5173/login";
        String body = String.format("""
                        Xin chào!
                        
                        Tài khoản của bạn đã được đăng ký thành công với địa chỉ email: %s
                        
                        Chúng tôi rất vui khi được đồng hành cùng bạn. Hãy đăng nhập vào hệ thống để bắt đầu sử dụng dịch vụ.
                        
                        %s
                        
                        Trân trọng,
                        Hệ thống hỗ trợ
                        """,
                emailDetail.getToEmail(), loginLink);
        message.setText(body);
        mailSender.send(message);
    }

    @Override
    public void sendResetPasswordEmail(EmailDetailForForgotPassword emailDetailForForgotPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("demofortest999@gmail.com");
        message.setTo(emailDetailForForgotPassword.getAccount().getEmail());
        message.setSubject(emailDetailForForgotPassword.getSubject());

        String body = String.format("""
                Xin chào %s,
                
                Bạn đã yêu cầu đặt lại mật khẩu. Vui lòng click vào liên kết bên dưới để tiếp tục:
                
                %s
                
                Nếu bạn không yêu cầu, vui lòng bỏ qua email này.
                
                Trân trọng,
                Hệ thống hỗ trợ
                """, emailDetailForForgotPassword.getAccount().getEmail(), emailDetailForForgotPassword.getLink());

        message.setText(body);
        mailSender.send(message);
    }
}