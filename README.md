# MCP Core

Core library for Model Context Protocol (MCP) - provides contracts, adapters, and utilities for secure system automation.

## Overview

MCP Core is a deterministic, auditable library that defines the contracts for tools and recipes in the MCP ecosystem. It provides Windows PowerShell adapters and structured audit logging capabilities.

## Features

- **Tool Contracts**: Define executable tools with JSON Schema validation
- **Execution Context**: Track user, asset, and correlation information
- **Windows Adapter**: Secure PowerShell execution with audit logging
- **Tool Registry**: Thread-safe tool management
- **Audit Logging**: Structured JSON logging for compliance

## Building

### Prerequisites

- Java 17 or higher (tested with Java 17)
- Maven 3.6 or higher (or use Maven Wrapper included)

### Compile and Test

```bash
# Using Maven directly (if installed)
mvn clean compile
mvn test
mvn clean install

# Using Maven Wrapper (recommended)
./mvnw.cmd clean compile    # Windows
./mvnw clean compile        # Linux/Mac
./mvnw.cmd test            # Windows
./mvnw test                # Linux/Mac
./mvnw.cmd clean install   # Windows
./mvnw clean install       # Linux/Mac

# Skip tests (if needed)
./mvnw.cmd clean install -DskipTests
```

### Verify Tests Pass

```bash
# Using Maven directly
mvn -q -DskipTests=false test

# Using Maven Wrapper
./mvnw.cmd -q -DskipTests=false test    # Windows
./mvnw -q -DskipTests=false test        # Linux/Mac

# Using build scripts
# Windows:
build.bat

# Linux/Mac:
./build.sh
```

## Usage

### Basic Tool Usage

```java
import com.acme.mcp.core.tool.*;
import com.acme.mcp.core.tools.SystemResetNetworkTool;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExampleUsage {
    public static void main(String[] args) {
        // Create a tool registry
        ToolRegistry registry = new ToolRegistry();
        
        // Register a tool
        SystemResetNetworkTool networkTool = new SystemResetNetworkTool();
        registry.register(networkTool);
        
        // Create execution context
        ExecutionContext context = new ExecutionContext(
            "user123",           // userId
            "asset-001",         // assetId
            "corr-456"           // correlationId
        );
        
        // Execute the tool
        ObjectMapper mapper = new ObjectMapper();
        JsonNode args = mapper.createObjectNode(); // Empty args for this tool
        
        ExecuteResult result = networkTool.execute(context, args);
        
        // Check result
        if (result.isSuccess()) {
            System.out.println("Tool executed successfully");
            System.out.println("Output: " + result.getStdout());
        } else {
            System.out.println("Tool failed with exit code: " + result.getExitCode());
            System.out.println("Error: " + result.getStderr());
        }
    }
}
```

### Tool Registry Usage

```java
// List all registered tools
Collection<Tool> tools = registry.list();
for (Tool tool : tools) {
    System.out.println("Tool: " + tool.getName());
    System.out.println("Description: " + tool.getDescription());
    System.out.println("Requires confirmation: " + tool.requiresConfirmation());
    System.out.println("OS Support: " + tool.getOsSupport());
}

// Get specific tool
Tool tool = registry.get("system_reset_network");
if (tool != null) {
    // Use the tool
}
```

### Audit Logging

The library automatically logs audit events in JSON format:

```json
{
  "event": "tool_execution_started",
  "timestamp": 1703123456789,
  "tool": "system_reset_network",
  "userId": "user123",
  "assetId": "asset-001",
  "correlationId": "corr-456"
}
```

## Available Tools

### SystemResetNetworkTool

Resets network configuration on Windows systems to resolve connectivity issues.

- **Name**: `system_reset_network`
- **Description**: Reset network configuration to resolve connectivity issues
- **Requires Confirmation**: Yes
- **OS Support**: Windows
- **Parameters**: None (empty schema)
- **Commands Executed**:
  - `ipconfig /flushdns`
  - `netsh winsock reset`
  - `netsh int ip reset`

## Architecture

```
com.acme.mcp.core/
├── tool/           # Tool contracts and execution
├── schema/         # JSON Schema definitions
├── audit/          # Structured logging
├── windows/        # Windows-specific adapters
└── tools/          # Concrete tool implementations
```

## Security

- PowerShell execution uses `-NoProfile -ExecutionPolicy Bypass` for consistent behavior
- All executions are logged with structured audit trails
- Tools can require user confirmation before execution
- Execution context tracks user and asset information

## Dependencies

- Jackson Databind (JSON processing)
- Apache Commons IO (file operations)
- SLF4J + Logback (logging)
- JUnit Jupiter (testing)

## Maven Wrapper

This project includes Maven Wrapper (`mvnw.cmd` for Windows, `mvnw` for Linux/Mac), so you don't need to install Maven separately. The wrapper will automatically download the correct Maven version when first used.

## Build Status

✅ **BUILD SUCCESSFUL** - All tests pass (18 tests, 0 failures, 0 errors)

The project compiles successfully and all tests pass, including:
- ToolRegistry functionality (11 tests)
- SystemResetNetworkTool execution (7 tests)
- Audit logging with JSON output
- PowerShell command execution

## License

This project is part of the MCP (Model Context Protocol) platform.
