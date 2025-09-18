# MCP Projects Restart Script (PowerShell)
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "MCP Projects Restart Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Set the base directory
$BaseDir = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "Stopping any running processes..." -ForegroundColor Yellow
Write-Host ""

# Function to kill processes on specific port
function Stop-ProcessOnPort {
    param([int]$Port, [string]$ServiceName)
    
    Write-Host "Checking port $Port for $ServiceName processes..." -ForegroundColor Yellow
    
    try {
        $connections = netstat -ano | Select-String ":$Port "
        if ($connections) {
            foreach ($connection in $connections) {
                $parts = $connection.ToString().Split() | Where-Object { $_ -ne "" }
                if ($parts.Length -ge 5) {
                    $processId = $parts[-1]
                    if ($processId -match '^\d+$') {
                        Write-Host "Found process on port $Port : PID $processId" -ForegroundColor Yellow
                        try {
                            Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
                            Write-Host "Process $processId on port $Port stopped." -ForegroundColor Green
                        } catch {
                            Write-Host "Could not stop process $processId on port $Port" -ForegroundColor Red
                        }
                    }
                }
            }
        } else {
            Write-Host "No processes found on port $Port" -ForegroundColor Gray
        }
        Write-Host "Port $Port cleared." -ForegroundColor Green
    } catch {
        Write-Host "Error checking port $Port : $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Check and kill processes on port 4200 (mcp-admin-ui)
Stop-ProcessOnPort -Port 4200 -ServiceName "mcp-admin-ui"

# Check and kill processes on port 8080 (mcp-controller-server)
Stop-ProcessOnPort -Port 8080 -ServiceName "mcp-controller-server"

# Additional cleanup for Node.js processes
Write-Host "Stopping any remaining Node.js processes..." -ForegroundColor Yellow
$nodeProcesses = Get-Process -Name "node" -ErrorAction SilentlyContinue
if ($nodeProcesses) {
    $nodeProcesses | Stop-Process -Force
    Write-Host "Node.js processes stopped." -ForegroundColor Green
} else {
    Write-Host "No Node.js processes found." -ForegroundColor Gray
}

# Additional cleanup for Java processes
Write-Host "Stopping any remaining Java processes..." -ForegroundColor Yellow
$javaProcesses = Get-Process -Name "java" -ErrorAction SilentlyContinue
if ($javaProcesses) {
    $javaProcesses | Stop-Process -Force
    Write-Host "Java processes stopped." -ForegroundColor Green
} else {
    Write-Host "No Java processes found." -ForegroundColor Gray
}

Write-Host ""
Write-Host "Waiting 3 seconds before restarting..." -ForegroundColor Yellow
Start-Sleep -Seconds 3

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Starting MCP Projects" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Start mcp-controller-server (Spring Boot)
Write-Host "Starting mcp-controller-server..." -ForegroundColor Yellow
$controllerPath = Join-Path $BaseDir "mcp-controller-server"
Start-Process -FilePath "cmd" -ArgumentList "/k", "cd /d `"$controllerPath`" && .\mvnw.cmd spring-boot:run" -WindowStyle Normal
Write-Host "mcp-controller-server started in new window." -ForegroundColor Green

# Wait a moment for the server to start
Start-Sleep -Seconds 5

# Start mcp-admin-ui (Angular)
Write-Host "Starting mcp-admin-ui..." -ForegroundColor Yellow
$uiPath = Join-Path $BaseDir "mcp-admin-ui"
Start-Process -FilePath "cmd" -ArgumentList "/k", "cd /d `"$uiPath`" && npm start" -WindowStyle Normal
Write-Host "mcp-admin-ui started in new window." -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Both projects have been restarted!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "mcp-controller-server: http://localhost:8080" -ForegroundColor Blue
Write-Host "mcp-admin-ui: http://localhost:4200" -ForegroundColor Blue
Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
