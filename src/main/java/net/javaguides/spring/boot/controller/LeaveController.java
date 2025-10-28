package net.javaguides.spring.boot.controller;

import jakarta.servlet.http.HttpServletRequest;
import net.javaguides.spring.boot.entity.Leave;
import net.javaguides.spring.boot.entity.User;
import net.javaguides.spring.boot.repository.UserRepository;
import net.javaguides.spring.boot.repository.EmployeeRepository;
import net.javaguides.spring.boot.model.Employee;
import net.javaguides.spring.boot.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {
    
    @Autowired
    private LeaveService leaveService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<List<Leave>> getAllLeaves() {
        return ResponseEntity.ok(leaveService.getAllLeaves());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Leave> getLeaveById(@PathVariable Long id) {
        return ResponseEntity.ok(leaveService.getLeaveById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Leave> applyLeave(
            @RequestBody Leave leave,
            Authentication authentication,
            HttpServletRequest request) {
        String performedBy = authentication.getName();
        // Resolve authenticated user -> employee and enforce association on server side
        User currentUser = userRepository.findByEmail(performedBy)
                .orElseThrow(() -> new RuntimeException("User not found: " + performedBy));
        if (currentUser.getEmployee() == null) {
            throw new RuntimeException("No employee profile linked to user: " + performedBy);
        }
        // Fetch a managed Employee entity to avoid transient association issues
        Long empId = currentUser.getEmployee().getId();
        Employee managedEmp = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + empId));
        leave.setEmployee(managedEmp);
        String ipAddress = request.getRemoteAddr();
        Leave created = leaveService.applyLeave(leave, performedBy, ipAddress);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<Leave> approveLeave(
            @PathVariable Long id,
            @RequestParam(required = false) String comments,
            Authentication authentication,
            HttpServletRequest request) {
        String approverEmail = authentication.getName();
        String ipAddress = request.getRemoteAddr();
        Leave approved = leaveService.approveLeave(id, approverEmail, comments, ipAddress);
        return ResponseEntity.ok(approved);
    }
    
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<Leave> rejectLeave(
            @PathVariable Long id,
            @RequestParam(required = false) String comments,
            Authentication authentication,
            HttpServletRequest request) {
        String approverEmail = authentication.getName();
        String ipAddress = request.getRemoteAddr();
        Leave rejected = leaveService.rejectLeave(id, approverEmail, comments, ipAddress);
        return ResponseEntity.ok(rejected);
    }
    
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Leave>> getLeavesByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveService.getLeavesByEmployee(employeeId));
    }
    
    @GetMapping("/my")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<List<Leave>> getMyLeaves(Authentication authentication) {
        String email = authentication.getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        if (currentUser.getEmployee() == null) {
            throw new RuntimeException("No employee profile linked to user: " + email);
        }
        Long empId = currentUser.getEmployee().getId();
        return ResponseEntity.ok(leaveService.getLeavesByEmployee(empId));
    }
    
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<List<Leave>> getPendingLeaves() {
        return ResponseEntity.ok(leaveService.getPendingLeaves());
    }
}
