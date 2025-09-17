package com.acme.mcp.core.tool;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * Contract for executable tools in the MCP system.
 * Tools represent actions that can be performed on target systems.
 */
public interface Tool {
    
    /**
     * Unique name identifier for this tool.
     * @return tool name
     */
    String getName();
    
    /**
     * Human-readable description of what this tool does.
     * @return tool description
     */
    String getDescription();
    
    /**
     * JSON Schema defining the parameters this tool accepts.
     * @return JSON schema as JsonNode
     */
    JsonNode getJsonSchema();
    
    /**
     * Whether this tool requires user confirmation before execution.
     * @return true if confirmation is required
     */
    boolean requiresConfirmation();
    
    /**
     * List of operating systems this tool supports.
     * @return list of OS identifiers (e.g., "windows", "linux", "macos")
     */
    List<String> getOsSupport();
    
    /**
     * Execute this tool with the given context and arguments.
     * @param context execution context containing user, asset, and correlation info
     * @param args tool arguments as JSON
     * @return execution result
     */
    ExecuteResult execute(ExecutionContext context, JsonNode args);
    
    /**
     * Get alternative names (aliases) for this tool.
     * @return list of alias names (empty by default)
     */
    default List<String> aliases() {
        return List.of();
    }
}
