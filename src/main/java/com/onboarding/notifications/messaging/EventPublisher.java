package com.onboarding.notifications.messaging;

public interface EventPublisher {

    void publish(NotificationEvent event);
}
