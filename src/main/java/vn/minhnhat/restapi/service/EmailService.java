package vn.minhnhat.restapi.service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import vn.minhnhat.restapi.domain.Job;
import vn.minhnhat.restapi.repository.JobRepository;

@Service
public class EmailService {
    private final MailSender mailSender;
    private final JavaMailSender javaMailSender;
    private final JobRepository jobRepository;
    private final SpringTemplateEngine templateEngine;

    public EmailService(MailSender mailSender, JavaMailSender javaMailSender, JobRepository jobRepository,
            SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.javaMailSender = javaMailSender;
        this.jobRepository = jobRepository;
        this.templateEngine = templateEngine;
    }

    public void sendSimpleEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("minhkr2302@gmail.com");
        message.setSubject("Test Email");
        message.setText("This is a test email.");
        this.mailSender.send(message);

    }

    public void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        MimeMessage message = this.javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, isMultipart, StandardCharsets.UTF_8.name());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, isHtml);
            this.javaMailSender.send(message);
        } catch (MailException | MessagingException e) {
            System.out.println("Error sending email: " + e);
        }
    }

    @Async
    public void sendEmailFromTemplateSync(String to, String subject, String templateName, String userName,
            Object value) {
        Context context = new Context();
        context.setVariable("name", userName);
        context.setVariable("jobs", value);

        String content = templateEngine.process(templateName, context);
        this.sendEmailSync(to, subject, content, false, true);
    }
}
