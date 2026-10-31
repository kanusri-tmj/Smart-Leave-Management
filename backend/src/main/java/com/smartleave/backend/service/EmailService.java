package com.smartleave.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger =
            LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(
            String recipient,
            String subject,
            String message) {

        if (!mailEnabled) {
            logger.info("Email notifications are disabled.");
            return;
        }

        if (recipient == null || recipient.isBlank()) {
            logger.warn("Email not sent because recipient is missing.");
            return;
        }

        try {
            SimpleMailMessage email = new SimpleMailMessage();

            if (fromEmail != null && !fromEmail.isBlank()) {
                email.setFrom(fromEmail);
            }

            email.setTo(recipient);
            email.setSubject(subject);
            email.setText(message);

            mailSender.send(email);

            logger.info("Email notification sent successfully.");

        } catch (MailException exception) {
            logger.error("Unable to send email notification: {}",
                    exception.getMessage());
        }
    }
}
