package net.javaguides.spring.boot.repository;

import net.javaguides.spring.boot.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>  {
    List<Employee> findByDepartmentId(Long departmentId);
    List<Employee> findByStatus(Employee.Status status);
    Optional<Employee> findByEmployeeCode(String employeeCode);
    Optional<Employee> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmployeeCode(String employeeCode);
    List<Employee> findByFirstNameContainingOrLastNameContainingOrEmailContaining(
        String firstName, String lastName, String email);
    Long countByStatus(Employee.Status status);
    Long countByDepartmentId(Long departmentId);
}
