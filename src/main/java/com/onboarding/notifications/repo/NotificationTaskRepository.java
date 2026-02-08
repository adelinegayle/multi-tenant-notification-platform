package com.onboarding.notifications.repo;

import com.onboarding.notifications.domain.NotificationTask;
import com.onboarding.notifications.domain.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, Long> {
    Optional<NotificationTask> findByTenantIdAndDedupeKey(String tenantId, String dedupeKey);
    List<NotificationTask> findByTenantIdAndStatus(String tenantId, TaskStatus status);
}