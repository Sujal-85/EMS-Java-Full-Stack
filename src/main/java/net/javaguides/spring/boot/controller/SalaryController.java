package net.javaguides.spring.boot.controller;

import jakarta.servlet.http.HttpServletRequest;
import net.javaguides.spring.boot.entity.Salary;
import net.javaguides.spring.boot.service.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
public class SalaryController {
    
    @Autowired
    private SalaryService salaryService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Salary> createSalary(
            @RequestBody Salary salary,
            Authentication authentication,
            HttpServletRequest request) {
        String performedBy = authentication.getName();
        String ipAddress = request.getRemoteAddr();
        Salary created = salaryService.createSalary(salary, performedBy, ipAddress);
        return ResponseEntity.ok(created);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Salary> updateSalary(
            @PathVariable Long id,
            @RequestBody Salary salary,
            Authentication authentication,
            HttpServletRequest request) {
        String performedBy = authentication.getName();
        String ipAddress = request.getRemoteAddr();
        Salary updated = salaryService.updateSalary(id, salary, performedBy, ipAddress);
        return ResponseEntity.ok(updated);
    }
    
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Salary>> getSalariesByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(salaryService.getSalariesByEmployee(employeeId));
    }
    
    @GetMapping("/month/{month}/year/{year}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<List<Salary>> getSalariesByMonth(
            @PathVariable Integer month,
            @PathVariable Integer year) {
        return ResponseEntity.ok(salaryService.getSalariesByMonth(month, year));
    }
    
    @GetMapping("/{id}/payslip")
    public ResponseEntity<byte[]> downloadPayslip(@PathVariable Long id) {
        byte[] pdfBytes = salaryService.generatePayslip(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "payslip_" + id + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
    
    @GetMapping("/expense/month/{month}/year/{year}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> getMonthlyExpense(
            @PathVariable Integer month,
            @PathVariable Integer year) {
        return ResponseEntity.ok(salaryService.getMonthlyExpense(month, year));
    }
}
