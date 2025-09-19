# MCP Backend Debug Script (PowerShell)
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "MCP Backend Debug Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Set the base directory
$BaseDir = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host "Starting mcp-controller-server in DEBUG mode..." -ForegroundColor Yellow
Write-Host ""

$controllerPath = Join-Path $BaseDir "mcp-controller-server"

# Start with debug parameters
Write-Host "Starting Spring Boot with debug configuration..." -ForegroundColor Yellow
Start-Process -FilePath "cmd" -ArgumentList "/k", "cd /d `"$controllerPath`" && .\mvnw.cmd spring-boot:run -Dspring-boot.run.jvmArguments=`"-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005`"" -WindowStyle Normal

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Backend started in DEBUG mode!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Debug port: 5005" -ForegroundColor Blue
Write-Host "Application: http://localhost:8080" -ForegroundColor Blue
Write-Host ""
Write-Host "To connect debugger:" -ForegroundColor Yellow
Write-Host "1. Open your IDE (IntelliJ IDEA, VS Code, Eclipse)" -ForegroundColor White
Write-Host "2. Create a Remote Debug configuration" -ForegroundColor White
Write-Host "3. Set host: localhost, port: 5005" -ForegroundColor White
Write-Host "4. Connect to start debugging" -ForegroundColor White
Write-Host ""
Write-Host "Press any key to exit..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
