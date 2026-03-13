package com.example.crm.service.impl;

import com.example.crm.entity.AuditAction;
import com.example.crm.entity.AuditLog;
import com.example.crm.entity.User;
import com.example.crm.exception.ResourceNotFoundException;
import com.example.crm.repository.AuditLogRepository;
import com.example.crm.repository.UserRepository;
import com.example.crm.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    public void logCreate(String entityName, Long entityId) {
        save(entityName, entityId, AuditAction.CREATE, null, null, null);
    }

    @Override
    public void logUpdate(String entityName, Long entityId, String fieldName, String oldValue, String newValue) {
        save(entityName, entityId, AuditAction.UPDATE, fieldName, oldValue, newValue);
    }

    @Override
    public void logDelete(String entityName, Long entityId) {
        save(entityName, entityId, AuditAction.DELETE, null, null, null);
    }

    private void save(String entityName, Long entityId, AuditAction action,
                      String fieldName, String oldValue, String newValue) {
        User currentUser = getCurrentUser();
        AuditLog log = AuditLog.builder()
                .entityName(entityName)
                .entityId(entityId)
                .action(action)
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .changedBy(currentUser)
                .build();
        auditLogRepository.save(log);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResourceNotFoundException("No authenticated user found");
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}