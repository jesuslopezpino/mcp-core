package com.acme.mcp.core.tool;

import com.acme.mcp.core.audit.AuditLogger;
import com.acme.mcp.core.security.Allowlist;
import com.acme.mcp.core.windows.PowerShellRunner;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Abstract base class for PowerShell-based tools.
 * Provides common functionality for command execution, auditing, and validation.
 */
public abstract class PowerShellToolBase implements Tool {
    
    private final String name;
    private final String description;
    private final boolean requiresConfirmation;
    private final List<String> osSupport;
    private final JsonNode jsonSchema;
    private final PowerShellRunner powerShellRunner;
    private final Allowlist allowlist;
    
    /**
     * Create a PowerShell tool with default PowerShellRunner.
     * @param name tool name
     * @param description tool description
     * @param requiresConfirmation whether user confirmation is required
     * @param osSupport supported operating systems
     * @param jsonSchema JSON schema for parameters
     */
    protected PowerShellToolBase(String name, String description, boolean requiresConfirmation,
                               List<String> osSupport, JsonNode jsonSchema) {
        this(name, description, requiresConfirmation, osSupport, jsonSchema, 
             new PowerShellRunner(), new Allowlist());
    }
    
    /**
     * Create a PowerShell tool with custom PowerShellRunner (for testing).
     * @param name tool name
     * @param description tool description
     * @param requiresConfirmation whether user confirmation is required
     * @param osSupport supported operating systems
     * @param jsonSchema JSON schema for parameters
     * @param powerShellRunner custom PowerShell runner
     * @param allowlist security allowlist
     */
    protected PowerShellToolBase(String name, String description, boolean requiresConfirmation,
                               List<String> osSupport, JsonNode jsonSchema,
                               PowerShellRunner powerShellRunner, Allowlist allowlist) {
        this.name = name;
        this.description = description;
        this.requiresConfirmation = requiresConfirmation;
        this.osSupport = osSupport;
        this.jsonSchema = jsonSchema;
        this.powerShellRunner = powerShellRunner;
        this.allowlist = allowlist;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public JsonNode getJsonSchema() {
        return jsonSchema;
    }
    
    @Override
    public boolean requiresConfirmation() {
        return requiresConfirmation;
    }
    
    @Override
    public List<String> getOsSupport() {
        return osSupport;
    }
    
    /**
     * Execute PowerShell commands with auditing and validation.
     * @param commands list of PowerShell commands to execute
     * @param context execution context
     * @param auditExtra additional audit data
     * @return execution result
     */
    protected ExecuteResult runPs(List<String> commands, ExecutionContext context, Map<String, Object> auditExtra) {
        // Validate commands against allowlist
        for (String command : commands) {
            if (!allowlist.isCommandAllowed(command)) {
                AuditLogger.info("tool_execution_blocked", Map.of(
                    "tool", getName(),
                    "command", command
                ));
                throw new SecurityException("Command not allowed: " + command);
            }
        }
        
        // Log execution start with detailed command info
        Map<String, Object> startLog = new java.util.HashMap<>(Map.of(
            "tool", getName(),
            "userId", context.getUserId(),
            "assetId", context.getAssetId(),
            "correlationId", context.getCorrelationId(),
            "commands", commands,
            "commandCount", commands.size(),
            "dryRun", powerShellRunner.toString().contains("dryRun=true")
        ));
        startLog.putAll(auditExtra);
        AuditLogger.info("tool_execution_started", startLog);
        
        try {
            // Execute commands
            ExecuteResult result = powerShellRunner.execute(commands.toArray(new String[0]));
            
            // Prepare audit data with output previews
            Map<String, Object> auditData = new java.util.HashMap<>(Map.of(
                "tool", getName(),
                "userId", context.getUserId(),
                "assetId", context.getAssetId(),
                "correlationId", context.getCorrelationId(),
                "executionId", result.getExecutionId(),
                "exitCode", result.getExitCode(),
                "status", result.getStatus().toString(),
                "success", result.isSuccess(),
                "stdoutLen", result.getStdout() != null ? result.getStdout().length() : 0,
                "stderrLen", result.getStderr() != null ? result.getStderr().length() : 0
            ));
            
            // Add output previews for debugging
            if (result.getStdout() != null && !result.getStdout().isEmpty()) {
                String stdoutPreview = result.getStdout().length() > 200 
                    ? result.getStdout().substring(0, 200) + "... [TRUNCATED]"
                    : result.getStdout();
                auditData.put("stdoutPreview", stdoutPreview);
            }
            
            if (result.getStderr() != null && !result.getStderr().isEmpty()) {
                String stderrPreview = result.getStderr().length() > 200 
                    ? result.getStderr().substring(0, 200) + "... [TRUNCATED]"
                    : result.getStderr();
                auditData.put("stderrPreview", stderrPreview);
            }
            
            // Add extra audit data if provided
            if (auditExtra != null && !auditExtra.isEmpty()) {
                // Create mutable map for combining data
                java.util.HashMap<String, Object> combinedData = new java.util.HashMap<>(auditData);
                combinedData.putAll(auditExtra);
                auditData = combinedData;
            }
            
            // Log execution completion
            AuditLogger.info("tool_execution_completed", auditData);
            
            return result;
            
        } catch (Exception e) {
            // Log execution error
            AuditLogger.info("tool_execution_error", Map.of(
                "tool", getName(),
                "userId", context.getUserId(),
                "assetId", context.getAssetId(),
                "correlationId", context.getCorrelationId(),
                "error", e.getMessage()
            ));
            
            return new ExecuteResult(
                java.util.UUID.randomUUID().toString(),
                -1,
                "",
                "Tool execution failed: " + e.getMessage(),
                ExecuteResult.Status.ERROR
            );
        }
    }
    
    /**
     * Validate required parameters from JSON arguments.
     * @param args JSON arguments
     * @param requiredFields list of required field names
     * @throws IllegalArgumentException if required fields are missing
     */
    protected void validateRequiredFields(JsonNode args, List<String> requiredFields) {
        for (String field : requiredFields) {
            if (!args.has(field) || args.get(field).isNull()) {
                AuditLogger.info("tool_validation_error", Map.of(
                    "tool", getName(),
                    "missingField", field,
                    "providedFields", java.util.Arrays.asList(args.fieldNames())
                ));
                throw new IllegalArgumentException("Required field missing: " + field);
            }
        }
    }
}
