package com.example.vocabulary.service;

import com.bastiaanjansen.otp.TOTPGenerator;
import com.example.vocabulary.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final TOTPGenerator totpGenerator;

    public void sendMail(Account account) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("learn.vocabulary.app");
        mailMessage.setTo(account.getEmail());
        mailMessage.setSubject("Reset Password");
        mailMessage.setText("Your opt code is %n %s".formatted(totpGenerator.now()));
        mailSender.send(mailMessage);
    }
}
