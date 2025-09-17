package com.acme.mcp.core.tool;

import com.acme.mcp.core.tools.SystemResetNetworkTool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ToolRegistry functionality including alias support.
 */
class ToolRegistryTest {
    
    private ToolRegistry registry;
    
    @BeforeEach
    void setUp() {
        registry = new ToolRegistry();
    }
    
    @Test
    void testRegisterTool() {
        SystemResetNetworkTool tool = new SystemResetNetworkTool();
        registry.register(tool);
        
        assertEquals(2, registry.size()); // Main name + 1 alias
        assertTrue(registry.contains("system.reset_network"));
        assertTrue(registry.contains("system_reset_network"));
        assertSame(tool, registry.get("system.reset_network"));
        assertSame(tool, registry.get("system_reset_network"));
    }
    
    @Test
    void testRegisterToolWithNullName() {
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register(new Tool() {
                @Override
                public String getName() { return null; }
                @Override
                public String getDescription() { return "Test"; }
                @Override
                public com.fasterxml.jackson.databind.JsonNode getJsonSchema() { return null; }
                @Override
                public boolean requiresConfirmation() { return false; }
                @Override
                public java.util.List<String> getOsSupport() { return java.util.List.of(); }
                @Override
                public ExecuteResult execute(ExecutionContext context, com.fasterxml.jackson.databind.JsonNode args) { return null; }
            });
        });
    }
    
    @Test
    void testRegisterToolWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register(new Tool() {
                @Override
                public String getName() { return ""; }
                @Override
                public String getDescription() { return "Test"; }
                @Override
                public com.fasterxml.jackson.databind.JsonNode getJsonSchema() { return null; }
                @Override
                public boolean requiresConfirmation() { return false; }
                @Override
                public java.util.List<String> getOsSupport() { return java.util.List.of(); }
                @Override
                public ExecuteResult execute(ExecutionContext context, com.fasterxml.jackson.databind.JsonNode args) { return null; }
            });
        });
    }
    
    @Test
    void testRegisterToolWithDuplicateName() {
        SystemResetNetworkTool tool1 = new SystemResetNetworkTool();
        SystemResetNetworkTool tool2 = new SystemResetNetworkTool();
        
        registry.register(tool1);
        
        assertThrows(IllegalStateException.class, () -> {
            registry.register(tool2);
        });
    }
    
    @Test
    void testRegisterToolWithDuplicateAlias() {
        SystemResetNetworkTool tool1 = new SystemResetNetworkTool();
        
        // Create a tool with conflicting alias
        Tool conflictingTool = new Tool() {
            @Override
            public String getName() { return "other.tool"; }
            @Override
            public String getDescription() { return "Other tool"; }
            @Override
            public com.fasterxml.jackson.databind.JsonNode getJsonSchema() { return null; }
            @Override
            public boolean requiresConfirmation() { return false; }
            @Override
            public java.util.List<String> getOsSupport() { return java.util.List.of(); }
            @Override
            public ExecuteResult execute(ExecutionContext context, com.fasterxml.jackson.databind.JsonNode args) { return null; }
            @Override
            public java.util.List<String> aliases() { return java.util.List.of("system_reset_network"); }
        };
        
        registry.register(tool1);
        
        assertThrows(IllegalStateException.class, () -> {
            registry.register(conflictingTool);
        });
    }
    
    @Test
    void testGetToolByName() {
        SystemResetNetworkTool tool = new SystemResetNetworkTool();
        registry.register(tool);
        
        assertSame(tool, registry.get("system.reset_network"));
        assertSame(tool, registry.get("system_reset_network"));
        assertNull(registry.get("nonexistent"));
    }
    
    @Test
    void testListTools() {
        SystemResetNetworkTool tool = new SystemResetNetworkTool();
        registry.register(tool);
        
        // With aliases, we get 2 registrations but only 1 unique tool
        assertEquals(2, registry.size()); // Main name + 1 alias
        assertEquals(1, registry.list().size()); // Only 1 unique tool
        assertTrue(registry.list().contains(tool));
    }
    
    @Test
    void testListNames() {
        SystemResetNetworkTool tool = new SystemResetNetworkTool();
        registry.register(tool);
        
        assertEquals(2, registry.listNames().size());
        assertTrue(registry.listNames().contains("system.reset_network"));
        assertTrue(registry.listNames().contains("system_reset_network"));
    }
    
    @Test
    void testContains() {
        SystemResetNetworkTool tool = new SystemResetNetworkTool();
        registry.register(tool);
        
        assertTrue(registry.contains("system.reset_network"));
        assertTrue(registry.contains("system_reset_network"));
        assertFalse(registry.contains("nonexistent"));
    }
    
    @Test
    void testUnregister() {
        SystemResetNetworkTool tool = new SystemResetNetworkTool();
        registry.register(tool);
        
        assertEquals(2, registry.size());
        
        Tool removed = registry.unregister("system.reset_network");
        assertSame(tool, removed);
        assertEquals(1, registry.size()); // Only alias remains
        assertTrue(registry.contains("system_reset_network"));
        assertFalse(registry.contains("system.reset_network"));
    }
    
    @Test
    void testClear() {
        SystemResetNetworkTool tool = new SystemResetNetworkTool();
        registry.register(tool);
        
        assertEquals(2, registry.size());
        
        registry.clear();
        assertEquals(0, registry.size());
    }
}