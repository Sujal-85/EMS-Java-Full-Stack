package net.javaguides.spring.boot.service;

import net.javaguides.spring.boot.entity.Leave;
import net.javaguides.spring.boot.entity.User;
import net.javaguides.spring.boot.repository.LeaveRepository;
import net.javaguides.spring.boot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveService {
    
    @Autowired
    private LeaveRepository leaveRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuditLogService auditLogService;
    
    @Autowired
    private EmailService emailService;
    
    public List<Leave> getAllLeaves() {
        return leaveRepository.findAll();
    }
    
    public Leave getLeaveById(Long id) {
        return leaveRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Leave not found with id: " + id));
    }
    
    public Leave applyLeave(Leave leave, String performedBy, String ipAddress) {
        // Basic validations
        if (leave.getStartDate() == null || leave.getEndDate() == null) {
            throw new RuntimeException("Start date and end date are required");
        }
        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date");
        }
        if (leave.getEmployee() == null || leave.getEmployee().getId() == null) {
            throw new RuntimeException("Invalid employee for leave application");
        }

        // Calculate number of days
        long days = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
        leave.setNumberOfDays((int) days);
        leave.setStatus(Leave.Status.PENDING);

        // Business rules:
        // 1) No overlapping leaves for the same employee (including same day)
        // 2) Minimum gap of 3 days between consecutive leaves
        final int MIN_GAP_DAYS = 3; // adjust if you need 4

        List<Leave> existingLeaves = leaveRepository.findByEmployeeId(leave.getEmployee().getId());

        // Consider blocking statuses only (PENDING, APPROVED). REJECTED/CANCELLED do not block.
        existingLeaves.stream()
                .filter(l -> l.getStatus() == Leave.Status.PENDING || l.getStatus() == Leave.Status.APPROVED)
                .forEach(l -> {
                    // Overlap check: (existing.start <= new.end) && (existing.end >= new.start)
                    boolean overlaps = !l.getStartDate().isAfter(leave.getEndDate())
                            && !l.getEndDate().isBefore(leave.getStartDate());
                    if (overlaps) {
                        throw new RuntimeException("Overlapping leave exists between "
                                + l.getStartDate() + " and " + l.getEndDate());
                    }
                });

        // Enforce minimum gap before and after
        // Find nearest leave before the new start
        existingLeaves.stream()
                .filter(l -> (l.getStatus() == Leave.Status.PENDING || l.getStatus() == Leave.Status.APPROVED))
                .filter(l -> l.getEndDate().isBefore(leave.getStartDate()))
                .max((a, b) -> a.getEndDate().compareTo(b.getEndDate()))
                .ifPresent(prev -> {
                    long gap = ChronoUnit.DAYS.between(prev.getEndDate(), leave.getStartDate());
                    if (gap < MIN_GAP_DAYS) {
                        throw new RuntimeException("Minimum gap of " + MIN_GAP_DAYS
                                + " day(s) required after previous leave ending on " + prev.getEndDate());
                    }
                });

        // Find nearest leave after the new end
        existingLeaves.stream()
                .filter(l -> (l.getStatus() == Leave.Status.PENDING || l.getStatus() == Leave.Status.APPROVED))
                .filter(l -> l.getStartDate().isAfter(leave.getEndDate()))
                .min((a, b) -> a.getStartDate().compareTo(b.getStartDate()))
                .ifPresent(next -> {
                    long gap = ChronoUnit.DAYS.between(leave.getEndDate(), next.getStartDate());
                    if (gap < MIN_GAP_DAYS) {
                        throw new RuntimeException("Minimum gap of " + MIN_GAP_DAYS
                                + " day(s) required before next leave starting on " + next.getStartDate());
                    }
                });

        Leave savedLeave = leaveRepository.save(leave);
        
        auditLogService.log("CREATE", "Leave", savedLeave.getId(),
            "Leave applied: " + leave.getLeaveType() + " for " + days + " days",
            performedBy, ipAddress);
        
        return savedLeave;
    }
    
    public Leave approveLeave(Long id, String approverEmail, String comments, String ipAddress) {
        Leave leave = getLeaveById(id);
        User approver = userRepository.findByEmail(approverEmail)
            .orElseThrow(() -> new RuntimeException("Approver not found"));
        
        leave.setStatus(Leave.Status.APPROVED);
        leave.setApprovedBy(approver);
        leave.setApprovedAt(LocalDateTime.now());
        leave.setApproverComments(comments);
        
        Leave updatedLeave = leaveRepository.save(leave);
        
        auditLogService.log("UPDATE", "Leave", id,
            "Leave approved by " + approverEmail, approverEmail, ipAddress);
        
        emailService.sendLeaveStatusEmail(leave.getEmployee().getEmail(),
            leave.getEmployee().getFirstName() + " " + leave.getEmployee().getLastName(),
            "APPROVED", leave.getLeaveType().toString());
        
        return updatedLeave;
    }
    
    public Leave rejectLeave(Long id, String approverEmail, String comments, String ipAddress) {
        Leave leave = getLeaveById(id);
        User approver = userRepository.findByEmail(approverEmail)
            .orElseThrow(() -> new RuntimeException("Approver not found"));
        
        leave.setStatus(Leave.Status.REJECTED);
        leave.setApprovedBy(approver);
        leave.setApprovedAt(LocalDateTime.now());
        leave.setApproverComments(comments);
        
        Leave updatedLeave = leaveRepository.save(leave);
        
        auditLogService.log("UPDATE", "Leave", id,
            "Leave rejected by " + approverEmail, approverEmail, ipAddress);
        
        emailService.sendLeaveStatusEmail(leave.getEmployee().getEmail(),
            leave.getEmployee().getFirstName() + " " + leave.getEmployee().getLastName(),
            "REJECTED", leave.getLeaveType().toString());
        
        return updatedLeave;
    }
    
    public List<Leave> getLeavesByEmployee(Long employeeId) {
        return leaveRepository.findByEmployeeId(employeeId);
    }
    
    public List<Leave> getPendingLeaves() {
        return leaveRepository.findByStatus(Leave.Status.PENDING);
    }
    
    public Long countPendingLeaves() {
        return leaveRepository.countByStatus(Leave.Status.PENDING);
    }
}
