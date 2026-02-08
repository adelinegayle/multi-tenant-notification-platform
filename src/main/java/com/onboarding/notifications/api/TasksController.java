package com.onboarding.notifications.api;

import com.onboarding.notifications.domain.NotificationTask;
import com.onboarding.notifications.domain.TaskStatus;
import com.onboarding.notifications.repo.NotificationTaskRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TasksController {

    private final NotificationTaskRepository taskRepo;

    public TasksController(NotificationTaskRepository taskRepo) {
        this.taskRepo = taskRepo;
    }

    @GetMapping("/tasks")
    public List<NotificationTask> tasks(@RequestParam String tenantId,
                                        @RequestParam(required = false) TaskStatus status) {
        if (status == null) {
            return taskRepo.findAll().stream().filter(t -> t.getTenantId().equals(tenantId)).toList();
        }
        return taskRepo.findByTenantIdAndStatus(tenantId, status);
    }
}