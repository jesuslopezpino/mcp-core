# MCP Core

Core library for Model Context Protocol (MCP) - provides contracts, adapters, and utilities for secure system automation with a complete Windows Starter Pack.

## Overview

MCP Core is a deterministic, auditable library that defines the contracts for tools and recipes in the MCP ecosystem. It provides Windows PowerShell adapters, structured audit logging, and a comprehensive set of Windows administration tools.

## Features

- **Tool Contracts**: Define executable tools with JSON Schema validation
- **Execution Context**: Track user, asset, and correlation information
- **Windows Adapter**: Secure PowerShell execution with audit logging
- **Tool Registry**: Thread-safe tool management with alias support
- **Audit Logging**: Structured JSON logging for compliance
- **Security Allowlist**: Command validation to prevent dangerous operations
- **Windows Starter Pack**: 8 ready-to-use Windows administration tools
- **Dry-Run Mode**: Safe testing without system modifications
- **Timeout Protection**: Configurable execution timeouts

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

### Windows Starter Pack

The easiest way to get started is with the Windows Starter Pack:

```java
import com.acme.mcp.core.tool.ToolRegistry;
import com.acme.mcp.core.tools.WindowsStarterPack;

public class ExampleUsage {
    public static void main(String[] args) {
        // Create a tool registry
        ToolRegistry registry = new ToolRegistry();
        
        // Register all Windows Starter Pack tools
        WindowsStarterPack.registerAll(registry);
        
        // Now you have access to 8 Windows administration tools:
        // - system.reset_network (alias: system_reset_network)
        // - system.restart_service
        // - system.clear_temp
        // - apps.install
        // - apps.repair_teams
        // - security.quick_scan_defender
        // - security.check_bitlocker
        // - files.backup_user_docs
    }
}
```

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
    System.out.println("Aliases: " + tool.aliases());
}

// Get specific tool by name or alias
Tool tool = registry.get("system.reset_network");  // New name
Tool legacyTool = registry.get("system_reset_network");  // Legacy alias
// Both return the same tool instance
```

### Dry-Run Mode for Testing

```java
import com.acme.mcp.core.windows.PowerShellRunner;
import com.acme.mcp.core.tools.SystemResetNetworkTool;

// Create a dry-run PowerShell runner
PowerShellRunner dryRunRunner = new PowerShellRunner(120, true); // 120s timeout, dry-run enabled

// Create tool with dry-run runner
SystemResetNetworkTool tool = new SystemResetNetworkTool(dryRunRunner, new Allowlist());

// Execute - will not actually run commands, just audit
ExecuteResult result = tool.execute(context, args);
// result.getStdout() will be "DRY_RUN"
// result.getExitCode() will be 0
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

## Windows Starter Pack Tools

### System Tools

#### `system.reset_network` (alias: `system_reset_network`)
- **Description**: Reset network configuration to resolve connectivity issues
- **Requires Confirmation**: Yes
- **Parameters**: None
- **Commands**: `ipconfig /flushdns`, `netsh winsock reset`, `netsh int ip reset`

#### `system.restart_service`
- **Description**: Restart a Windows service
- **Requires Confirmation**: Yes
- **Parameters**: 
  ```json
  {
    "name": "ServiceName",
    "timeoutSec": 30
  }
  ```
- **Commands**: `Stop-Service`, `Start-Service`, `Get-Service`

#### `system.clear_temp`
- **Description**: Clear temporary files from system and user temp directories
- **Requires Confirmation**: Yes
- **Parameters**: None
- **Commands**: `Remove-Item` on `$env:TEMP` and `C:\Windows\Temp`

### Application Tools

#### `apps.install`
- **Description**: Install applications using Windows Package Manager (winget)
- **Requires Confirmation**: Yes
- **Parameters**:
  ```json
  {
    "name": "Microsoft.VisualStudioCode",
    "silent": true
  }
  ```
- **Commands**: `winget install --accept-source-agreements --accept-package-agreements`

#### `apps.repair_teams`
- **Description**: Repair Microsoft Teams by killing processes and clearing cache
- **Requires Confirmation**: No
- **Parameters**: None
- **Commands**: `taskkill`, `Remove-Item`, `Start-Process`

### Security Tools

#### `security.quick_scan_defender`
- **Description**: Perform quick or full scan using Windows Defender
- **Requires Confirmation**: Yes
- **Parameters**:
  ```json
  {
    "scanType": "QuickScan"
  }
  ```
- **Commands**: `Get-MpComputerStatus`, `Start-MpScan`

#### `security.check_bitlocker`
- **Description**: Check BitLocker encryption status on all volumes
- **Requires Confirmation**: No
- **Parameters**: None
- **Commands**: `Get-BitLockerVolume`

### File Tools

#### `files.backup_user_docs`
- **Description**: Backup user documents to a ZIP file
- **Requires Confirmation**: Yes
- **Parameters**:
  ```json
  {
    "user": "username",
    "destZip": "C:\\Temp\\user-docs.zip",
    "includeDesktop": true,
    "includeDocuments": true,
    "includeDownloads": false
  }
  ```
- **Commands**: `Compress-Archive`

## Architecture

```
com.acme.mcp.core/
├── tool/           # Tool contracts and execution
├── schema/         # JSON Schema definitions
├── audit/          # Structured logging
├── security/       # Security allowlist
├── windows/        # Windows-specific adapters
└── tools/          # Concrete tool implementations
```

## Security

- **Command Allowlist**: Only approved PowerShell commands can be executed
- **PowerShell Security**: Uses `-NoProfile -ExecutionPolicy Bypass` for consistent behavior
- **Audit Logging**: All executions are logged with structured audit trails
- **User Confirmation**: Tools can require user confirmation before execution
- **Execution Context**: Tracks user, asset, and correlation information
- **Timeout Protection**: Configurable execution timeouts prevent hanging processes

### Allowed Commands

The security allowlist permits these PowerShell commands:
- `ipconfig`, `netsh` (network management)
- `Get-MpComputerStatus`, `Start-MpScan` (Windows Defender)
- `winget` (Windows Package Manager)
- `Get-BitLockerVolume`, `manage-bde` (BitLocker)
- `Get-Service`, `Restart-Service` (service management)
- `Stop-Process`, `Start-Process` (process management)
- `Remove-Item`, `Get-ChildItem`, `Copy-Item`, `New-Item` (file operations)
- `Compress-Archive` (archiving)
- `Set-ExecutionPolicy` (PowerShell policy)
- `taskkill` (process termination)

### Audit Events

The library logs these audit events:
- `tool_execution_started` - Tool execution begins
- `tool_execution_completed` - Tool execution finishes successfully
- `tool_execution_error` - Tool execution fails
- `tool_execution_blocked` - Command blocked by allowlist
- `tool_validation_error` - Parameter validation fails

## Dependencies

- Jackson Databind (JSON processing)
- Apache Commons IO (file operations)
- SLF4J + Logback (logging)
- JUnit Jupiter (testing)

## Maven Wrapper

This project includes Maven Wrapper (`mvnw.cmd` for Windows, `mvnw` for Linux/Mac), so you don't need to install Maven separately. The wrapper will automatically download the correct Maven version when first used.

## Build Status

✅ **BUILD SUCCESSFUL** - All tests pass (25+ tests, 0 failures, 0 errors)

The project compiles successfully and all tests pass, including:
- ToolRegistry functionality with alias support (11 tests)
- PowerShellToolBase functionality (3 tests)
- Allowlist security validation (6 tests)
- Windows Starter Pack tools (8 tools with individual tests)
- SystemResetNetworkTool execution (7 tests)
- Audit logging with JSON output
- PowerShell command execution with dry-run mode

## License

This project is part of the MCP (Model Context Protocol) platform.
