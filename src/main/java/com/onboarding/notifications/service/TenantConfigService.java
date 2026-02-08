package com.onboarding.notifications.service;

import com.onboarding.notifications.domain.Tenant;
import com.onboarding.notifications.repo.TenantRepository;
import org.springframework.stereotype.Service;

@Service
public class TenantConfigService {

    private final TenantRepository tenantRepo;

    public TenantConfigService(TenantRepository tenantRepo) {
        this.tenantRepo = tenantRepo;
    }

    public Tenant getTenantOrThrow(String tenantId) {
        return tenantRepo.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found: " + tenantId));
    }
}
