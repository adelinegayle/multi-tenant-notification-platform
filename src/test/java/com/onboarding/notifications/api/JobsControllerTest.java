package com.onboarding.notifications.api;

import com.onboarding.notifications.domain.Tenant;
import com.onboarding.notifications.messaging.NotificationEvent;
import com.onboarding.notifications.messaging.NotificationEventPublisher;
import com.onboarding.notifications.repo.TenantRepository;
import com.onboarding.notifications.service.EligibilityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneId;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JobsController.class)
class JobsControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TenantRepository tenantRepo;

    @MockBean
    private EligibilityService eligibilityService;

    @MockBean
    private NotificationEventPublisher publisher;

    @Test
    void jobsRunPublishesEventsForEnabledTenants() throws Exception {
        Tenant tenant = new Tenant("acme", "Acme", "Australia/Sydney", 5, true);

        when(tenantRepo.findAll()).thenReturn(List.of(tenant));
        when(eligibilityService.generateEventsForTenant(eq("acme"), eq(ZoneId.of("Australia/Sydney"))))
                .thenReturn(List.of(new NotificationEvent()));

        mvc.perform(post("/jobs/run"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Published events: 1")));

        verify(publisher, times(1)).publish(any(NotificationEvent.class));
    }
}
