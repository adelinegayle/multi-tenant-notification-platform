package com.onboarding.notifications.repo;


import com.onboarding.notifications.domain.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, String> {}