package com.onboarding.notifications.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @Column(nullable = false, length = 64)
    private String tenantId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeStatus status;

    protected Employee() {}

    public Employee(String tenantId, String name, String email, LocalDate startDate, EmployeeStatus status) {
        this.tenantId = tenantId;
        this.name = name;
        this.email = email;
        this.startDate = startDate;
        this.status = status;
    }

    public Long getEmployeeId() { return employeeId; }
    public String getTenantId() { return tenantId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public LocalDate getStartDate() { return startDate; }
    public EmployeeStatus getStatus() { return status; }
}
