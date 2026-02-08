package com.onboarding.notifications.service;

import com.onboarding.notifications.domain.*;
import com.onboarding.notifications.messaging.NotificationEvent;
import com.onboarding.notifications.repo.EmployeeRepository;
import com.onboarding.notifications.repo.NotificationRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EligibilityServiceTest {

    private EmployeeRepository employeeRepo;
    private NotificationRuleRepository ruleRepo;
    private EligibilityService eligibilityService;

    @BeforeEach
    void setUp() {
        employeeRepo = mock(EmployeeRepository.class);
        ruleRepo = mock(NotificationRuleRepository.class);
        eligibilityService = new EligibilityService(employeeRepo, ruleRepo);
    }

    @Test
    void generatesEvents_whenEmployeeIsDueToday_inTenantTimezone() {
        String tenantId = "acme";
        ZoneId zone = ZoneId.of("Australia/Sydney");
        LocalDate today = LocalDate.now(zone);

        // Rule: trigger after 3 days, send at 09:00
        NotificationRule rule = new NotificationRule(
                tenantId,
                WorkflowType.DOCUMENT_SUBMISSION,
                3,
                LocalTime.of(9, 0),
                null,
                null
        );

        // Employee startDate such that startDate + 3 == today
        Employee dueEmployee = new Employee(
                tenantId,
                "John Doe",
                "john@acme.com",
                today.minusDays(3),
                EmployeeStatus.ACTIVE
        );

        when(employeeRepo.findByTenantId(tenantId)).thenReturn(List.of(dueEmployee));
        when(ruleRepo.findByTenantId(tenantId)).thenReturn(List.of(rule));

        List<NotificationEvent> events = eligibilityService.generateEventsForTenant(tenantId, zone);

        assertEquals(1, events.size());
        NotificationEvent e = events.get(0);

        assertEquals(tenantId, e.getTenantId());
        assertEquals(WorkflowType.DOCUMENT_SUBMISSION, e.getWorkflowType());
        assertNotNull(e.getEventId());

        // scheduledSendTime should be today at 09:00 in tenant timezone
        OffsetDateTime expected = ZonedDateTime.of(today, LocalTime.of(9, 0), zone).toOffsetDateTime();
        assertEquals(expected, e.getScheduledSendTime());

        // dedupeKey should exist and include tenant + workflow
        assertNotNull(e.getDedupeKey());
        assertTrue(e.getDedupeKey().contains("acme"));
        assertTrue(e.getDedupeKey().contains("DOCUMENT_SUBMISSION"));
    }

    @Test
    void doesNotGenerateEvents_forInactiveEmployee() {
        String tenantId = "acme";
        ZoneId zone = ZoneId.of("Australia/Sydney");
        LocalDate today = LocalDate.now(zone);

        NotificationRule rule = new NotificationRule(
                tenantId,
                WorkflowType.DOCUMENT_SUBMISSION,
                3,
                LocalTime.of(9, 0),
                null,
                null
        );

        Employee inactiveDueEmployee = new Employee(
                tenantId,
                "Inactive User",
                "inactive@acme.com",
                today.minusDays(3),
                EmployeeStatus.INACTIVE
        );

        when(employeeRepo.findByTenantId(tenantId)).thenReturn(List.of(inactiveDueEmployee));
        when(ruleRepo.findByTenantId(tenantId)).thenReturn(List.of(rule));

        List<NotificationEvent> events = eligibilityService.generateEventsForTenant(tenantId, zone);

        assertTrue(events.isEmpty());
    }

    @Test
    void doesNotGenerateEvents_whenNotDueToday() {
        String tenantId = "acme";
        ZoneId zone = ZoneId.of("Australia/Sydney");
        LocalDate today = LocalDate.now(zone);

        NotificationRule rule = new NotificationRule(
                tenantId,
                WorkflowType.DOCUMENT_SUBMISSION,
                3,
                LocalTime.of(9, 0),
                null,
                null
        );

        // Not due: startDate + 3 != today
        Employee notDue = new Employee(
                tenantId,
                "Not Due",
                "notdue@acme.com",
                today.minusDays(2),
                EmployeeStatus.ACTIVE
        );

        when(employeeRepo.findByTenantId(tenantId)).thenReturn(List.of(notDue));
        when(ruleRepo.findByTenantId(tenantId)).thenReturn(List.of(rule));

        List<NotificationEvent> events = eligibilityService.generateEventsForTenant(tenantId, zone);

        assertTrue(events.isEmpty());
    }
}
