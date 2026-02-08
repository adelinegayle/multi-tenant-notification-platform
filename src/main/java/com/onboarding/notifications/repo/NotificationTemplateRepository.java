package com.onboarding.notifications.repo;

import com.onboarding.notifications.domain.NotificationTemplate;
import com.onboarding.notifications.domain.WorkflowType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByTenantIdAndWorkflowType(String tenantId, WorkflowType workflowType);
}