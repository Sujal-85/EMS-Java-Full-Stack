package net.javaguides.spring.boot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.javaguides.spring.boot.model.Employee;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "salaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Salary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @Column(nullable = false)
    private Double basicSalary;
    
    private Double hra; // House Rent Allowance
    
    private Double transportAllowance;
    
    private Double medicalAllowance;
    
    private Double otherAllowances;
    
    private Double bonus;
    
    private Double deductions;
    
    @Column(nullable = false)
    private Double netSalary;
    
    @Column(nullable = false)
    private Integer month; // 1-12
    
    @Column(nullable = false)
    private Integer year;
    
    @Column(nullable = false)
    private LocalDate paymentDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(length = 500)
    private String remarks;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    public enum PaymentStatus {
        PENDING, PAID, CANCELLED
    }
}
