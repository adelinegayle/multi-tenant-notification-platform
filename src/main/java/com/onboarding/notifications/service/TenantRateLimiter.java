package com.onboarding.notifications.service;

public interface TenantRateLimiter {

    boolean tryAcquire(String tenantId);
}
