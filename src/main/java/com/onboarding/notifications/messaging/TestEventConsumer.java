package com.onboarding.notifications.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TestEventConsumer {

    @RabbitListener(queues = "${app.messaging.queue}")
    public void handleMessage(String message) {
        System.out.println("[RabbitMQ Consumer] Received: " + message);
    }
}