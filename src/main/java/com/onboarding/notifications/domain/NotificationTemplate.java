package com.onboarding.notifications.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "notification_templates")
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long templateId;

    @Column(nullable = false, length = 64)
    private String tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowType workflowType;

    @Column(nullable = false)
    private String channel; // "EMAIL" for v1

    @Column(nullable = false)
    private String subjectTemplate;

    @Lob
    @Column(nullable = false)
    private String bodyTemplate;

    protected NotificationTemplate() {}

    public NotificationTemplate(String tenantId, WorkflowType workflowType, String channel,
                                String subjectTemplate, String bodyTemplate) {
        this.tenantId = tenantId;
        this.workflowType = workflowType;
        this.channel = channel;
        this.subjectTemplate = subjectTemplate;
        this.bodyTemplate = bodyTemplate;
    }

    public Long getTemplateId() { return templateId; }
    public String getTenantId() { return tenantId; }
    public WorkflowType getWorkflowType() { return workflowType; }
    public String getChannel() { return channel; }
    public String getSubjectTemplate() { return subjectTemplate; }
    public String getBodyTemplate() { return bodyTemplate; }
}