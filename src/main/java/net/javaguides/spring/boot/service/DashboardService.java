package net.javaguides.spring.boot.service;

import net.javaguides.spring.boot.dto.DashboardStats;
import net.javaguides.spring.boot.entity.Attendance;
import net.javaguides.spring.boot.entity.Leave;
import net.javaguides.spring.boot.model.Employee;
import net.javaguides.spring.boot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class DashboardService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private LeaveRepository leaveRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private SalaryRepository salaryRepository;
    
    public DashboardStats getDashboardStats() {
        Long totalEmployees = employeeRepository.count();
        Long activeEmployees = employeeRepository.countByStatus(Employee.Status.ACTIVE);
        Long inactiveEmployees = employeeRepository.countByStatus(Employee.Status.INACTIVE);
        Long onLeaveEmployees = employeeRepository.countByStatus(Employee.Status.ON_LEAVE);
        Long presentToday = attendanceRepository.countPresentEmployeesByDate(LocalDate.now());
        Long pendingLeaves = leaveRepository.countByStatus(Leave.Status.PENDING);
        Long totalDepartments = departmentRepository.count();
        
        YearMonth currentMonth = YearMonth.now();
        Double monthlySalaryExpense = salaryRepository.getTotalSalaryExpenseByMonthAndYear(
            currentMonth.getMonthValue(), currentMonth.getYear());
        
        return new DashboardStats(
            totalEmployees,
            activeEmployees,
            inactiveEmployees,
            onLeaveEmployees,
            presentToday,
            pendingLeaves,
            totalDepartments,
            monthlySalaryExpense != null ? monthlySalaryExpense : 0.0
        );
    }
}
