package net.javaguides.spring.boot.repository;

import net.javaguides.spring.boot.entity.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByEmployeeId(Long employeeId);
    List<Leave> findByStatus(Leave.Status status);
    List<Leave> findByEmployeeIdAndStatus(Long employeeId, Leave.Status status);
    List<Leave> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    Long countByEmployeeIdAndStatus(Long employeeId, Leave.Status status);
    Long countByStatus(Leave.Status status);
}
