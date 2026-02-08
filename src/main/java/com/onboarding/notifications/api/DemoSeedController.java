package com.onboarding.notifications.api;

import com.onboarding.notifications.domain.*;
import com.onboarding.notifications.repo.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
public class DemoSeedController {

    private final TenantRepository tenantRepo;
    private final EmployeeRepository employeeRepo;
    private final NotificationRuleRepository ruleRepo;
    private final NotificationTemplateRepository templateRepo;

    public DemoSeedController(
            TenantRepository tenantRepo,
            EmployeeRepository employeeRepo,
            NotificationRuleRepository ruleRepo,
            NotificationTemplateRepository templateRepo
    ) {
        this.tenantRepo = tenantRepo;
        this.employeeRepo = employeeRepo;
        this.ruleRepo = ruleRepo;
        this.templateRepo = templateRepo;
    }

    @PostMapping("/demo/seed")
    public String seed() {
        // Tenants
        tenantRepo.save(new Tenant("acme", "Acme Corp", "Australia/Sydney", 5, true));
        tenantRepo.save(new Tenant("bright", "Bright Startups", "America/New_York", 2, true));

        // Employees
        employeeRepo.save(new Employee("acme", "John Doe", "john@acme.com", LocalDate.now().minusDays(3), EmployeeStatus.ACTIVE));
        employeeRepo.save(new Employee("acme", "Jane Roe", "jane@acme.com", LocalDate.now().minusDays(7), EmployeeStatus.ACTIVE));
        employeeRepo.save(new Employee("bright", "Sam Smith", "sam@bright.com", LocalDate.now().minusDays(3), EmployeeStatus.ACTIVE));

        // Rules
        ruleRepo.save(new NotificationRule("acme", WorkflowType.DOCUMENT_SUBMISSION, 3, LocalTime.of(9, 0), 2, 3));
        ruleRepo.save(new NotificationRule("acme", WorkflowType.MANAGER_CHECKIN, 7, LocalTime.of(9, 0), null, null));
        ruleRepo.save(new NotificationRule("bright", WorkflowType.DOCUMENT_SUBMISSION, 3, LocalTime.of(9, 0), null, null));

        // Templates
        templateRepo.save(new NotificationTemplate(
                "acme", WorkflowType.DOCUMENT_SUBMISSION, "EMAIL",
                "Acme: Please upload your documents",
                "Hi {{name}}, please upload your onboarding documents."
        ));
        templateRepo.save(new NotificationTemplate(
                "acme", WorkflowType.MANAGER_CHECKIN, "EMAIL",
                "Acme: Manager check-in due",
                "Hi {{name}}, your manager check-in is due."
        ));
        templateRepo.save(new NotificationTemplate(
                "bright", WorkflowType.DOCUMENT_SUBMISSION, "EMAIL",
                "Bright: Upload documents reminder",
                "Hi {{name}}, upload your documents to complete onboarding."
        ));

        return "Seeded demo data";
    }
}