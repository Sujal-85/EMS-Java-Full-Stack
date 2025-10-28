package net.javaguides.spring.boot.service;

import net.javaguides.spring.boot.entity.Salary;
import net.javaguides.spring.boot.repository.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDate;

@Service
public class SalaryService {
    
    @Autowired
    private SalaryRepository salaryRepository;
    
    @Autowired
    private AuditLogService auditLogService;
    
    @Autowired
    private PdfGenerationService pdfGenerationService;
    
    public Salary createSalary(Salary salary, String performedBy, String ipAddress) {
        // Calculate net salary
        double totalAllowances = (salary.getHra() != null ? salary.getHra() : 0) +
                                (salary.getTransportAllowance() != null ? salary.getTransportAllowance() : 0) +
                                (salary.getMedicalAllowance() != null ? salary.getMedicalAllowance() : 0) +
                                (salary.getOtherAllowances() != null ? salary.getOtherAllowances() : 0) +
                                (salary.getBonus() != null ? salary.getBonus() : 0);
        
        double deductions = salary.getDeductions() != null ? salary.getDeductions() : 0;
        double netSalary = salary.getBasicSalary() + totalAllowances - deductions;
        salary.setNetSalary(netSalary);

        // Ensure non-null payment date to satisfy NOT NULL constraint
        if (salary.getPaymentDate() == null) {
            salary.setPaymentDate(LocalDate.now());
        }
        
        Salary saved = salaryRepository.save(salary);
        
        auditLogService.log("CREATE", "Salary", saved.getId(),
            "Salary record created for " + salary.getMonth() + "/" + salary.getYear(),
            performedBy, ipAddress);
        
        return saved;
    }
    
    public Salary updateSalary(Long id, Salary salaryDetails, String performedBy, String ipAddress) {
        Salary salary = salaryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Salary record not found"));
        
        salary.setBasicSalary(salaryDetails.getBasicSalary());
        salary.setHra(salaryDetails.getHra());
        salary.setTransportAllowance(salaryDetails.getTransportAllowance());
        salary.setMedicalAllowance(salaryDetails.getMedicalAllowance());
        salary.setOtherAllowances(salaryDetails.getOtherAllowances());
        salary.setBonus(salaryDetails.getBonus());
        salary.setDeductions(salaryDetails.getDeductions());
        salary.setPaymentStatus(salaryDetails.getPaymentStatus());
        salary.setRemarks(salaryDetails.getRemarks());
        
        // Recalculate net salary
        double totalAllowances = (salary.getHra() != null ? salary.getHra() : 0) +
                                (salary.getTransportAllowance() != null ? salary.getTransportAllowance() : 0) +
                                (salary.getMedicalAllowance() != null ? salary.getMedicalAllowance() : 0) +
                                (salary.getOtherAllowances() != null ? salary.getOtherAllowances() : 0) +
                                (salary.getBonus() != null ? salary.getBonus() : 0);
        
        double deductions = salary.getDeductions() != null ? salary.getDeductions() : 0;
        salary.setNetSalary(salary.getBasicSalary() + totalAllowances - deductions);
        
        Salary updated = salaryRepository.save(salary);
        
        auditLogService.log("UPDATE", "Salary", id,
            "Salary record updated", performedBy, ipAddress);
        
        return updated;
    }
    
    public List<Salary> getSalariesByEmployee(Long employeeId) {
        return salaryRepository.findByEmployeeId(employeeId);
    }
    
    public List<Salary> getSalariesByMonth(Integer month, Integer year) {
        return salaryRepository.findByMonthAndYear(month, year);
    }
    
    public byte[] generatePayslip(Long salaryId) {
        Salary salary = salaryRepository.findById(salaryId)
            .orElseThrow(() -> new RuntimeException("Salary record not found"));
        
        return pdfGenerationService.generatePayslip(salary);
    }
    
    public Double getMonthlyExpense(Integer month, Integer year) {
        Double expense = salaryRepository.getTotalSalaryExpenseByMonthAndYear(month, year);
        return expense != null ? expense : 0.0;
    }
}
