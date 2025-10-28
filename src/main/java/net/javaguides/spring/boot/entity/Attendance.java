package net.javaguides.spring.boot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.javaguides.spring.boot.model.Employee;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;
    
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PRESENT;
    
    @Column(length = 500)
    private String remarks;
    
    @Column(name = "working_hours")
    private Double workingHours;
    
    public enum Status {
        PRESENT, ABSENT, HALF_DAY, LATE, ON_LEAVE
    }
}
