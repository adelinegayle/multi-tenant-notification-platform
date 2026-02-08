package com.onboarding.notifications.service;

import com.onboarding.notifications.domain.Tenant;
import com.onboarding.notifications.messaging.EventPublisher;
import com.onboarding.notifications.messaging.NotificationEvent;
import com.onboarding.notifications.repo.TenantRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.List;

@Service
public class JobRunnerService implements JobRunner {

    private final TenantRepository tenantRepo;
    private final EligibilityService eligibilityService;
    private final EventPublisher eventPublisher;

    public JobRunnerService(TenantRepository tenantRepo,
                            EligibilityService eligibilityService,
                            EventPublisher eventPublisher) {
        this.tenantRepo = tenantRepo;
        this.eligibilityService = eligibilityService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public String run() {
        List<Tenant> tenants = tenantRepo.findAll();
        int published = 0;

        for (Tenant t : tenants) {
            if (!t.isNotificationsEnabled()) continue;

            ZoneId zone = ZoneId.of(t.getTimezone());
            List<NotificationEvent> events = eligibilityService.generateEventsForTenant(t.getTenantId(), zone);

            for (NotificationEvent e : events) {
                eventPublisher.publish(e);
                published++;
            }
        }

        return "Published events: " + published;
    }
}
