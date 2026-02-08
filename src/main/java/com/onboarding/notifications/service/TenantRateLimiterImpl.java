package com.onboarding.notifications.service;

import com.onboarding.notifications.domain.Tenant;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TenantRateLimiterImpl implements TenantRateLimiter {

    private final TenantLookup tenantLookup;
    private final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();

    public TenantRateLimiterImpl(TenantLookup tenantLookup) {
        this.tenantLookup = tenantLookup;
    }

    @Override
    public boolean tryAcquire(String tenantId) {
        Tenant tenant = tenantLookup.getTenantOrThrow(tenantId);
        RateLimiter limiter = limiters.computeIfAbsent(tenantId, t -> createLimiter(tenant.getRateLimitPerSecond()));
        return limiter.acquirePermission();
    }

    private RateLimiter createLimiter(int perSecond) {
        int limit = Math.max(perSecond, 1);
        RateLimiterConfig cfg = RateLimiterConfig.custom()
                .limitForPeriod(limit)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofSeconds(2))
                .build();
        return RateLimiter.of("tenantLimiter-" + limit, cfg);
    }
}
