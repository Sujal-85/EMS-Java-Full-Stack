package net.javaguides.spring.boot.repository;

import net.javaguides.spring.boot.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByEmployeeId(Long employeeId);
    Optional<Salary> findByEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);
    List<Salary> findByMonthAndYear(Integer month, Integer year);
    List<Salary> findByPaymentStatus(Salary.PaymentStatus status);
    
    @Query("SELECT SUM(s.netSalary) FROM Salary s WHERE s.month = :month AND s.year = :year")
    Double getTotalSalaryExpenseByMonthAndYear(Integer month, Integer year);
}
