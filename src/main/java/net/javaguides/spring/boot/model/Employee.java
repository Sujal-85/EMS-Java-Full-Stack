package net.javaguides.spring.boot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import net.javaguides.spring.boot.entity.Attendance;
import net.javaguides.spring.boot.entity.Department;
import net.javaguides.spring.boot.entity.Leave;
import net.javaguides.spring.boot.entity.Salary;
import net.javaguides.spring.boot.entity.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "employee_code", unique = true, nullable = false)
	private String employeeCode;
	
	@Column(name = "first_name", nullable = false)
	private String firstName;
	
	@Column(name = "last_name", nullable = false)
	private String lastName;
	
	@Column(name = "email", unique = true, nullable = false)
	private String email;
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column(name = "date_of_birth")
	private LocalDate dateOfBirth;
	
	@Column(name = "date_of_joining")
	private LocalDate dateOfJoining;
	
	@Column(name = "designation")
	private String designation;
	
	@Column(name = "photo_url")
	private String photoUrl;
	
	@Column(name = "address")
	private String address;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status = Status.ACTIVE;
	
	@ManyToOne
	@JoinColumn(name = "department_id")
	private Department department;
	
	@OneToOne
	@JoinColumn(name = "user_id", unique = true)
	@JsonManagedReference
	private User user;
	
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Attendance> attendances;
	
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Leave> leaves;
	
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<Salary> salaries;
	
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	public enum Status {
		ACTIVE, INACTIVE, ON_LEAVE, TERMINATED
	}
}
