package com.acme.mcp.core.schema;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Schema definition for a tool.
 * Contains metadata about tool name, description, and parameter schema.
 */
public class ToolSchema {
    
    private final String name;
    private final String description;
    private final JsonNode parameters;
    
    public ToolSchema(String name, String description, JsonNode parameters) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public JsonNode getParameters() {
        return parameters;
    }
    
    @Override
    public String toString() {
        return "ToolSchema{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", parameters=" + parameters +
                '}';
    }
}
