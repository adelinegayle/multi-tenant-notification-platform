package com.onboarding.notifications.service;

import com.onboarding.notifications.domain.WorkflowType;
import org.springframework.stereotype.Component;

@Component
public class LoggingNotificationChannel implements NotificationChannel {

    @Override
    public void send(String tenantId, String employeeEmail, WorkflowType workflowType, String subject, String body) {
        System.out.println("[Delivery] tenant=" + tenantId +
                " employee=" + employeeEmail +
                " workflow=" + workflowType +
                " subject=\"" + subject + "\" body=\"" + body + "\"");
    }
}
