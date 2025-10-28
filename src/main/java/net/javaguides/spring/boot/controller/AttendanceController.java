package net.javaguides.spring.boot.controller;

import jakarta.servlet.http.HttpServletRequest;
import net.javaguides.spring.boot.entity.Attendance;
import net.javaguides.spring.boot.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    
    @Autowired
    private AttendanceService attendanceService;
    
    @PostMapping("/checkin/{employeeId}")
    public ResponseEntity<Attendance> markCheckIn(
            @PathVariable Long employeeId,
            Authentication authentication,
            HttpServletRequest request) {
        String performedBy = authentication.getName();
        String ipAddress = request.getRemoteAddr();
        Attendance attendance = attendanceService.markCheckIn(employeeId, performedBy, ipAddress);
        return ResponseEntity.ok(attendance);
    }
    
    @PostMapping("/checkout/{employeeId}")
    public ResponseEntity<Attendance> markCheckOut(
            @PathVariable Long employeeId,
            Authentication authentication,
            HttpServletRequest request) {
        String performedBy = authentication.getName();
        String ipAddress = request.getRemoteAddr();
        Attendance attendance = attendanceService.markCheckOut(employeeId, performedBy, ipAddress);
        return ResponseEntity.ok(attendance);
    }
    
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Attendance>> getAttendanceByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByEmployee(employeeId));
    }
    
    @GetMapping("/range")
    public ResponseEntity<List<Attendance>> getAttendanceByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(attendanceService.getAttendanceByDateRange(startDate, endDate));
    }
    
    @GetMapping("/today-present")
    public ResponseEntity<Long> getTodayPresentCount() {
        return ResponseEntity.ok(attendanceService.getTodayPresentCount());
    }
}
