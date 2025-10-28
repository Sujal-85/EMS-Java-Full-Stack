package net.javaguides.spring.boot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String action; // CREATE, UPDATE, DELETE, LOGIN, LOGOUT
    
    @Column(nullable = false)
    private String entityName; // Employee, Leave, Salary, etc.
    
    private Long entityId;
    
    @Column(nullable = false)
    private String performedBy; // Email of user who performed action
    
    @Column(length = 2000)
    private String details; // JSON or description of changes
    
    @Column(nullable = false)
    private String ipAddress;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime timestamp;
}
