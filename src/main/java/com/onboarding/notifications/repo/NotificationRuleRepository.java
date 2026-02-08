package com.onboarding.notifications.repo;

import com.onboarding.notifications.domain.NotificationRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRuleRepository extends JpaRepository<NotificationRule, Long> {
    List<NotificationRule> findByTenantId(String tenantId);
}