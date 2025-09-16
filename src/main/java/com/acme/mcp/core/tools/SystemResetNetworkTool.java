package com.acme.mcp.core.tools;

import com.acme.mcp.core.audit.AuditLogger;
import com.acme.mcp.core.tool.*;
import com.acme.mcp.core.windows.PowerShellRunner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Map;

/**
 * Tool to reset network configuration on Windows systems.
 * Executes network reset commands to resolve connectivity issues.
 */
public class SystemResetNetworkTool implements Tool {
    
    private static final String TOOL_NAME = "system_reset_network";
    private static final String TOOL_DESCRIPTION = "Reset network configuration to resolve connectivity issues";
    private static final List<String> OS_SUPPORT = List.of("windows");
    
    private final PowerShellRunner powerShellRunner;
    private final ObjectMapper objectMapper;
    
    public SystemResetNetworkTool() {
        this.powerShellRunner = new PowerShellRunner();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public String getName() {
        return TOOL_NAME;
    }
    
    @Override
    public String getDescription() {
        return TOOL_DESCRIPTION;
    }
    
    @Override
    public JsonNode getJsonSchema() {
        // Empty schema - this tool doesn't require parameters
        return objectMapper.createObjectNode();
    }
    
    @Override
    public boolean requiresConfirmation() {
        return true;
    }
    
    @Override
    public List<String> getOsSupport() {
        return OS_SUPPORT;
    }
    
    @Override
    public ExecuteResult execute(ExecutionContext context, JsonNode args) {
        // Log execution start
        AuditLogger.info("tool_execution_started", Map.of(
            "tool", getName(),
            "userId", context.getUserId(),
            "assetId", context.getAssetId(),
            "correlationId", context.getCorrelationId()
        ));
        
        try {
            // Execute network reset commands
            String[] commands = {
                "ipconfig /flushdns",
                "netsh winsock reset",
                "netsh int ip reset"
            };
            
            ExecuteResult result = powerShellRunner.execute(commands);
            
            // Log execution result
            AuditLogger.info("tool_execution_completed", Map.of(
                "tool", getName(),
                "userId", context.getUserId(),
                "assetId", context.getAssetId(),
                "correlationId", context.getCorrelationId(),
                "executionId", result.getExecutionId(),
                "exitCode", result.getExitCode(),
                "status", result.getStatus().toString(),
                "success", result.isSuccess()
            ));
            
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
}
