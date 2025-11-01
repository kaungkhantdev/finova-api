package com.financial.api.service.impl;

import com.financial.api.dto.request.MailRequest;
import com.financial.api.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.mail.sender.name}")
    private String senderName;

    /**
     * Sends an HTML email with optional attachments, using Thymeleaf templates.
     */
    @Override
    @Async // send email asynchronously (non-blocking)
    public void sendMail(MailRequest request) {
        try {

            Context thymleafContext = new Context();
            thymleafContext.setVariables(request.getVariables());

            String htmlContent = templateEngine.process(request.getTemplateName(), thymleafContext);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(htmlContent, true);
            helper.setFrom(from, senderName);

            mailSender.send(message);
            log.info("Email sent successfully to {}", request.getTo());

        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", request.getTo(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error while sending email: {}", e.getMessage(), e);
        }
    }
}
