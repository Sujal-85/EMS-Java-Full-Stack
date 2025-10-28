package net.javaguides.spring.boot.service;

import net.javaguides.spring.boot.entity.AuditLog;
import net.javaguides.spring.boot.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    public void log(String action, String entityName, Long entityId, 
                    String details, String performedBy, String ipAddress) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setEntityName(entityName);
        auditLog.setEntityId(entityId);
        auditLog.setDetails(details);
        auditLog.setPerformedBy(performedBy);
        auditLog.setIpAddress(ipAddress);
        
        auditLogRepository.save(auditLog);
    }
    
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
    
    public List<AuditLog> getLogsByUser(String email) {
        return auditLogRepository.findByPerformedBy(email);
    }
    
    public List<AuditLog> getLogsByEntity(String entityName, Long entityId) {
        return auditLogRepository.findByEntityNameAndEntityId(entityName, entityId);
    }
}
