package com.onboarding.notifications.service;

import com.onboarding.notifications.domain.NotificationTask;
import com.onboarding.notifications.domain.TaskStatus;
import com.onboarding.notifications.messaging.NotificationEvent;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class NotificationEventProcessorImpl implements NotificationEventProcessor {

    private final TaskService taskService;
    private final NotificationDeliverer deliverer;

    public NotificationEventProcessorImpl(TaskService taskService, NotificationDeliverer deliverer) {
        this.taskService = taskService;
        this.deliverer = deliverer;
    }

    @Override
    public void process(NotificationEvent event) {
        if (taskService.findByTenantAndDedupeKey(event.getTenantId(), event.getDedupeKey()).isPresent()) {
            System.out.println("[Consumer] Duplicate event ignored. dedupeKey=" + event.getDedupeKey());
            return;
        }

        NotificationTask task = new NotificationTask(
                event.getTenantId(),
                event.getEmployeeId(),
                event.getWorkflowType(),
                event.getScheduledSendTime(),
                event.getDedupeKey(),
                TaskStatus.PENDING
        );
        task = taskService.save(task);

        try {
            OffsetDateTime now = OffsetDateTime.now();
            if (task.getScheduledTime().isAfter(now)) {
                System.out.println("[Consumer] Task not due yet, leaving as PENDING. taskId=" + task.getTaskId());
                return;
            }

            deliverer.deliver(task.getTenantId(), task.getEmployeeId(), task.getWorkflowType());
            taskService.markSent(task);
            System.out.println("[Consumer] Task SENT. taskId=" + task.getTaskId());
        } catch (Exception ex) {
            taskService.markFailed(task);
            System.out.println("[Consumer] Task FAILED. taskId=" + task.getTaskId() + " error=" + ex.getMessage());
        }
    }
}
