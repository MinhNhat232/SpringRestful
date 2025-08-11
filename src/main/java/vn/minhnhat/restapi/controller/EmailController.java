package vn.minhnhat.restapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.minhnhat.restapi.service.EmailService;
import vn.minhnhat.restapi.service.SubscriberService;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final EmailService emailService;
    private final SubscriberService subscriberService;

    public EmailController(EmailService emailService, SubscriberService subscriberService) {
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    public String sendEmail() {
        this.subscriberService.sendSubscribersEmailJobs();
        return "ok";
    }

}
