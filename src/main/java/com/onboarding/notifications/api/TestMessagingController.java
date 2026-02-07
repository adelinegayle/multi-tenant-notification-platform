package com.onboarding.notifications.api;

import com.onboarding.notifications.messaging.TestEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestMessagingController {

    private final TestEventPublisher publisher;

    public TestMessagingController(TestEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping("/test/publish")
    public String publish(@RequestParam String msg) {
        publisher.publish(msg);
        return "Published: " + msg;
    }
}