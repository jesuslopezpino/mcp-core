package com.acme.mcp.core.tools;

import com.acme.mcp.core.tool.*;
import com.acme.mcp.core.schema.JsonSchemas;
import com.acme.mcp.core.security.Allowlist;
import com.acme.mcp.core.windows.PowerShellRunner;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Tool to restart a Windows service.
 */
public class SystemRestartServiceTool extends PowerShellToolBase {
    
    private static final String TOOL_NAME = "system.restart_service";
    private static final String TOOL_DESCRIPTION = "Restart a Windows service";
    private static final List<String> OS_SUPPORT = List.of("windows");
    
    public SystemRestartServiceTool() {
        super(TOOL_NAME, TOOL_DESCRIPTION, true, OS_SUPPORT, createJsonSchema());
    }
    
    /**
     * Constructor for testing with custom PowerShellRunner.
     */
    public SystemRestartServiceTool(PowerShellRunner powerShellRunner, Allowlist allowlist) {
        super(TOOL_NAME, TOOL_DESCRIPTION, true, OS_SUPPORT, createJsonSchema(), powerShellRunner, allowlist);
    }
    
    @Override
    public ExecuteResult execute(ExecutionContext context, JsonNode args) {
        // Validate required fields
        validateRequiredFields(args, List.of("name"));
        
        String serviceName = args.get("name").asText();
        int timeoutSec = args.has("timeoutSec") ? args.get("timeoutSec").asInt() : 30;
        
        // Implement restart with stop/start
        List<String> commands = List.of(
            "Stop-Service -Name \"" + serviceName + "\" -Force",
            "Start-Service -Name \"" + serviceName + "\"",
            "Get-Service -Name \"" + serviceName + "\" | Select-Object Name, Status"
        );
        
        return runPs(commands, context, Map.of(
            "serviceName", serviceName,
            "timeoutSec", timeoutSec
        ));
    }
    
    private static JsonNode createJsonSchema() {
        return JsonSchemas.object(
            Map.of(
                "name", JsonSchemas.string("Service name"),
                "timeoutSec", JsonSchemas.integer(0, 120, 30)
            ),
            List.of("name")
        );
    }
}
