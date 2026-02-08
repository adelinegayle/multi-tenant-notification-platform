package com.onboarding.notifications.service;

import com.onboarding.notifications.messaging.NotificationEvent;

public interface NotificationEventProcessor {

    void process(NotificationEvent event);
}
