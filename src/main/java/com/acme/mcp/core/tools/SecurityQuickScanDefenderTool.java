package com.acme.mcp.core.tools;

import com.acme.mcp.core.tool.*;
import com.acme.mcp.core.schema.JsonSchemas;
import com.acme.mcp.core.security.Allowlist;
import com.acme.mcp.core.windows.PowerShellRunner;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Tool to perform quick or full scan using Windows Defender.
 */
public class SecurityQuickScanDefenderTool extends PowerShellToolBase {
    
    private static final String TOOL_NAME = "security.quick_scan_defender";
    private static final String TOOL_DESCRIPTION = "Perform quick or full scan using Windows Defender";
    private static final List<String> OS_SUPPORT = List.of("windows");
    
    public SecurityQuickScanDefenderTool() {
        super(TOOL_NAME, TOOL_DESCRIPTION, true, OS_SUPPORT, createJsonSchema());
    }
    
    /**
     * Constructor for testing with custom PowerShellRunner.
     */
    public SecurityQuickScanDefenderTool(PowerShellRunner powerShellRunner, Allowlist allowlist) {
        super(TOOL_NAME, TOOL_DESCRIPTION, true, OS_SUPPORT, createJsonSchema(), powerShellRunner, allowlist);
    }
    
    @Override
    public ExecuteResult execute(ExecutionContext context, JsonNode args) {
        String scanType = args.has("scanType") ? args.get("scanType").asText() : "QuickScan";
        
        // First, get computer status (read-only)
        List<String> commands = List.of(
            "Get-MpComputerStatus",
            "Start-MpScan -ScanType " + scanType
        );
        
        return runPs(commands, context, Map.of(
            "scanType", scanType
        ));
    }
    
    private static JsonNode createJsonSchema() {
        return JsonSchemas.object(
            Map.of(
                "scanType", JsonSchemas.stringEnum(List.of("QuickScan", "FullScan"), "QuickScan")
            ),
            List.of()
        );
    }
}
