package com.onboarding.notifications.domain;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "notification_rules")
public class NotificationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    @Column(nullable = false, length = 64)
    private String tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowType workflowType;

    @Column(nullable = false)
    private int triggerAfterDays;

    @Column(nullable = false)
    private LocalTime sendTimeOfDay;

    private Integer repeatEveryDays; // optional
    private Integer maxRepeats;      // optional

    protected NotificationRule() {}

    public NotificationRule(String tenantId, WorkflowType workflowType, int triggerAfterDays, LocalTime sendTimeOfDay,
                            Integer repeatEveryDays, Integer maxRepeats) {
        this.tenantId = tenantId;
        this.workflowType = workflowType;
        this.triggerAfterDays = triggerAfterDays;
        this.sendTimeOfDay = sendTimeOfDay;
        this.repeatEveryDays = repeatEveryDays;
        this.maxRepeats = maxRepeats;
    }

    public Long getRuleId() { return ruleId; }
    public String getTenantId() { return tenantId; }
    public WorkflowType getWorkflowType() { return workflowType; }
    public int getTriggerAfterDays() { return triggerAfterDays; }
    public LocalTime getSendTimeOfDay() { return sendTimeOfDay; }
    public Integer getRepeatEveryDays() { return repeatEveryDays; }
    public Integer getMaxRepeats() { return maxRepeats; }
}