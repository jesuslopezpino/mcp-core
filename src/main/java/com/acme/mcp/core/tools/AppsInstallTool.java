package com.acme.mcp.core.tools;

import com.acme.mcp.core.tool.*;
import com.acme.mcp.core.schema.JsonSchemas;
import com.acme.mcp.core.security.Allowlist;
import com.acme.mcp.core.windows.PowerShellRunner;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Tool to install applications using Windows Package Manager (winget).
 */
public class AppsInstallTool extends PowerShellToolBase {
    
    private static final String TOOL_NAME = "apps.install";
    private static final String TOOL_DESCRIPTION = "Install applications using Windows Package Manager (winget)";
    private static final List<String> OS_SUPPORT = List.of("windows");
    
    public AppsInstallTool() {
        super(TOOL_NAME, TOOL_DESCRIPTION, true, OS_SUPPORT, createJsonSchema());
    }
    
    /**
     * Constructor for testing with custom PowerShellRunner.
     */
    public AppsInstallTool(PowerShellRunner powerShellRunner, Allowlist allowlist) {
        super(TOOL_NAME, TOOL_DESCRIPTION, true, OS_SUPPORT, createJsonSchema(), powerShellRunner, allowlist);
    }
    
    @Override
    public ExecuteResult execute(ExecutionContext context, JsonNode args) {
        // Validate required fields
        validateRequiredFields(args, List.of("name"));
        
        String name = args.get("name").asText();
        boolean silent = args.has("silent") ? args.get("silent").asBoolean() : true;
        
        // Build winget command
        StringBuilder command = new StringBuilder("winget install --accept-source-agreements --accept-package-agreements ");
        command.append(name);
        
        if (silent) {
            command.append(" --silent");
        }
        
        List<String> commands = List.of(command.toString());
        
        return runPs(commands, context, Map.of(
            "packageName", name,
            "silent", silent
        ));
    }
    
    private static JsonNode createJsonSchema() {
        return JsonSchemas.object(
            Map.of(
                "name", JsonSchemas.string("Id/name in Winget"),
                "silent", JsonSchemas.bool(true)
            ),
            List.of("name")
        );
    }
}
