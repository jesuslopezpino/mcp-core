package com.acme.mcp.core.tools;

import com.acme.mcp.core.security.Allowlist;
import com.acme.mcp.core.tool.ExecutionContext;
import com.acme.mcp.core.tool.ExecuteResult;
import com.acme.mcp.core.windows.PowerShellRunner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SystemResetNetworkTool.
 */
class SystemResetNetworkToolTest {
    
    private SystemResetNetworkTool tool;
    private PowerShellRunner dryRunRunner;
    private Allowlist allowlist;
    private ExecutionContext context;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        dryRunRunner = new PowerShellRunner(120, true); // Dry run mode
        allowlist = new Allowlist();
        tool = new SystemResetNetworkTool(dryRunRunner, allowlist);
        context = new ExecutionContext("testuser", "testasset", "testcorrelation");
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void testToolProperties() {
        assertEquals("system.reset_network", tool.getName());
        assertEquals("Reset network configuration to resolve connectivity issues", tool.getDescription());
        assertTrue(tool.requiresConfirmation());
        assertEquals(List.of("windows"), tool.getOsSupport());
        assertNotNull(tool.getJsonSchema());
    }
    
    @Test
    void testAliases() {
        assertEquals(List.of("system_reset_network"), tool.aliases());
    }
    
    @Test
    void testJsonSchema() {
        JsonNode schema = tool.getJsonSchema();
        assertNotNull(schema);
        assertTrue(schema.isObject());
        // Empty schema for this tool
        assertEquals(0, schema.size());
    }
    
    @Test
    void testExecute() {
        JsonNode args = objectMapper.createObjectNode();
        
        ExecuteResult result = tool.execute(context, args);
        
        assertEquals(0, result.getExitCode());
        assertEquals("DRY_RUN", result.getStdout());
        assertEquals("", result.getStderr());
        assertEquals(ExecuteResult.Status.SUCCESS, result.getStatus());
    }
}