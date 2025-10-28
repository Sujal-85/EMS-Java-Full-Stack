package net.javaguides.spring.boot.service;

import net.javaguides.spring.boot.model.Employee;
import net.javaguides.spring.boot.entity.User;
import net.javaguides.spring.boot.repository.EmployeeRepository;
import net.javaguides.spring.boot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private AuditLogService auditLogService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }
    
    public Employee createEmployee(Employee employee, MultipartFile photo, String performedBy, String ipAddress) {
        // Generate unique employee code
        employee.setEmployeeCode("EMP" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        // Handle photo upload
        if (photo != null && !photo.isEmpty()) {
            String photoUrl = fileStorageService.storeFile(photo);
            employee.setPhotoUrl(photoUrl);
        }
        // Create linked EMPLOYEE user with fixed default password
        // If a user already exists for this email, reuse it; otherwise create new
        User user = userRepository.findByEmail(employee.getEmail()).orElseGet(User::new);
        user.setEmail(employee.getEmail());
        user.setFullName(employee.getFirstName() + " " + employee.getLastName());
        user.setRole(User.Role.EMPLOYEE);
        user.setActive(true);
        // Fixed default password for employees
        String defaultPassword = "employee123";
        user.setPassword(passwordEncoder.encode(defaultPassword));
        User savedUser = userRepository.save(user);

        // Link user to employee
        employee.setUser(savedUser);

        Employee savedEmployee = employeeRepository.save(employee);
        
        auditLogService.log("CREATE", "Employee", savedEmployee.getId(),
            "Created employee: " + savedEmployee.getFirstName() + " " + savedEmployee.getLastName(),
            performedBy, ipAddress);
        
        emailService.sendEmployeeCreationEmail(savedEmployee.getEmail(), 
            savedEmployee.getFirstName() + " " + savedEmployee.getLastName(), 
            savedEmployee.getEmployeeCode() + " | Default Password: " + defaultPassword);
        
        return savedEmployee;
    }
    
    public Employee updateEmployee(Long id, Employee employeeDetails, MultipartFile photo, 
                                   String performedBy, String ipAddress) {
        Employee employee = getEmployeeById(id);
        
        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setEmail(employeeDetails.getEmail());
        employee.setPhoneNumber(employeeDetails.getPhoneNumber());
        employee.setDateOfBirth(employeeDetails.getDateOfBirth());
        employee.setDateOfJoining(employeeDetails.getDateOfJoining());
        employee.setDesignation(employeeDetails.getDesignation());
        employee.setAddress(employeeDetails.getAddress());
        employee.setStatus(employeeDetails.getStatus());
        employee.setDepartment(employeeDetails.getDepartment());
        
        if (photo != null && !photo.isEmpty()) {
            String photoUrl = fileStorageService.storeFile(photo);
            employee.setPhotoUrl(photoUrl);
        }
        
        Employee updatedEmployee = employeeRepository.save(employee);
        
        auditLogService.log("UPDATE", "Employee", id,
            "Updated employee: " + employee.getFirstName() + " " + employee.getLastName(),
            performedBy, ipAddress);
        
        return updatedEmployee;
    }
    
    public void deleteEmployee(Long id, String performedBy, String ipAddress) {
        Employee employee = getEmployeeById(id);
        employeeRepository.deleteById(id);
        
        auditLogService.log("DELETE", "Employee", id,
            "Deleted employee: " + employee.getFirstName() + " " + employee.getLastName(),
            performedBy, ipAddress);
    }
    
    public List<Employee> searchEmployees(String searchTerm) {
        return employeeRepository.findByFirstNameContainingOrLastNameContainingOrEmailContaining(
            searchTerm, searchTerm, searchTerm);
    }
    
    public List<Employee> getEmployeesByDepartment(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId);
    }
    
    public List<Employee> getEmployeesByStatus(Employee.Status status) {
        return employeeRepository.findByStatus(status);
    }
    
    public Long countByStatus(Employee.Status status) {
        return employeeRepository.countByStatus(status);
    }
}
