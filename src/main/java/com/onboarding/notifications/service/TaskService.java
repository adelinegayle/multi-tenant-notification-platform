package com.onboarding.notifications.service;

import com.onboarding.notifications.domain.NotificationTask;
import com.onboarding.notifications.domain.TaskStatus;
import com.onboarding.notifications.repo.NotificationTaskRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskService {

    private final NotificationTaskRepository taskRepo;

    public TaskService(NotificationTaskRepository taskRepo) {
        this.taskRepo = taskRepo;
    }

    public Optional<NotificationTask> findByTenantAndDedupeKey(String tenantId, String dedupeKey) {
        return taskRepo.findByTenantIdAndDedupeKey(tenantId, dedupeKey);
    }

    public NotificationTask save(NotificationTask task) {
        return taskRepo.save(task);
    }

    public void markSent(NotificationTask task) {
        task.markSent();
        taskRepo.save(task);
    }

    public void markFailed(NotificationTask task) {
        task.markFailed();
        taskRepo.save(task);
    }

    public TaskStatus getStatus(NotificationTask task) {
        return task.getStatus();
    }
}