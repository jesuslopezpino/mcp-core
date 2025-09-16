package com.acme.mcp.core.tool;

import com.acme.mcp.core.tools.SystemResetNetworkTool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ToolRegistryTest {
    
    private ToolRegistry registry;
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        registry = new ToolRegistry();
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void testRegisterAndGet() {
        // Create a test tool
        Tool testTool = createTestTool("test_tool", "Test tool description");
        
        // Register the tool
        registry.register(testTool);
        
        // Verify it can be retrieved
        Tool retrieved = registry.get("test_tool");
        assertNotNull(retrieved);
        assertEquals("test_tool", retrieved.getName());
        assertEquals("Test tool description", retrieved.getDescription());
    }
    
    @Test
    void testRegisterWithNullName() {
        Tool invalidTool = createTestTool(null, "Invalid tool");
        
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register(invalidTool);
        });
    }
    
    @Test
    void testRegisterWithEmptyName() {
        Tool invalidTool = createTestTool("", "Invalid tool");
        
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register(invalidTool);
        });
    }
    
    @Test
    void testGetNonExistentTool() {
        Tool retrieved = registry.get("non_existent");
        assertNull(retrieved);
    }
    
    @Test
    void testList() {
        // Register multiple tools
        Tool tool1 = createTestTool("tool1", "Tool 1");
        Tool tool2 = createTestTool("tool2", "Tool 2");
        
        registry.register(tool1);
        registry.register(tool2);
        
        // Get all tools
        Collection<Tool> tools = registry.list();
        assertEquals(2, tools.size());
        assertTrue(tools.contains(tool1));
        assertTrue(tools.contains(tool2));
    }
    
    @Test
    void testListNames() {
        // Register multiple tools
        registry.register(createTestTool("tool1", "Tool 1"));
        registry.register(createTestTool("tool2", "Tool 2"));
        
        // Get all tool names
        List<String> names = registry.listNames();
        assertEquals(2, names.size());
        assertTrue(names.contains("tool1"));
        assertTrue(names.contains("tool2"));
    }
    
    @Test
    void testContains() {
        registry.register(createTestTool("test_tool", "Test tool"));
        
        assertTrue(registry.contains("test_tool"));
        assertFalse(registry.contains("non_existent"));
    }
    
    @Test
    void testUnregister() {
        Tool testTool = createTestTool("test_tool", "Test tool");
        registry.register(testTool);
        
        assertTrue(registry.contains("test_tool"));
        
        Tool removed = registry.unregister("test_tool");
        assertEquals(testTool, removed);
        assertFalse(registry.contains("test_tool"));
    }
    
    @Test
    void testSize() {
        assertEquals(0, registry.size());
        
        registry.register(createTestTool("tool1", "Tool 1"));
        assertEquals(1, registry.size());
        
        registry.register(createTestTool("tool2", "Tool 2"));
        assertEquals(2, registry.size());
    }
    
    @Test
    void testClear() {
        registry.register(createTestTool("tool1", "Tool 1"));
        registry.register(createTestTool("tool2", "Tool 2"));
        
        assertEquals(2, registry.size());
        
        registry.clear();
        assertEquals(0, registry.size());
        assertTrue(registry.list().isEmpty());
    }
    
    @Test
    void testRegisterRealTool() {
        // Test with actual SystemResetNetworkTool
        SystemResetNetworkTool networkTool = new SystemResetNetworkTool();
        registry.register(networkTool);
        
        Tool retrieved = registry.get("system_reset_network");
        assertNotNull(retrieved);
        assertEquals(networkTool, retrieved);
        assertTrue(retrieved.requiresConfirmation());
        assertTrue(retrieved.getOsSupport().contains("windows"));
    }
    
    private Tool createTestTool(String name, String description) {
        return new Tool() {
            @Override
            public String getName() {
                return name;
            }
            
            @Override
            public String getDescription() {
                return description;
            }
            
            @Override
            public JsonNode getJsonSchema() {
                return objectMapper.createObjectNode();
            }
            
            @Override
            public boolean requiresConfirmation() {
                return false;
            }
            
            @Override
            public List<String> getOsSupport() {
                return List.of("windows");
            }
            
            @Override
            public ExecuteResult execute(ExecutionContext context, JsonNode args) {
                return new ExecuteResult("test-execution", 0, "success", "", ExecuteResult.Status.SUCCESS);
            }
        };
    }
}
