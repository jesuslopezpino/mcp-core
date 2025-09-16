package com.acme.mcp.core.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Structured audit logger for MCP operations.
 * Logs events in JSON format for easy parsing and analysis.
 */
public class AuditLogger {
    
    private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger("AUDIT");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    /**
     * Log an audit event with structured data.
     * @param event event type/name
     * @param data additional event data
     */
    public static void info(String event, Map<String, Object> data) {
        try {
            ObjectNode logEntry = OBJECT_MAPPER.createObjectNode();
            logEntry.put("event", event);
            logEntry.put("timestamp", System.currentTimeMillis());
            
            // Add all data fields to the log entry
            if (data != null) {
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    
                    if (value == null) {
                        logEntry.putNull(key);
                    } else if (value instanceof String) {
                        logEntry.put(key, (String) value);
                    } else if (value instanceof Number) {
                        Number num = (Number) value;
                        if (value instanceof Integer) {
                            logEntry.put(key, num.intValue());
                        } else if (value instanceof Long) {
                            logEntry.put(key, num.longValue());
                        } else if (value instanceof Double) {
                            logEntry.put(key, num.doubleValue());
                        } else if (value instanceof Float) {
                            logEntry.put(key, num.floatValue());
                        } else {
                            logEntry.put(key, num.doubleValue());
                        }
                    } else if (value instanceof Boolean) {
                        logEntry.put(key, (Boolean) value);
                    } else {
                        // Convert complex objects to JSON string
                        logEntry.put(key, OBJECT_MAPPER.writeValueAsString(value));
                    }
                }
            }
            
            AUDIT_LOGGER.info(OBJECT_MAPPER.writeValueAsString(logEntry));
            
        } catch (Exception e) {
            // Fallback to simple logging if JSON serialization fails
            AUDIT_LOGGER.error("Failed to serialize audit log entry: {}", e.getMessage());
            AUDIT_LOGGER.info("AUDIT: event={}, data={}", event, data);
        }
    }
    
    /**
     * Log an audit event with a single key-value pair.
     * @param event event type/name
     * @param key data key
     * @param value data value
     */
    public static void info(String event, String key, Object value) {
        info(event, Map.of(key, value));
    }
    
    /**
     * Log an audit event without additional data.
     * @param event event type/name
     */
    public static void info(String event) {
        info(event, null);
    }
}
