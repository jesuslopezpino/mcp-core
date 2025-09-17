package com.acme.mcp.core.tool;

import com.acme.mcp.core.security.Allowlist;
import com.acme.mcp.core.windows.PowerShellRunner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PowerShellToolBase functionality.
 */
class PowerShellToolBaseTest {
    
    private TestPowerShellTool tool;
    private PowerShellRunner dryRunRunner;
    private Allowlist allowlist;
    private ExecutionContext context;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        dryRunRunner = new PowerShellRunner(120, true); // Dry run mode
        allowlist = new Allowlist();
        tool = new TestPowerShellTool(dryRunRunner, allowlist);
        context = new ExecutionContext("testuser", "testasset", "testcorrelation");
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void testToolProperties() {
        assertEquals("test.tool", tool.getName());
        assertEquals("Test tool for unit testing", tool.getDescription());
        assertTrue(tool.requiresConfirmation());
        assertEquals(List.of("windows"), tool.getOsSupport());
        assertNotNull(tool.getJsonSchema());
    }
    
    @Test
    void testDryRunExecution() {
        JsonNode args = objectMapper.createObjectNode();
        
        ExecuteResult result = tool.execute(context, args);
        
        assertEquals(0, result.getExitCode());
        assertEquals("DRY_RUN", result.getStdout());
        assertEquals("", result.getStderr());
        assertEquals(ExecuteResult.Status.SUCCESS, result.getStatus());
    }
    
    @Test
    void testValidationError() {
        JsonNode args = objectMapper.createObjectNode();
        
        // This should trigger validation error for missing required field
        assertThrows(IllegalArgumentException.class, () -> {
            tool.executeWithValidation(context, args);
        });
    }
    
    @Test
    void testBlockedCommand() {
        JsonNode args = objectMapper.createObjectNode();
        
        // This should trigger security exception for blocked command
        assertThrows(SecurityException.class, () -> {
            tool.executeWithBlockedCommand(context, args);
        });
    }
    
    /**
     * Test implementation of PowerShellToolBase for unit testing.
     */
    private static class TestPowerShellTool extends PowerShellToolBase {
        
        public TestPowerShellTool(PowerShellRunner powerShellRunner, Allowlist allowlist) {
            super("test.tool", "Test tool for unit testing", true, 
                  List.of("windows"), createJsonSchema(), powerShellRunner, allowlist);
        }
        
        @Override
        public ExecuteResult execute(ExecutionContext context, JsonNode args) {
            List<String> commands = List.of("Get-Service");
            return runPs(commands, context, Map.of());
        }
        
        public ExecuteResult executeWithValidation(ExecutionContext context, JsonNode args) {
            validateRequiredFields(args, List.of("requiredField"));
            return execute(context, args);
        }
        
        public ExecuteResult executeWithBlockedCommand(ExecutionContext context, JsonNode args) {
            List<String> commands = List.of("format C:"); // Blocked command
            return runPs(commands, context, Map.of());
        }
        
        private static JsonNode createJsonSchema() {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.createObjectNode();
        }
    }
}
