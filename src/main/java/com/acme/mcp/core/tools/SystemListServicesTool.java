package com.acme.mcp.core.tools;

import com.acme.mcp.core.tool.*;
import com.acme.mcp.core.schema.JsonSchemas;
import com.acme.mcp.core.security.Allowlist;
import com.acme.mcp.core.windows.PowerShellRunner;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Tool to list and search Windows services.
 */
public class SystemListServicesTool extends PowerShellToolBase {
    
    private static final String TOOL_NAME = "system.list_services";
    private static final String TOOL_DESCRIPTION = "List and search Windows services";
    private static final List<String> OS_SUPPORT = List.of("windows");
    
    public SystemListServicesTool() {
        super(TOOL_NAME, TOOL_DESCRIPTION, false, OS_SUPPORT, createJsonSchema());
    }
    
    /**
     * Constructor for testing with custom PowerShellRunner.
     */
    public SystemListServicesTool(PowerShellRunner powerShellRunner, Allowlist allowlist) {
        super(TOOL_NAME, TOOL_DESCRIPTION, false, OS_SUPPORT, createJsonSchema(), powerShellRunner, allowlist);
    }
    
    @Override
    public ExecuteResult execute(ExecutionContext context, JsonNode args) {
        String searchTerm = args.has("search") ? args.get("search").asText() : "";
        String status = args.has("status") ? args.get("status").asText() : "all";
        int limit = args.has("limit") ? args.get("limit").asInt() : 50;
        
        // Enhanced service listing with detailed information
        List<String> commands = List.of(
            "Write-Host '=== WINDOWS SERVICES LIST ===' -ForegroundColor Yellow",
            "Write-Host 'Timestamp: ' + (Get-Date -Format 'yyyy-MM-dd HH:mm:ss') -ForegroundColor Cyan",
            "Write-Host 'Search Term: " + searchTerm + "' -ForegroundColor Green",
            "Write-Host 'Status Filter: " + status + "' -ForegroundColor Green",
            "Write-Host 'Limit: " + limit + "' -ForegroundColor Green",
            "",
            "Write-Host '--- Service Information ---' -ForegroundColor Magenta",
            "$services = Get-Service",
            "if ('" + status + "' -ne 'all') {",
            "    $services = $services | Where-Object { $_.Status -eq '" + status + "' }",
            "}",
            "if ('" + searchTerm + "' -ne '') {",
            "    $services = $services | Where-Object { $_.Name -like '*" + searchTerm + "*' -or $_.DisplayName -like '*" + searchTerm + "*' }",
            "}",
            "$services = $services | Select-Object -First " + limit,
            "",
            "Write-Host 'Total Services Found: ' $services.Count -ForegroundColor Cyan",
            "",
            "Write-Host '--- Detailed Service List ---' -ForegroundColor Magenta",
            "$services | ForEach-Object {",
            "    $service = $_",
            "    Write-Host 'Service Name: ' $service.Name -ForegroundColor White",
            "    Write-Host 'Display Name: ' $service.DisplayName -ForegroundColor White",
            "    Write-Host 'Status: ' $service.Status -ForegroundColor $(if ($service.Status -eq 'Running') { 'Green' } elseif ($service.Status -eq 'Stopped') { 'Red' } else { 'Yellow' })",
            "    Write-Host 'Start Type: ' $service.StartType -ForegroundColor White",
            "    ",
            "    # Get detailed service information",
            "    $serviceInfo = Get-WmiObject -Class Win32_Service | Where-Object { $_.Name -eq $service.Name }",
            "    if ($serviceInfo) {",
            "        Write-Host 'Service Account: ' $serviceInfo.StartName -ForegroundColor White",
            "        Write-Host 'Service Path: ' $serviceInfo.PathName -ForegroundColor White",
            "        Write-Host 'Service Description: ' $serviceInfo.Description -ForegroundColor White",
            "        Write-Host 'Service State: ' $serviceInfo.State -ForegroundColor White",
            "        Write-Host 'Service Start Mode: ' $serviceInfo.StartMode -ForegroundColor White",
            "        ",
            "        if ($serviceInfo.ProcessId -ne 0) {",
            "            $proc = Get-Process -Id $serviceInfo.ProcessId -ErrorAction SilentlyContinue",
            "            if ($proc) {",
            "                Write-Host 'Process ID: ' $proc.Id -ForegroundColor Green",
            "                Write-Host 'Process Name: ' $proc.ProcessName -ForegroundColor Green",
            "                Write-Host 'Process Start Time: ' $proc.StartTime -ForegroundColor Green",
            "                Write-Host 'Process CPU Time: ' $proc.TotalProcessorTime -ForegroundColor Green",
            "                Write-Host 'Process Memory: ' ([math]::Round($proc.WorkingSet64/1MB, 2)) 'MB' -ForegroundColor Green",
            "            }",
            "        }",
            "        ",
            "        # Get service dependencies",
            "        $dependencies = Get-Service -Name $service.Name -DependentServices -ErrorAction SilentlyContinue",
            "        if ($dependencies) {",
            "            Write-Host 'Dependent Services: ' ($dependencies | ForEach-Object { $_.Name }) -Join ', ' -ForegroundColor Cyan",
            "        }",
            "        ",
            "        $requiredServices = Get-Service -Name $service.Name -RequiredServices -ErrorAction SilentlyContinue",
            "        if ($requiredServices) {",
            "            Write-Host 'Required Services: ' ($requiredServices | ForEach-Object { $_.Name }) -Join ', ' -ForegroundColor Cyan",
            "        }",
            "    }",
            "    Write-Host '----------------------------------------' -ForegroundColor DarkGray",
            "}",
            "",
            "Write-Host '--- Service Summary ---' -ForegroundColor Magenta",
            "$runningCount = ($services | Where-Object { $_.Status -eq 'Running' }).Count",
            "$stoppedCount = ($services | Where-Object { $_.Status -eq 'Stopped' }).Count",
            "$otherCount = ($services | Where-Object { $_.Status -ne 'Running' -and $_.Status -ne 'Stopped' }).Count",
            "",
            "Write-Host 'Running Services: ' $runningCount -ForegroundColor Green",
            "Write-Host 'Stopped Services: ' $stoppedCount -ForegroundColor Red",
            "Write-Host 'Other Status: ' $otherCount -ForegroundColor Yellow",
            "",
            "Write-Host '=== LIST COMPLETED ===' -ForegroundColor Yellow",
            "Write-Host 'Completion Time: ' + (Get-Date -Format 'yyyy-MM-dd HH:mm:ss') -ForegroundColor Cyan"
        );
        
        return runPs(commands, context, Map.of(
            "searchTerm", searchTerm,
            "status", status,
            "limit", limit
        ));
    }
    
    private static JsonNode createJsonSchema() {
        return JsonSchemas.object(
            Map.of(
                "search", JsonSchemas.string("Search term to filter services by name or display name"),
                "status", JsonSchemas.string("Filter by service status: all, Running, Stopped, Paused"),
                "limit", JsonSchemas.integer(1, 200, 50)
            ),
            List.of()
        );
    }
}
