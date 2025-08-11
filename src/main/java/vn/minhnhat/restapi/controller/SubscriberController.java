package vn.minhnhat.restapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.minhnhat.restapi.domain.Subscriber;
import vn.minhnhat.restapi.service.SubscriberService;
import vn.minhnhat.restapi.util.error.IdInvalidException;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    public ResponseEntity<Subscriber> createSubscriber(@RequestBody Subscriber subscriber) throws IdInvalidException {

        boolean exists = subscriberService.existsByEmail(subscriber.getEmail());
        if (exists) {
            throw new IdInvalidException("Email already exists");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(subscriber));
    }

    @PutMapping("/subscribers")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subscriber) throws IdInvalidException {
        Subscriber currentSubscriber = this.subscriberService.findById(subscriber.getId());
        if (currentSubscriber == null) {
            throw new IdInvalidException("Subscriber not found");
        }

        currentSubscriber.setSkills(subscriber.getSkills());
        return ResponseEntity.ok().body(this.subscriberService.update(currentSubscriber, subscriber));
    }

}
