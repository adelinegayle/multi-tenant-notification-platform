package com.onboarding.notifications.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tenants")
public class Tenant {

    @Id
    @Column(length = 64)
    private String tenantId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String timezone; // e.g. "Australia/Sydney"

    @Column(nullable = false)
    private int rateLimitPerSecond;

    @Column(nullable = false)
    private boolean notificationsEnabled = true;

    protected Tenant() {
    }

    public Tenant(String tenantId, String name, String timezone, int rateLimitPerSecond, boolean notificationsEnabled) {
        this.tenantId = tenantId;
        this.name = name;
        this.timezone = timezone;
        this.rateLimitPerSecond = rateLimitPerSecond;
        this.notificationsEnabled = notificationsEnabled;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getName() {
        return name;
    }

    public String getTimezone() {
        return timezone;
    }

    public int getRateLimitPerSecond() {
        return rateLimitPerSecond;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

}