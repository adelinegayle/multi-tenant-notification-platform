package com.onboarding.notifications.messaging;

import com.onboarding.notifications.domain.NotificationTask;
import com.onboarding.notifications.domain.TaskStatus;
import com.onboarding.notifications.service.DeliveryService;
import com.onboarding.notifications.service.TaskService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class NotificationEventConsumer {

    private final TaskService taskService;
    private final DeliveryService deliveryService;

    public NotificationEventConsumer(TaskService taskService, DeliveryService deliveryService) {
        this.taskService = taskService;
        this.deliveryService = deliveryService;
    }

    @RabbitListener(queues = "${app.messaging.queue}")
    public void handle(NotificationEvent event) {
        // 1) Idempotency: if task exists, ignore duplicate event
        if (taskService.findByTenantAndDedupeKey(event.getTenantId(), event.getDedupeKey()).isPresent()) {
            System.out.println("[Consumer] Duplicate event ignored. dedupeKey=" + event.getDedupeKey());
            return;
        }

        // 2) Create task PENDING
        NotificationTask task = new NotificationTask(
                event.getTenantId(),
                event.getEmployeeId(),
                event.getWorkflowType(),
                event.getScheduledSendTime(),
                event.getDedupeKey(),
                TaskStatus.PENDING
        );
        task = taskService.save(task);

        // 3) Deliver only if due (v1: immediate if scheduled time <= now)
        try {
            OffsetDateTime now = OffsetDateTime.now();
            if (task.getScheduledTime().isAfter(now)) {
                System.out.println("[Consumer] Task not due yet, leaving as PENDING. taskId=" + task.getTaskId());
                return;
            }

            deliveryService.deliver(task.getTenantId(), task.getEmployeeId(), task.getWorkflowType());
            taskService.markSent(task);
            System.out.println("[Consumer] Task SENT. taskId=" + task.getTaskId());
        } catch (Exception ex) {
            taskService.markFailed(task);
            System.out.println("[Consumer] Task FAILED. taskId=" + task.getTaskId() + " error=" + ex.getMessage());
        }
    }
}