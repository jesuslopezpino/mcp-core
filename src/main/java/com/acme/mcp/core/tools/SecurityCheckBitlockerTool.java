package com.acme.mcp.core.tools;

import com.acme.mcp.core.tool.*;
import com.acme.mcp.core.security.Allowlist;
import com.acme.mcp.core.windows.PowerShellRunner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * Tool to check BitLocker encryption status on all volumes.
 */
public class SecurityCheckBitlockerTool extends PowerShellToolBase {
    
    private static final String TOOL_NAME = "security.check_bitlocker";
    private static final String TOOL_DESCRIPTION = "Check BitLocker encryption status on all volumes";
    private static final List<String> OS_SUPPORT = List.of("windows");
    
    public SecurityCheckBitlockerTool() {
        super(TOOL_NAME, TOOL_DESCRIPTION, false, OS_SUPPORT, createJsonSchema());
    }
    
    /**
     * Constructor for testing with custom PowerShellRunner.
     */
    public SecurityCheckBitlockerTool(PowerShellRunner powerShellRunner, Allowlist allowlist) {
        super(TOOL_NAME, TOOL_DESCRIPTION, false, OS_SUPPORT, createJsonSchema(), powerShellRunner, allowlist);
    }
    
    @Override
    public ExecuteResult execute(ExecutionContext context, JsonNode args) {
        // This tool doesn't require parameters
        
        List<String> commands = List.of(
            "Get-BitLockerVolume | Select-Object MountPoint, ProtectionStatus, VolumeStatus, EncryptionMethod, PercentageEncrypted"
        );
        
        ExecuteResult result = runPs(commands, context, Map.of());
        
        // Try to parse output as JSON (optional)
        boolean parsed = false;
        try {
            if (result.getStdout() != null && !result.getStdout().trim().isEmpty()) {
                // Simple check if output looks like structured data
                String output = result.getStdout().trim();
                parsed = output.contains("MountPoint") && output.contains("ProtectionStatus");
            }
        } catch (Exception e) {
            // Ignore parsing errors
        }
        
        // Add parsing result to audit data
        return runPs(commands, context, Map.of(
            "parsed", parsed
        ));
    }
    
    private static JsonNode createJsonSchema() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.createObjectNode();
    }
}
