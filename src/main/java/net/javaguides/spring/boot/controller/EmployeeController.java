package net.javaguides.spring.boot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import net.javaguides.spring.boot.dto.EmployeeResponse;
import net.javaguides.spring.boot.entity.Department;
import net.javaguides.spring.boot.model.Employee;
import net.javaguides.spring.boot.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        List<EmployeeResponse> list = employeeService.getAllEmployees().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(employeeService.getEmployeeById(id)));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<EmployeeResponse> createEmployee(
            @RequestPart("employee") String employeeJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            Authentication authentication,
            HttpServletRequest request) {
        try {
            Employee employee = objectMapper.readValue(employeeJson, Employee.class);
            String performedBy = authentication.getName();
            String ipAddress = request.getRemoteAddr();
            Employee created = employeeService.createEmployee(employee, photo, performedBy, ipAddress);
            return ResponseEntity.ok(toResponse(created));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse employee data: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @RequestPart("employee") String employeeJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            Authentication authentication,
            HttpServletRequest request) {
        try {
            Employee employee = objectMapper.readValue(employeeJson, Employee.class);
            String performedBy = authentication.getName();
            String ipAddress = request.getRemoteAddr();
            Employee updated = employeeService.updateEmployee(id, employee, photo, performedBy, ipAddress);
            return ResponseEntity.ok(toResponse(updated));
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse employee data: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteEmployee(
            @PathVariable Long id,
            Authentication authentication,
            HttpServletRequest request) {
        String performedBy = authentication.getName();
        String ipAddress = request.getRemoteAddr();
        employeeService.deleteEmployee(id, performedBy, ipAddress);
        return ResponseEntity.ok("Employee deleted successfully");
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<EmployeeResponse>> searchEmployees(@RequestParam String query) {
        List<EmployeeResponse> list = employeeService.searchEmployees(query).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByDepartment(@PathVariable Long departmentId) {
        List<EmployeeResponse> list = employeeService.getEmployeesByDepartment(departmentId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmployeeResponse>> getEmployeesByStatus(@PathVariable Employee.Status status) {
        List<EmployeeResponse> list = employeeService.getEmployeesByStatus(status).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    private EmployeeResponse toResponse(Employee e) {
        EmployeeResponse.DepartmentInfo dept = null;
        Department d = e.getDepartment();
        if (d != null) {
            dept = new EmployeeResponse.DepartmentInfo(d.getId(), d.getName());
        }
        return new EmployeeResponse(
                e.getId(),
                e.getEmployeeCode(),
                e.getFirstName(),
                e.getLastName(),
                e.getEmail(),
                e.getPhoneNumber(),
                e.getDesignation(),
                e.getStatus() != null ? e.getStatus().name() : null,
                dept,
                e.getPhotoUrl()
        );
    }
}
