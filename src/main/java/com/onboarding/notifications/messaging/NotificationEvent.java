package com.onboarding.notifications.messaging;

import com.onboarding.notifications.domain.WorkflowType;

import java.time.OffsetDateTime;

public class NotificationEvent {
    private String eventId;
    private String tenantId;
    private Long employeeId;
    private WorkflowType workflowType;
    private OffsetDateTime scheduledSendTime;
    private String dedupeKey;

    public NotificationEvent() {}

    public NotificationEvent(String eventId, String tenantId, Long employeeId,
                             WorkflowType workflowType, OffsetDateTime scheduledSendTime, String dedupeKey) {
        this.eventId = eventId;
        this.tenantId = tenantId;
        this.employeeId = employeeId;
        this.workflowType = workflowType;
        this.scheduledSendTime = scheduledSendTime;
        this.dedupeKey = dedupeKey;
    }

    public String getEventId() { return eventId; }
    public String getTenantId() { return tenantId; }
    public Long getEmployeeId() { return employeeId; }
    public WorkflowType getWorkflowType() { return workflowType; }
    public OffsetDateTime getScheduledSendTime() { return scheduledSendTime; }
    public String getDedupeKey() { return dedupeKey; }

    public void setEventId(String eventId) { this.eventId = eventId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public void setWorkflowType(WorkflowType workflowType) { this.workflowType = workflowType; }
    public void setScheduledSendTime(OffsetDateTime scheduledSendTime) { this.scheduledSendTime = scheduledSendTime; }
    public void setDedupeKey(String dedupeKey) { this.dedupeKey = dedupeKey; }
}