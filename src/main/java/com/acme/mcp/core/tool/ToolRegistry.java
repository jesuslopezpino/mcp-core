package com.acme.mcp.core.tool;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Registry for managing available tools.
 * Thread-safe registry using ConcurrentHashMap for tool storage and retrieval.
 */
public class ToolRegistry {
    
    private final ConcurrentMap<String, Tool> tools = new ConcurrentHashMap<>();
    
    /**
     * Register a tool in the registry.
     * @param tool the tool to register
     * @throws IllegalArgumentException if tool name is null or empty
     */
    public void register(Tool tool) {
        if (tool == null || tool.getName() == null || tool.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tool name cannot be null or empty");
        }
        tools.put(tool.getName(), tool);
    }
    
    /**
     * Get a tool by name.
     * @param name tool name
     * @return the tool or null if not found
     */
    public Tool get(String name) {
        return tools.get(name);
    }
    
    /**
     * Get all registered tools.
     * @return unmodifiable collection of all tools
     */
    public Collection<Tool> list() {
        return Collections.unmodifiableCollection(tools.values());
    }
    
    /**
     * Get all tool names.
     * @return unmodifiable list of tool names
     */
    public List<String> listNames() {
        return List.copyOf(tools.keySet());
    }
    
    /**
     * Check if a tool is registered.
     * @param name tool name
     * @return true if tool is registered
     */
    public boolean contains(String name) {
        return tools.containsKey(name);
    }
    
    /**
     * Remove a tool from the registry.
     * @param name tool name
     * @return the removed tool or null if not found
     */
    public Tool unregister(String name) {
        return tools.remove(name);
    }
    
    /**
     * Get the number of registered tools.
     * @return tool count
     */
    public int size() {
        return tools.size();
    }
    
    /**
     * Clear all tools from the registry.
     */
    public void clear() {
        tools.clear();
    }
}
