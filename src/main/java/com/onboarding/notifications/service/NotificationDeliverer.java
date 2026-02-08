package com.onboarding.notifications.service;

import com.onboarding.notifications.domain.WorkflowType;

public interface NotificationDeliverer {

    void deliver(String tenantId, Long employeeId, WorkflowType workflowType);
}
