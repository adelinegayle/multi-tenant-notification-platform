package com.onboarding.notifications.service;

import com.onboarding.notifications.domain.Employee;
import com.onboarding.notifications.domain.EmployeeStatus;
import com.onboarding.notifications.domain.NotificationRule;
import com.onboarding.notifications.messaging.NotificationEvent;
import com.onboarding.notifications.repo.EmployeeRepository;
import com.onboarding.notifications.repo.NotificationRuleRepository;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EligibilityService {

    private final EmployeeRepository employeeRepo;
    private final NotificationRuleRepository ruleRepo;

    public EligibilityService(EmployeeRepository employeeRepo, NotificationRuleRepository ruleRepo) {
        this.employeeRepo = employeeRepo;
        this.ruleRepo = ruleRepo;
    }

    public List<NotificationEvent> generateEventsForTenant(String tenantId, ZoneId tenantZone) {
        List<Employee> employees = employeeRepo.findByTenantId(tenantId);
        List<NotificationRule> rules = ruleRepo.findByTenantId(tenantId);

        List<NotificationEvent> events = new ArrayList<>();
        LocalDate today = LocalDate.now(tenantZone);

        for (Employee e : employees) {
            if (e.getStatus() != EmployeeStatus.ACTIVE) continue;

            for (NotificationRule r : rules) {
                // v1 rule: only generate the "first trigger" event (ignore repeats for now)
                LocalDate dueDate = e.getStartDate().plusDays(r.getTriggerAfterDays());
                if (!dueDate.equals(today)) continue;

                OffsetDateTime scheduledSendTime = ZonedDateTime.of(today, r.getSendTimeOfDay(), tenantZone)
                        .toOffsetDateTime();

                String dedupeKey = tenantId + ":" + e.getEmployeeId() + ":" + r.getWorkflowType() + ":" + scheduledSendTime;

                events.add(new NotificationEvent(
                        UUID.randomUUID().toString(),
                        tenantId,
                        e.getEmployeeId(),
                        r.getWorkflowType(),
                        scheduledSendTime,
                        dedupeKey
                ));
            }
        }

        return events;
    }
}