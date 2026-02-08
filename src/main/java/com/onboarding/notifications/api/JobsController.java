package com.onboarding.notifications.api;

import com.onboarding.notifications.domain.Tenant;
import com.onboarding.notifications.messaging.NotificationEvent;
import com.onboarding.notifications.messaging.NotificationEventPublisher;
import com.onboarding.notifications.repo.TenantRepository;
import com.onboarding.notifications.service.EligibilityService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.util.List;

@RestController
public class JobsController {

    private final TenantRepository tenantRepo;
    private final EligibilityService eligibilityService;
    private final NotificationEventPublisher publisher;

    public JobsController(TenantRepository tenantRepo,
                          EligibilityService eligibilityService,
                          NotificationEventPublisher publisher) {
        this.tenantRepo = tenantRepo;
        this.eligibilityService = eligibilityService;
        this.publisher = publisher;
    }

    @PostMapping("/jobs/run")
    public String run() {
        List<Tenant> tenants = tenantRepo.findAll();
        int published = 0;

        for (Tenant t : tenants) {
            if (!t.isNotificationsEnabled()) continue;

            ZoneId zone = ZoneId.of(t.getTimezone());
            List<NotificationEvent> events = eligibilityService.generateEventsForTenant(t.getTenantId(), zone);

            for (NotificationEvent e : events) {
                publisher.publish(e);
                published++;
            }
        }

        return "Published events: " + published;
    }
}
