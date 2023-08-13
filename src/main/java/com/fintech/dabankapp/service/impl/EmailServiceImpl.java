package com.fintech.dabankapp.service.impl;

import com.fintech.dabankapp.dto.EmailDetails;
import com.fintech.dabankapp.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String senderEmail;
    @Override
    public void sendMail(EmailDetails emailDetails) {
        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderEmail);
            mailMessage.setText(emailDetails.getMessageBody());
            mailMessage.setSubject(emailDetails.getSubject());
            mailMessage.setTo(emailDetails.getRecipient());

            javaMailSender.send(mailMessage);
            System.out.println("Mail sent Successfully");

        } catch (Exception e){
            throw new RuntimeException(e);
        }

    }
}
