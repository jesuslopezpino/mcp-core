package com.acme.mcp.core.tools;

import com.acme.mcp.core.tool.*;
import com.acme.mcp.core.schema.JsonSchemas;
import com.acme.mcp.core.security.Allowlist;
import com.acme.mcp.core.windows.PowerShellRunner;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Tool to restart a Windows service.
 */
public class SystemRestartServiceTool extends PowerShellToolBase {
    
    private static final String TOOL_NAME = "system.restart_service";
    private static final String TOOL_DESCRIPTION = "Restart a Windows service";
    private static final List<String> OS_SUPPORT = List.of("windows");
    
    public SystemRestartServiceTool() {
        super(TOOL_NAME, TOOL_DESCRIPTION, true, OS_SUPPORT, createJsonSchema());
    }
    
    /**
     * Constructor for testing with custom PowerShellRunner.
     */
    public SystemRestartServiceTool(PowerShellRunner powerShellRunner, Allowlist allowlist) {
        super(TOOL_NAME, TOOL_DESCRIPTION, true, OS_SUPPORT, createJsonSchema(), powerShellRunner, allowlist);
    }
    
    @Override
    public ExecuteResult execute(ExecutionContext context, JsonNode args) {
        // Validate required fields
        validateRequiredFields(args, List.of("name"));
        
        String serviceName = args.get("name").asText();
        int timeoutSec = args.has("timeoutSec") ? args.get("timeoutSec").asInt() : 30;
        
        // Enhanced restart with detailed monitoring and information gathering
        List<String> commands = List.of(
            // Phase 1: Pre-restart information gathering
            "Write-Host '=== PHASE 1: PRE-RESTART SERVICE ANALYSIS ===' -ForegroundColor Yellow",
            "Write-Host 'Timestamp: ' + (Get-Date -Format 'yyyy-MM-dd HH:mm:ss') -ForegroundColor Cyan",
            "Write-Host 'Service Name: " + serviceName + "' -ForegroundColor Green",
            "",
            "Write-Host '--- Current Service Status ---' -ForegroundColor Magenta",
            "Get-Service -Name \"" + serviceName + "\" | Select-Object Name, Status, StartType, DisplayName | Format-Table -AutoSize",
            "",
            "Write-Host '--- Service Process Information ---' -ForegroundColor Magenta",
            "$service = Get-Service -Name \"" + serviceName + "\"",
            "if ($service.Status -eq 'Running') {",
            "    $processes = Get-WmiObject -Class Win32_Service | Where-Object { $_.Name -eq \"" + serviceName + "\" }",
            "    if ($processes) {",
            "        $processes | ForEach-Object {",
            "            Write-Host 'Process ID: ' $_.ProcessId -ForegroundColor White",
            "            Write-Host 'Service Account: ' $_.StartName -ForegroundColor White",
            "            Write-Host 'Service Path: ' $_.PathName -ForegroundColor White",
            "            Write-Host 'Service Description: ' $_.Description -ForegroundColor White",
            "            Write-Host 'Service State: ' $_.State -ForegroundColor White",
            "            Write-Host 'Service Start Mode: ' $_.StartMode -ForegroundColor White",
            "            if ($_.ProcessId -ne 0) {",
            "                $proc = Get-Process -Id $_.ProcessId -ErrorAction SilentlyContinue",
            "                if ($proc) {",
            "                    Write-Host 'Process Name: ' $proc.ProcessName -ForegroundColor White",
            "                    Write-Host 'Process Start Time: ' $proc.StartTime -ForegroundColor White",
            "                    Write-Host 'Process CPU Time: ' $proc.TotalProcessorTime -ForegroundColor White",
            "                    Write-Host 'Process Memory: ' ([math]::Round($proc.WorkingSet64/1MB, 2)) 'MB' -ForegroundColor White",
            "                }",
            "            }",
            "        }",
            "    }",
            "} else {",
            "    Write-Host 'Service is not running' -ForegroundColor Red",
            "}",
            "",
            "Write-Host '--- Service Dependencies ---' -ForegroundColor Magenta",
            "Get-Service -Name \"" + serviceName + "\" -DependentServices | Select-Object Name, Status | Format-Table -AutoSize",
            "",
            "Write-Host '--- Service Dependents ---' -ForegroundColor Magenta",
            "Get-Service -Name \"" + serviceName + "\" -RequiredServices | Select-Object Name, Status | Format-Table -AutoSize",
            "",
            "Write-Host '=== PHASE 2: STOPPING SERVICE ===' -ForegroundColor Yellow",
            "Write-Host 'Stopping service: " + serviceName + "' -ForegroundColor Red",
            "Write-Host 'Stop Time: ' + (Get-Date -Format 'yyyy-MM-dd HH:mm:ss') -ForegroundColor Cyan",
            "Stop-Service -Name \"" + serviceName + "\" -Force -ErrorAction SilentlyContinue",
            "Start-Sleep -Seconds 2",
            "",
            "Write-Host '--- Post-Stop Status ---' -ForegroundColor Magenta",
            "Get-Service -Name \"" + serviceName + "\" | Select-Object Name, Status | Format-Table -AutoSize",
            "",
            "Write-Host '=== PHASE 3: STARTING SERVICE ===' -ForegroundColor Yellow",
            "Write-Host 'Starting service: " + serviceName + "' -ForegroundColor Green",
            "Write-Host 'Start Time: ' + (Get-Date -Format 'yyyy-MM-dd HH:mm:ss') -ForegroundColor Cyan",
            "Start-Service -Name \"" + serviceName + "\" -ErrorAction SilentlyContinue",
            "Start-Sleep -Seconds 3",
            "",
            "Write-Host '=== PHASE 4: POST-RESTART MONITORING ===' -ForegroundColor Yellow",
            "Write-Host '--- Final Service Status ---' -ForegroundColor Magenta",
            "Get-Service -Name \"" + serviceName + "\" | Select-Object Name, Status, StartType, DisplayName | Format-Table -AutoSize",
            "",
            "Write-Host '--- New Process Information ---' -ForegroundColor Magenta",
            "$newService = Get-Service -Name \"" + serviceName + "\"",
            "if ($newService.Status -eq 'Running') {",
            "    $newProcesses = Get-WmiObject -Class Win32_Service | Where-Object { $_.Name -eq \"" + serviceName + "\" }",
            "    if ($newProcesses) {",
            "        $newProcesses | ForEach-Object {",
            "            Write-Host 'NEW Process ID: ' $_.ProcessId -ForegroundColor Green",
            "            Write-Host 'NEW Service Account: ' $_.StartName -ForegroundColor Green",
            "            Write-Host 'NEW Service Path: ' $_.PathName -ForegroundColor Green",
            "            Write-Host 'NEW Service State: ' $_.State -ForegroundColor Green",
            "            if ($_.ProcessId -ne 0) {",
            "                $newProc = Get-Process -Id $_.ProcessId -ErrorAction SilentlyContinue",
            "                if ($newProc) {",
            "                    Write-Host 'NEW Process Name: ' $newProc.ProcessName -ForegroundColor Green",
            "                    Write-Host 'NEW Process Start Time: ' $newProc.StartTime -ForegroundColor Green",
            "                    Write-Host 'NEW Process CPU Time: ' $newProc.TotalProcessorTime -ForegroundColor Green",
            "                    Write-Host 'NEW Process Memory: ' ([math]::Round($newProc.WorkingSet64/1MB, 2)) 'MB' -ForegroundColor Green",
            "                }",
            "            }",
            "        }",
            "    }",
            "} else {",
            "    Write-Host 'WARNING: Service failed to start!' -ForegroundColor Red",
            "}",
            "",
            "Write-Host '--- Service Event Log (Last 5 entries) ---' -ForegroundColor Magenta",
            "Get-WinEvent -FilterHashtable @{LogName='System'; ID=7034,7035,7036} -MaxEvents 5 | Where-Object { $_.Message -like '*" + serviceName + "*' } | Select-Object TimeCreated, Id, LevelDisplayName, Message | Format-Table -AutoSize",
            "",
            "Write-Host '=== RESTART COMPLETED ===' -ForegroundColor Yellow",
            "Write-Host 'Completion Time: ' + (Get-Date -Format 'yyyy-MM-dd HH:mm:ss') -ForegroundColor Cyan",
            "Write-Host 'Service Status: ' + (Get-Service -Name \"" + serviceName + "\").Status -ForegroundColor $(if ((Get-Service -Name \"" + serviceName + "\").Status -eq 'Running') { 'Green' } else { 'Red' })"
        );
        
        return runPs(commands, context, Map.of(
            "serviceName", serviceName,
            "timeoutSec", timeoutSec
        ));
    }
    
    private static JsonNode createJsonSchema() {
        return JsonSchemas.object(
            Map.of(
                "name", JsonSchemas.string("Service name"),
                "timeoutSec", JsonSchemas.integer(0, 120, 30)
            ),
            List.of("name")
        );
    }
}
