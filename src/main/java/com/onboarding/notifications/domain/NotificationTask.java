package com.onboarding.notifications.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "notification_tasks",
        uniqueConstraints = @UniqueConstraint(name = "uk_task_dedupe", columnNames = {"tenantId", "dedupeKey"})
)
public class NotificationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    @Column(nullable = false, length = 64)
    private String tenantId;

    @Column(nullable = false)
    private Long employeeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowType workflowType;

    @Column(nullable = false)
    private OffsetDateTime scheduledTime;

    @Column(nullable = false, length = 256)
    private String dedupeKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    protected NotificationTask() {}

    public NotificationTask(String tenantId, Long employeeId, WorkflowType workflowType, OffsetDateTime scheduledTime,
                            String dedupeKey, TaskStatus status) {
        this.tenantId = tenantId;
        this.employeeId = employeeId;
        this.workflowType = workflowType;
        this.scheduledTime = scheduledTime;
        this.dedupeKey = dedupeKey;
        this.status = status;
    }

    public Long getTaskId() { return taskId; }
    public String getTenantId() { return tenantId; }
    public Long getEmployeeId() { return employeeId; }
    public WorkflowType getWorkflowType() { return workflowType; }
    public OffsetDateTime getScheduledTime() { return scheduledTime; }
    public String getDedupeKey() { return dedupeKey; }
    public TaskStatus getStatus() { return status; }

    public void markSent() { this.status = TaskStatus.SENT; }
    public void markFailed() { this.status = TaskStatus.FAILED; }
}