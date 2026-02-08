package com.onboarding.notifications.service;

import com.onboarding.notifications.domain.Employee;
import com.onboarding.notifications.domain.NotificationTemplate;
import com.onboarding.notifications.domain.Tenant;
import com.onboarding.notifications.domain.WorkflowType;
import com.onboarding.notifications.repo.EmployeeRepository;
import com.onboarding.notifications.repo.NotificationTemplateRepository;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeliveryService {

    private final TenantConfigService tenantConfigService;
    private final EmployeeRepository employeeRepo;
    private final NotificationTemplateRepository templateRepo;

    // per-tenant limiter cache
    private final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();

    public DeliveryService(TenantConfigService tenantConfigService,
                           EmployeeRepository employeeRepo,
                           NotificationTemplateRepository templateRepo) {
        this.tenantConfigService = tenantConfigService;
        this.employeeRepo = employeeRepo;
        this.templateRepo = templateRepo;
    }

    public void deliver(String tenantId, Long employeeId, WorkflowType workflowType) {
        Tenant tenant = tenantConfigService.getTenantOrThrow(tenantId);

        RateLimiter limiter = limiters.computeIfAbsent(tenantId, t -> createLimiter(tenant.getRateLimitPerSecond()));

        // Acquire permission (blocks up to timeoutDuration)
        boolean allowed = limiter.acquirePermission();
        if (!allowed) {
            throw new RuntimeException("Rate limiter denied permission for tenant=" + tenantId);
        }

        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeId));

        NotificationTemplate template = templateRepo.findByTenantIdAndWorkflowType(tenantId, workflowType)
                .orElseThrow(() -> new IllegalArgumentException("Template not found for tenant=" + tenantId + ", workflow=" + workflowType));

        String subject = render(template.getSubjectTemplate(), employee.getName());
        String body = render(template.getBodyTemplate(), employee.getName());

        // v1 delivery: simulate sending by logging
        System.out.println("[Delivery] tenant=" + tenantId +
                " employee=" + employee.getEmail() +
                " workflow=" + workflowType +
                " subject=\"" + subject + "\" body=\"" + body + "\"");
    }

    private RateLimiter createLimiter(int perSecond) {
        // perSecond must be >= 1; if 0, treat as 1 to avoid crashes
        int limit = Math.max(perSecond, 1);

        RateLimiterConfig cfg = RateLimiterConfig.custom()
                .limitForPeriod(limit)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofSeconds(2))
                .build();

        return RateLimiter.of("tenantLimiter-" + limit, cfg);
    }

    private String render(String template, String name) {
        return template.replace("{{name}}", name);
    }
}