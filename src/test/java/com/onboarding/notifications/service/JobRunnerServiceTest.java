package com.onboarding.notifications.service;

import com.onboarding.notifications.domain.Tenant;
import com.onboarding.notifications.messaging.EventPublisher;
import com.onboarding.notifications.messaging.NotificationEvent;
import com.onboarding.notifications.repo.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class JobRunnerServiceTest {

    private TenantRepository tenantRepo;
    private EligibilityService eligibilityService;
    private EventPublisher eventPublisher;
    private JobRunnerService jobRunnerService;

    @BeforeEach
    void setUp() {
        tenantRepo = mock(TenantRepository.class);
        eligibilityService = mock(EligibilityService.class);
        eventPublisher = mock(EventPublisher.class);
        jobRunnerService = new JobRunnerService(tenantRepo, eligibilityService, eventPublisher);
    }

    @Test
    void runPublishesEventsForEnabledTenantsAndReturnsSummary() {
        Tenant tenant = new Tenant("acme", "Acme", "Australia/Sydney", 5, true);

        when(tenantRepo.findAll()).thenReturn(List.of(tenant));
        when(eligibilityService.generateEventsForTenant(eq("acme"), eq(ZoneId.of("Australia/Sydney"))))
                .thenReturn(List.of(new NotificationEvent()));

        String result = jobRunnerService.run();

        assertTrue(result.contains("Published events: 1"));
        verify(eventPublisher, times(1)).publish(any(NotificationEvent.class));
    }

    @Test
    void runSkipsTenantsWithNotificationsDisabled() {
        Tenant disabled = new Tenant("acme", "Acme", "Australia/Sydney", 5, false);

        when(tenantRepo.findAll()).thenReturn(List.of(disabled));

        String result = jobRunnerService.run();

        assertTrue(result.contains("Published events: 0"));
        verify(eligibilityService, never()).generateEventsForTenant(anyString(), any());
        verify(eventPublisher, never()).publish(any());
    }
}
