package com.acme.mcp.core.security;

import java.util.Set;

/**
 * Security allowlist for PowerShell commands.
 * Validates that only approved commands can be executed.
 */
public class Allowlist {
    
    private static final Set<String> ALLOWED_COMMANDS = Set.of(
        "ipconfig",
        "netsh", 
        "Get-MpComputerStatus",
        "Start-MpScan",
        "winget",
        "Get-BitLockerVolume",
        "manage-bde",
        "Get-Service",
        "Restart-Service",
        "Stop-Process",
        "Start-Process",
        "Remove-Item",
        "Get-Process",
        "Get-ChildItem",
        "Compress-Archive",
        "Copy-Item",
        "New-Item",
        "Set-ExecutionPolicy",
        "taskkill",
        "Write-Host",
        "Get-WmiObject",
        "Get-Date",
        "Where-Object",
        "Select-Object",
        "ForEach-Object",
        "Start-Sleep"
    );
    
    /**
     * Check if a command is allowed by extracting the first token.
     * @param command the full command to check
     * @return true if the first token is in the allowlist
     */
    public boolean isCommandAllowed(String command) {
        if (command == null || command.trim().isEmpty()) {
            return false;
        }
        
        // Extract first token (command name)
        String firstToken = command.trim().split("\\s+")[0];
        
        // Remove any PowerShell operators or special characters
        firstToken = firstToken.replaceAll("[;&|]", "");
        
        return ALLOWED_COMMANDS.contains(firstToken);
    }
    
    /**
     * Get all allowed commands.
     * @return unmodifiable set of allowed command names
     */
    public Set<String> getAllowedCommands() {
        return Set.copyOf(ALLOWED_COMMANDS);
    }
}
