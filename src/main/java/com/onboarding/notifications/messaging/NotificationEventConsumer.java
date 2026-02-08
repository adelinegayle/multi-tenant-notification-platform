package com.onboarding.notifications.messaging;

import com.onboarding.notifications.domain.NotificationTask;
import com.onboarding.notifications.domain.TaskStatus;
import com.onboarding.notifications.repo.NotificationTaskRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventConsumer {

    private final NotificationTaskRepository taskRepo;

    public NotificationEventConsumer(NotificationTaskRepository taskRepo) {
        this.taskRepo = taskRepo;
    }

    @RabbitListener(queues = "${app.messaging.queue}")
    public void handle(NotificationEvent event) {
        // Idempotency: if task exists with the same dedupeKey, ignore
        boolean exists = taskRepo.findByTenantIdAndDedupeKey(event.getTenantId(), event.getDedupeKey()).isPresent();
        if (exists) {
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

        taskRepo.save(task);
        System.out.println("[Consumer] Created task. dedupeKey=" + event.getDedupeKey());
    }
}