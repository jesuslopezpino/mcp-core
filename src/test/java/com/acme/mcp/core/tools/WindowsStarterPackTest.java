package com.acme.mcp.core.tools;

import com.acme.mcp.core.tool.ToolRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for WindowsStarterPack registration.
 */
class WindowsStarterPackTest {
    
    private ToolRegistry registry;
    
    @BeforeEach
    void setUp() {
        registry = new ToolRegistry();
    }
    
    @Test
    void testRegisterAll() {
        WindowsStarterPack.registerAll(registry);
        
        // Check that all tools are registered
        String[] expectedTools = WindowsStarterPack.getToolNames();
        for (String toolName : expectedTools) {
            assertNotNull(registry.get(toolName), "Tool should be registered: " + toolName);
        }
        
        // Check that legacy aliases are registered
        String[] expectedAliases = WindowsStarterPack.getLegacyAliases();
        for (String alias : expectedAliases) {
            assertNotNull(registry.get(alias), "Alias should be registered: " + alias);
        }
        
        // Verify total count (9 tools + 1 alias = 10 registrations)
        assertEquals(10, registry.size());
    }
    
    @Test
    void testGetToolNames() {
        String[] toolNames = WindowsStarterPack.getToolNames();
        
        assertNotNull(toolNames);
        assertEquals(9, toolNames.length);
        
        // Verify specific tools are included
        assertTrue(java.util.Arrays.asList(toolNames).contains("system.reset_network"));
        assertTrue(java.util.Arrays.asList(toolNames).contains("system.list_services"));
        assertTrue(java.util.Arrays.asList(toolNames).contains("apps.install"));
        assertTrue(java.util.Arrays.asList(toolNames).contains("security.quick_scan_defender"));
        assertTrue(java.util.Arrays.asList(toolNames).contains("files.backup_user_docs"));
    }
    
    @Test
    void testGetLegacyAliases() {
        String[] aliases = WindowsStarterPack.getLegacyAliases();
        
        assertNotNull(aliases);
        assertEquals(1, aliases.length);
        assertEquals("system_reset_network", aliases[0]);
    }
    
    @Test
    void testToolSchemas() {
        WindowsStarterPack.registerAll(registry);
        
        // Test that all tools have valid schemas
        for (String toolName : WindowsStarterPack.getToolNames()) {
            var tool = registry.get(toolName);
            assertNotNull(tool, "Tool should exist: " + toolName);
            assertNotNull(tool.getJsonSchema(), "Tool should have schema: " + toolName);
            assertTrue(tool.getJsonSchema().isObject(), "Schema should be object: " + toolName);
        }
    }
    
    @Test
    void testToolProperties() {
        WindowsStarterPack.registerAll(registry);
        
        // Test specific tool properties
        var networkTool = registry.get("system.reset_network");
        assertNotNull(networkTool);
        assertTrue(networkTool.requiresConfirmation());
        assertEquals("Reset network configuration to resolve connectivity issues", networkTool.getDescription());
        
        var installTool = registry.get("apps.install");
        assertNotNull(installTool);
        assertTrue(installTool.requiresConfirmation());
        assertEquals("Install applications using Windows Package Manager (winget)", installTool.getDescription());
        
        var teamsTool = registry.get("apps.repair_teams");
        assertNotNull(teamsTool);
        assertFalse(teamsTool.requiresConfirmation());
        assertEquals("Repair Microsoft Teams by killing processes and clearing cache", teamsTool.getDescription());
    }
}
