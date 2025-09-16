package com.acme.mcp.core.tools;

import com.acme.mcp.core.tool.ExecutionContext;
import com.acme.mcp.core.tool.ExecuteResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SystemResetNetworkToolTest {
    
    private SystemResetNetworkTool tool;
    private ObjectMapper objectMapper;
    private ExecutionContext context;
    
    @BeforeEach
    void setUp() {
        tool = new SystemResetNetworkTool();
        objectMapper = new ObjectMapper();
        context = new ExecutionContext("test-user", "test-asset", "test-correlation");
    }
    
    @Test
    void testToolProperties() {
        assertEquals("system_reset_network", tool.getName());
        assertEquals("Reset network configuration to resolve connectivity issues", tool.getDescription());
        assertTrue(tool.requiresConfirmation());
        
        List<String> osSupport = tool.getOsSupport();
        assertEquals(1, osSupport.size());
        assertTrue(osSupport.contains("windows"));
    }
    
    @Test
    void testJsonSchema() {
        JsonNode schema = tool.getJsonSchema();
        assertNotNull(schema);
        assertTrue(schema.isObject());
        // Empty schema should be an empty object
        assertEquals(0, schema.size());
    }
    
    @Test
    void testExecuteWithEmptyArgs() {
        JsonNode emptyArgs = objectMapper.createObjectNode();
        
        ExecuteResult result = tool.execute(context, emptyArgs);
        
        assertNotNull(result);
        assertNotNull(result.getExecutionId());
        assertNotNull(result.getStatus());
        
        // Note: In a real test environment, we might mock the PowerShellRunner
        // to avoid actual system calls. For now, we verify the structure.
        assertTrue(result.getExecutionId().length() > 0);
    }
    
    @Test
    void testExecuteWithNullArgs() {
        ExecuteResult result = tool.execute(context, null);
        
        assertNotNull(result);
        assertNotNull(result.getExecutionId());
        assertNotNull(result.getStatus());
    }
    
    @Test
    void testExecuteResultStructure() {
        JsonNode args = objectMapper.createObjectNode();
        ExecuteResult result = tool.execute(context, args);
        
        // Verify all required fields are present
        assertNotNull(result.getExecutionId());
        assertNotNull(result.getStdout());
        assertNotNull(result.getStderr());
        assertNotNull(result.getStatus());
        
        // Verify execution ID is a valid UUID format
        assertTrue(result.getExecutionId().matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }
    
    @Test
    void testToolImplementsInterface() {
        // Verify the tool implements the Tool interface correctly
        assertInstanceOf(com.acme.mcp.core.tool.Tool.class, tool);
    }
    
    @Test
    void testContextUsage() {
        // Test that the tool uses the execution context properly
        JsonNode args = objectMapper.createObjectNode();
        
        // This test verifies that the tool can handle the context
        // In a real implementation, we might verify that the context
        // is used for logging or other purposes
        assertDoesNotThrow(() -> {
            ExecuteResult result = tool.execute(context, args);
            assertNotNull(result);
        });
    }
}
