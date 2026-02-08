package com.onboarding.notifications.repo;

import com.onboarding.notifications.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByTenantId(String tenantId);
}