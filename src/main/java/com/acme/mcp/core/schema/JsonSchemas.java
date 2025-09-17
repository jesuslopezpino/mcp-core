package com.acme.mcp.core.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Map;

/**
 * Helper utilities for creating JSON schemas for tool parameters.
 */
public class JsonSchemas {
    
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    /**
     * Create an object schema with properties and required fields.
     * @param properties map of property names to their schemas
     * @param required list of required property names
     * @return JSON schema object node
     */
    public static ObjectNode object(Map<String, JsonNode> properties, List<String> required) {
        ObjectNode schema = OBJECT_MAPPER.createObjectNode();
        schema.put("type", "object");
        
        ObjectNode propertiesNode = OBJECT_MAPPER.createObjectNode();
        for (Map.Entry<String, JsonNode> entry : properties.entrySet()) {
            propertiesNode.set(entry.getKey(), entry.getValue());
        }
        schema.set("properties", propertiesNode);
        
        if (required != null && !required.isEmpty()) {
            ArrayNode requiredNode = OBJECT_MAPPER.createArrayNode();
            for (String req : required) {
                requiredNode.add(req);
            }
            schema.set("required", requiredNode);
        }
        
        return schema;
    }
    
    /**
     * Create a string schema.
     * @param description description of the string field
     * @return JSON schema object node
     */
    public static ObjectNode string(String description) {
        ObjectNode schema = OBJECT_MAPPER.createObjectNode();
        schema.put("type", "string");
        if (description != null) {
            schema.put("description", description);
        }
        return schema;
    }
    
    /**
     * Create a boolean schema with default value.
     * @param defaultVal default boolean value
     * @return JSON schema object node
     */
    public static ObjectNode bool(boolean defaultVal) {
        ObjectNode schema = OBJECT_MAPPER.createObjectNode();
        schema.put("type", "boolean");
        schema.put("default", defaultVal);
        return schema;
    }
    
    /**
     * Create an integer schema with min, max, and default values.
     * @param min minimum value
     * @param max maximum value
     * @param defaultVal default value
     * @return JSON schema object node
     */
    public static ObjectNode integer(int min, int max, int defaultVal) {
        ObjectNode schema = OBJECT_MAPPER.createObjectNode();
        schema.put("type", "integer");
        schema.put("minimum", min);
        schema.put("maximum", max);
        schema.put("default", defaultVal);
        return schema;
    }
    
    /**
     * Create an enum schema for string values.
     * @param values allowed string values
     * @param defaultVal default value
     * @return JSON schema object node
     */
    public static ObjectNode stringEnum(List<String> values, String defaultVal) {
        ObjectNode schema = OBJECT_MAPPER.createObjectNode();
        schema.put("type", "string");
        
        ArrayNode enumNode = OBJECT_MAPPER.createArrayNode();
        for (String value : values) {
            enumNode.add(value);
        }
        schema.set("enum", enumNode);
        
        if (defaultVal != null) {
            schema.put("default", defaultVal);
        }
        
        return schema;
    }
}
