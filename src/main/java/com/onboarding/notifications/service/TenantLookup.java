package com.onboarding.notifications.service;

import com.onboarding.notifications.domain.Tenant;

public interface TenantLookup {

    Tenant getTenantOrThrow(String tenantId);
}
