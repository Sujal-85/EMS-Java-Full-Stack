package net.javaguides.spring.boot.service;

import net.javaguides.spring.boot.entity.Attendance;
import net.javaguides.spring.boot.model.Employee;
import net.javaguides.spring.boot.repository.AttendanceRepository;
import net.javaguides.spring.boot.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private AuditLogService auditLogService;
    
    public Attendance markCheckIn(Long employeeId, String performedBy, String ipAddress) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        LocalDate today = LocalDate.now();
        Optional<Attendance> existing = attendanceRepository.findByEmployeeIdAndDate(employeeId, today);
        
        if (existing.isPresent()) {
            throw new RuntimeException("Already checked in today");
        }
        
        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setDate(today);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setStatus(Attendance.Status.PRESENT);
        
        Attendance saved = attendanceRepository.save(attendance);
        
        auditLogService.log("CREATE", "Attendance", saved.getId(),
            "Check-in recorded", performedBy, ipAddress);
        
        return saved;
    }
    
    public Attendance markCheckOut(Long employeeId, String performedBy, String ipAddress) {
        LocalDate today = LocalDate.now();
        Attendance attendance = attendanceRepository.findByEmployeeIdAndDate(employeeId, today)
            .orElseThrow(() -> new RuntimeException("No check-in found for today"));
        
        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException("Already checked out today");
        }
        
        LocalDateTime checkOut = LocalDateTime.now();
        attendance.setCheckOutTime(checkOut);
        
        // Calculate working hours
        if (attendance.getCheckInTime() != null) {
            Duration duration = Duration.between(attendance.getCheckInTime(), checkOut);
            double hours = duration.toMinutes() / 60.0;
            attendance.setWorkingHours(Math.round(hours * 100.0) / 100.0);
        }
        
        Attendance updated = attendanceRepository.save(attendance);
        
        auditLogService.log("UPDATE", "Attendance", updated.getId(),
            "Check-out recorded", performedBy, ipAddress);
        
        return updated;
    }
    
    public List<Attendance> getAttendanceByEmployee(Long employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId);
    }
    
    public List<Attendance> getAttendanceByDateRange(LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByDateBetween(startDate, endDate);
    }
    
    public Long getTodayPresentCount() {
        return attendanceRepository.countPresentEmployeesByDate(LocalDate.now());
    }
}
