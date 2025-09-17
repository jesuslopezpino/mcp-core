package com.acme.mcp.core.tools;

import com.acme.mcp.core.tool.*;
import com.acme.mcp.core.security.Allowlist;
import com.acme.mcp.core.windows.PowerShellRunner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * Tool to clear temporary files from system and user temp directories.
 */
public class SystemClearTempTool extends PowerShellToolBase {
    
    private static final String TOOL_NAME = "system.clear_temp";
    private static final String TOOL_DESCRIPTION = "Clear temporary files from system and user temp directories";
    private static final List<String> OS_SUPPORT = List.of("windows");
    
    public SystemClearTempTool() {
        super(TOOL_NAME, TOOL_DESCRIPTION, true, OS_SUPPORT, createJsonSchema());
    }
    
    /**
     * Constructor for testing with custom PowerShellRunner.
     */
    public SystemClearTempTool(PowerShellRunner powerShellRunner, Allowlist allowlist) {
        super(TOOL_NAME, TOOL_DESCRIPTION, true, OS_SUPPORT, createJsonSchema(), powerShellRunner, allowlist);
    }
    
    @Override
    public ExecuteResult execute(ExecutionContext context, JsonNode args) {
        // This tool doesn't require parameters
        
        List<String> commands = List.of(
            "Remove-Item -Recurse -Force \"$env:TEMP\\*\"",
            "Remove-Item -Recurse -Force \"C:\\Windows\\Temp\\*\""
        );
        
        return runPs(commands, context, Map.of());
    }
    
    private static JsonNode createJsonSchema() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.createObjectNode();
    }
}
