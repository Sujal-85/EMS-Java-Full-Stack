package net.javaguides.spring.boot.bootstrap;

import net.javaguides.spring.boot.entity.Department;
import net.javaguides.spring.boot.entity.User;
import net.javaguides.spring.boot.repository.DepartmentRepository;
import net.javaguides.spring.boot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Initialize departments if they don't exist
        if (departmentRepository.count() == 0) {
            initializeDepartments();
        }
        
        // Ensure default admin and HR users exist (create or update on every startup)
        ensureDefaultUsers();
        
        System.out.println("=== Data Initialization Complete ===");
        System.out.println("Total Departments: " + departmentRepository.count());
        System.out.println("Total Users: " + userRepository.count());
    }
    
    private void initializeDepartments() {
        createDepartment("Engineering", "Software development and engineering");
        createDepartment("Human Resources", "HR and employee management");
        createDepartment("Finance", "Financial operations and accounting");
        createDepartment("Marketing", "Marketing and communications");
        createDepartment("Sales", "Sales and business development");
        createDepartment("Operations", "Operations and logistics");
        
        System.out.println("✓ Sample departments created");
    }
    
    private void createDepartment(String name, String description) {
        Department dept = new Department();
        dept.setName(name);
        dept.setDescription(description);
        departmentRepository.save(dept);
    }
    
    private void ensureDefaultUsers() {
        // Upsert Admin user
        User admin = userRepository.findByEmail("admin@ems.com").orElseGet(User::new);
        admin.setEmail("admin@ems.com");
        admin.setFullName("System Administrator");
        admin.setRole(User.Role.ADMIN);
        admin.setActive(true);
        // Always set (update) password to the configured default
        admin.setPassword(passwordEncoder.encode("admin123"));
        userRepository.save(admin);

        // Upsert HR user
        User hr = userRepository.findByEmail("hr@ems.com").orElseGet(User::new);
        hr.setEmail("hr@ems.com");
        hr.setFullName("HR Manager");
        hr.setRole(User.Role.HR);
        hr.setActive(true);
        hr.setPassword(passwordEncoder.encode("hr123"));
        userRepository.save(hr);

        System.out.println("✓ Default users ensured (created/updated):");
        System.out.println("  Admin: admin@ems.com / admin123");
        System.out.println("  HR: hr@ems.com / hr123");
    }
}
