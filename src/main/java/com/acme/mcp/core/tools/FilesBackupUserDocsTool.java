package com.acme.mcp.core.tools;

import com.acme.mcp.core.tool.*;
import com.acme.mcp.core.schema.JsonSchemas;
import com.acme.mcp.core.security.Allowlist;
import com.acme.mcp.core.windows.PowerShellRunner;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Tool to backup user documents to a ZIP file.
 */
public class FilesBackupUserDocsTool extends PowerShellToolBase {
    
    private static final String TOOL_NAME = "files.backup_user_docs";
    private static final String TOOL_DESCRIPTION = "Backup user documents to a ZIP file";
    private static final List<String> OS_SUPPORT = List.of("windows");
    
    public FilesBackupUserDocsTool() {
        super(TOOL_NAME, TOOL_DESCRIPTION, true, OS_SUPPORT, createJsonSchema());
    }
    
    /**
     * Constructor for testing with custom PowerShellRunner.
     */
    public FilesBackupUserDocsTool(PowerShellRunner powerShellRunner, Allowlist allowlist) {
        super(TOOL_NAME, TOOL_DESCRIPTION, true, OS_SUPPORT, createJsonSchema(), powerShellRunner, allowlist);
    }
    
    @Override
    public ExecuteResult execute(ExecutionContext context, JsonNode args) {
        String user = args.has("user") ? args.get("user").asText() : "$env:USERNAME";
        String destZip = args.has("destZip") ? args.get("destZip").asText() : "C:\\Temp\\user-docs.zip";
        boolean includeDesktop = args.has("includeDesktop") ? args.get("includeDesktop").asBoolean() : true;
        boolean includeDocuments = args.has("includeDocuments") ? args.get("includeDocuments").asBoolean() : true;
        boolean includeDownloads = args.has("includeDownloads") ? args.get("includeDownloads").asBoolean() : false;
        
        // Build paths to collect
        StringBuilder pathsToBackup = new StringBuilder();
        
        if (includeDesktop) {
            pathsToBackup.append("\"$env:USERPROFILE\\Desktop\"");
        }
        if (includeDocuments) {
            if (pathsToBackup.length() > 0) pathsToBackup.append(", ");
            pathsToBackup.append("\"$env:USERPROFILE\\Documents\"");
        }
        if (includeDownloads) {
            if (pathsToBackup.length() > 0) pathsToBackup.append(", ");
            pathsToBackup.append("\"$env:USERPROFILE\\Downloads\"");
        }
        
        // Create backup command
        String backupCommand = String.format(
            "Compress-Archive -Path %s -DestinationPath \"%s\" -Force",
            pathsToBackup.toString(),
            destZip
        );
        
        List<String> commands = List.of(backupCommand);
        
        return runPs(commands, context, Map.of(
            "user", user,
            "destZip", destZip,
            "includeDesktop", includeDesktop,
            "includeDocuments", includeDocuments,
            "includeDownloads", includeDownloads
        ));
    }
    
    private static JsonNode createJsonSchema() {
        return JsonSchemas.object(
            Map.of(
                "user", JsonSchemas.string("User name; defaults to current user"),
                "destZip", JsonSchemas.string("Destination ZIP file path"),
                "includeDesktop", JsonSchemas.bool(true),
                "includeDocuments", JsonSchemas.bool(true),
                "includeDownloads", JsonSchemas.bool(false)
            ),
            List.of()
        );
    }
}
