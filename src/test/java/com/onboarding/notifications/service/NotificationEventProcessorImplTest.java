package com.onboarding.notifications.service;

import com.onboarding.notifications.domain.NotificationTask;
import com.onboarding.notifications.domain.TaskStatus;
import com.onboarding.notifications.domain.WorkflowType;
import com.onboarding.notifications.messaging.NotificationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;

class NotificationEventProcessorImplTest {

    private TaskService taskService;
    private NotificationDeliverer deliverer;
    private NotificationEventProcessorImpl processor;

    @BeforeEach
    void setUp() {
        taskService = mock(TaskService.class);
        deliverer = mock(NotificationDeliverer.class);
        processor = new NotificationEventProcessorImpl(taskService, deliverer);
    }

    @Test
    void ignoresDuplicateEvent_whenTaskAlreadyExistsForDedupeKey() {
        NotificationEvent event = new NotificationEvent(
                "e1",
                "acme",
                10L,
                WorkflowType.DOCUMENT_SUBMISSION,
                OffsetDateTime.now().minusMinutes(1),
                "acme:10:DOCUMENT_SUBMISSION:dedupe"
        );

        when(taskService.findByTenantAndDedupeKey("acme", event.getDedupeKey()))
                .thenReturn(Optional.of(mock(NotificationTask.class)));

        processor.process(event);

        verify(taskService, never()).save(any());
        verify(deliverer, never()).deliver(anyString(), anyLong(), any());
    }

    @Test
    void createsTaskAndMarksSent_whenDueNow() {
        OffsetDateTime dueTime = OffsetDateTime.now().minusMinutes(1);

        NotificationEvent event = new NotificationEvent(
                "e2",
                "acme",
                11L,
                WorkflowType.DOCUMENT_SUBMISSION,
                dueTime,
                "acme:11:DOCUMENT_SUBMISSION:" + dueTime
        );

        when(taskService.findByTenantAndDedupeKey("acme", event.getDedupeKey()))
                .thenReturn(Optional.empty());

        NotificationTask saved = new NotificationTask(
                event.getTenantId(),
                event.getEmployeeId(),
                event.getWorkflowType(),
                event.getScheduledSendTime(),
                event.getDedupeKey(),
                TaskStatus.PENDING
        );
        when(taskService.save(any(NotificationTask.class))).thenReturn(saved);

        processor.process(event);

        verify(taskService).save(any(NotificationTask.class));
        verify(deliverer).deliver("acme", 11L, WorkflowType.DOCUMENT_SUBMISSION);
        verify(taskService).markSent(saved);
        verify(taskService, never()).markFailed(any());
    }

    @Test
    void leavesTaskPending_whenScheduledInFuture() {
        OffsetDateTime future = OffsetDateTime.now().plusHours(1);

        NotificationEvent event = new NotificationEvent(
                "e3",
                "acme",
                12L,
                WorkflowType.MANAGER_CHECKIN,
                future,
                "acme:12:MANAGER_CHECKIN:" + future
        );

        when(taskService.findByTenantAndDedupeKey("acme", event.getDedupeKey()))
                .thenReturn(Optional.empty());

        NotificationTask saved = new NotificationTask(
                event.getTenantId(),
                event.getEmployeeId(),
                event.getWorkflowType(),
                event.getScheduledSendTime(),
                event.getDedupeKey(),
                TaskStatus.PENDING
        );
        when(taskService.save(any(NotificationTask.class))).thenReturn(saved);

        processor.process(event);

        verify(taskService).save(any(NotificationTask.class));
        verify(deliverer, never()).deliver(anyString(), anyLong(), any());
        verify(taskService, never()).markSent(any());
        verify(taskService, never()).markFailed(any());
    }

    @Test
    void marksFailed_whenDeliveryThrowsException() {
        OffsetDateTime dueTime = OffsetDateTime.now().minusMinutes(1);

        NotificationEvent event = new NotificationEvent(
                "e4",
                "acme",
                13L,
                WorkflowType.DOCUMENT_SUBMISSION,
                dueTime,
                "acme:13:DOCUMENT_SUBMISSION:" + dueTime
        );

        when(taskService.findByTenantAndDedupeKey("acme", event.getDedupeKey()))
                .thenReturn(Optional.empty());

        NotificationTask saved = new NotificationTask(
                event.getTenantId(),
                event.getEmployeeId(),
                event.getWorkflowType(),
                event.getScheduledSendTime(),
                event.getDedupeKey(),
                TaskStatus.PENDING
        );
        when(taskService.save(any(NotificationTask.class))).thenReturn(saved);

        doThrow(new RuntimeException("boom"))
                .when(deliverer).deliver("acme", 13L, WorkflowType.DOCUMENT_SUBMISSION);

        processor.process(event);

        verify(taskService).markFailed(saved);
        verify(taskService, never()).markSent(any());
    }
}
