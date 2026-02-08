package com.onboarding.notifications.repo;

import com.onboarding.notifications.domain.NotificationTask;
import com.onboarding.notifications.domain.TaskStatus;
import com.onboarding.notifications.domain.WorkflowType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class NotificationTaskRepositoryTest {

    @Autowired
    private NotificationTaskRepository taskRepo;

    @Test
    void enforcesUniqueTenantAndDedupeKey() {
        OffsetDateTime t = OffsetDateTime.now();
        String tenantId = "acme";
        String dedupeKey = "acme:1:DOCUMENT_SUBMISSION:" + t;

        NotificationTask first = new NotificationTask(
                tenantId, 1L, WorkflowType.DOCUMENT_SUBMISSION, t, dedupeKey, TaskStatus.PENDING
        );
        taskRepo.saveAndFlush(first);

        NotificationTask second = new NotificationTask(
                tenantId, 2L, WorkflowType.DOCUMENT_SUBMISSION, t, dedupeKey, TaskStatus.PENDING
        );

        assertThrows(Exception.class, () -> taskRepo.saveAndFlush(second));
    }

    @Test
    void allowsSameDedupeKeyAcrossDifferentTenants() {
        OffsetDateTime t = OffsetDateTime.now();
        String dedupeKey = "same-key";

        NotificationTask acme = new NotificationTask(
                "acme", 1L, WorkflowType.DOCUMENT_SUBMISSION, t, dedupeKey, TaskStatus.PENDING
        );
        taskRepo.saveAndFlush(acme);

        NotificationTask bright = new NotificationTask(
                "bright", 2L, WorkflowType.DOCUMENT_SUBMISSION, t, dedupeKey, TaskStatus.PENDING
        );

        assertDoesNotThrow(() -> taskRepo.saveAndFlush(bright));
    }
}
