package com.acme.mcp.core.tools;

import com.acme.mcp.core.security.Allowlist;
import com.acme.mcp.core.tool.ExecutionContext;
import com.acme.mcp.core.tool.ExecuteResult;
import com.acme.mcp.core.windows.PowerShellRunner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AppsInstallTool.
 */
class AppsInstallToolTest {
    
    private AppsInstallTool tool;
    private PowerShellRunner dryRunRunner;
    private Allowlist allowlist;
    private ExecutionContext context;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        dryRunRunner = new PowerShellRunner(120, true); // Dry run mode
        allowlist = new Allowlist();
        tool = new AppsInstallTool(dryRunRunner, allowlist);
        context = new ExecutionContext("testuser", "testasset", "testcorrelation");
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void testToolProperties() {
        assertEquals("apps.install", tool.getName());
        assertEquals("Install applications using Windows Package Manager (winget)", tool.getDescription());
        assertTrue(tool.requiresConfirmation());
        assertEquals(List.of("windows"), tool.getOsSupport());
        assertNotNull(tool.getJsonSchema());
    }
    
    @Test
    void testJsonSchema() {
        JsonNode schema = tool.getJsonSchema();
        assertNotNull(schema);
        assertTrue(schema.isObject());
        assertEquals("object", schema.get("type").asText());
        
        // Check required fields
        assertTrue(schema.has("required"));
        JsonNode required = schema.get("required");
        assertTrue(required.isArray());
        assertEquals(1, required.size());
        assertEquals("name", required.get(0).asText());
        
        // Check properties
        assertTrue(schema.has("properties"));
        JsonNode properties = schema.get("properties");
        assertTrue(properties.has("name"));
        assertTrue(properties.has("silent"));
    }
    
    @Test
    void testExecuteWithRequiredFields() {
        ObjectNode args = objectMapper.createObjectNode();
        args.put("name", "Microsoft.VisualStudioCode");
        
        ExecuteResult result = tool.execute(context, args);
        
        assertEquals(0, result.getExitCode());
        assertEquals("DRY_RUN", result.getStdout());
        assertEquals(ExecuteResult.Status.SUCCESS, result.getStatus());
    }
    
    @Test
    void testExecuteWithAllFields() {
        ObjectNode args = objectMapper.createObjectNode();
        args.put("name", "Google.Chrome");
        args.put("silent", false);
        
        ExecuteResult result = tool.execute(context, args);
        
        assertEquals(0, result.getExitCode());
        assertEquals("DRY_RUN", result.getStdout());
        assertEquals(ExecuteResult.Status.SUCCESS, result.getStatus());
    }
    
    @Test
    void testValidationErrorMissingName() {
        ObjectNode args = objectMapper.createObjectNode();
        // Missing required "name" field
        
        assertThrows(IllegalArgumentException.class, () -> {
            tool.execute(context, args);
        });
    }
}
