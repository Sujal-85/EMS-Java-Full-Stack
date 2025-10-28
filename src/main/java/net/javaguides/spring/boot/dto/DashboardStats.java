package net.javaguides.spring.boot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardStats {
    private Long totalEmployees;
    private Long activeEmployees;
    private Long inactiveEmployees;
    private Long onLeaveEmployees;
    private Long presentToday;
    private Long pendingLeaves;
    private Long totalDepartments;
    private Double monthlySalaryExpense;
}
