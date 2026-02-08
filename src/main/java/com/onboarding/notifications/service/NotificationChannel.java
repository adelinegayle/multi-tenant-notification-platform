package com.onboarding.notifications.service;

import com.onboarding.notifications.domain.WorkflowType;

public interface NotificationChannel {

    void send(String tenantId, String employeeEmail, WorkflowType workflowType, String subject, String body);
}
