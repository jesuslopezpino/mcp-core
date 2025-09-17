package com.acme.mcp.core.tools;

import com.acme.mcp.core.tool.*;
import com.acme.mcp.core.security.Allowlist;
import com.acme.mcp.core.windows.PowerShellRunner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * Tool to repair Microsoft Teams by killing processes and clearing cache.
 */
public class AppsRepairTeamsTool extends PowerShellToolBase {
    
    private static final String TOOL_NAME = "apps.repair_teams";
    private static final String TOOL_DESCRIPTION = "Repair Microsoft Teams by killing processes and clearing cache";
    private static final List<String> OS_SUPPORT = List.of("windows");
    
    public AppsRepairTeamsTool() {
        super(TOOL_NAME, TOOL_DESCRIPTION, false, OS_SUPPORT, createJsonSchema());
    }
    
    /**
     * Constructor for testing with custom PowerShellRunner.
     */
    public AppsRepairTeamsTool(PowerShellRunner powerShellRunner, Allowlist allowlist) {
        super(TOOL_NAME, TOOL_DESCRIPTION, false, OS_SUPPORT, createJsonSchema(), powerShellRunner, allowlist);
    }
    
    @Override
    public ExecuteResult execute(ExecutionContext context, JsonNode args) {
        // This tool doesn't require parameters
        
        List<String> commands = List.of(
            "taskkill /IM Teams.exe /F",
            "Remove-Item -Recurse -Force \"$env:APPDATA\\Microsoft\\Teams\"",
            "Start-Process \"$env:LOCALAPPDATA\\Microsoft\\Teams\\Update.exe\" --processStart \"Teams.exe\""
        );
        
        return runPs(commands, context, Map.of());
    }
    
    private static JsonNode createJsonSchema() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.createObjectNode();
    }
}
