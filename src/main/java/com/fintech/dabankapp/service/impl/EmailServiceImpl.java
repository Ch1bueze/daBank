package com.fintech.dabankapp.service.impl;

import com.fintech.dabankapp.dto.EmailDetails;
import com.fintech.dabankapp.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;
@Slf4j
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

    @Override
    public void sendEmailWithAttachment(EmailDetails emailDetails) {

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper;
            try{
                mimeMessageHelper = new MimeMessageHelper(message, true);
                mimeMessageHelper.setFrom(senderEmail);
                mimeMessageHelper.setTo(emailDetails.getRecipient());
                mimeMessageHelper.setSubject(emailDetails.getSubject());
                mimeMessageHelper.setText(emailDetails.getMessageBody());

                FileSystemResource file = new FileSystemResource(new File(emailDetails.getAttachment()));
                mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
                javaMailSender.send(message);

                log.info(file.getFilename() + " has been sent to user with email" + emailDetails.getRecipient());

            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }

    }
}
