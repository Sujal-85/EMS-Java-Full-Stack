package net.javaguides.spring.boot.controller;

import net.javaguides.spring.boot.entity.AuditLog;
import net.javaguides.spring.boot.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {
    
    @Autowired
    private AuditLogService auditLogService;
    
    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }
    
    @GetMapping("/user/{email}")
    public ResponseEntity<List<AuditLog>> getLogsByUser(@PathVariable String email) {
        return ResponseEntity.ok(auditLogService.getLogsByUser(email));
    }
    
    @GetMapping("/entity/{entityName}/{entityId}")
    public ResponseEntity<List<AuditLog>> getLogsByEntity(
            @PathVariable String entityName,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(auditLogService.getLogsByEntity(entityName, entityId));
    }
}
