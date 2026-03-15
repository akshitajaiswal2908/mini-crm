package com.example.crm.service;

public interface AuditLogService {
    void logCreate(String entityName, Long entityId);
    void logUpdate(String entityName, Long entityId, String fieldName, String oldValue, String newValue);
    void logDelete(String entityName, Long entityId);
}