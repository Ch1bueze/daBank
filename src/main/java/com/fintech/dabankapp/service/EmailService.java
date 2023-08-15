package com.fintech.dabankapp.service;

import com.fintech.dabankapp.dto.EmailDetails;

public interface EmailService {
    void sendMail(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails emailDetails);
}
