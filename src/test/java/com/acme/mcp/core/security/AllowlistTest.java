package com.acme.mcp.core.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Allowlist security functionality.
 */
class AllowlistTest {
    
    private Allowlist allowlist;
    
    @BeforeEach
    void setUp() {
        allowlist = new Allowlist();
    }
    
    @Test
    void testAllowedCommands() {
        // Test allowed commands
        assertTrue(allowlist.isCommandAllowed("ipconfig /flushdns"));
        assertTrue(allowlist.isCommandAllowed("netsh winsock reset"));
        assertTrue(allowlist.isCommandAllowed("Get-MpComputerStatus"));
        assertTrue(allowlist.isCommandAllowed("Start-MpScan -ScanType QuickScan"));
        assertTrue(allowlist.isCommandAllowed("winget install --accept-source-agreements"));
        assertTrue(allowlist.isCommandAllowed("Get-BitLockerVolume"));
        assertTrue(allowlist.isCommandAllowed("manage-bde -status"));
        assertTrue(allowlist.isCommandAllowed("Get-Service"));
        assertTrue(allowlist.isCommandAllowed("Restart-Service -Name Spooler"));
        assertTrue(allowlist.isCommandAllowed("Stop-Process -Name notepad"));
        assertTrue(allowlist.isCommandAllowed("Start-Process notepad"));
        assertTrue(allowlist.isCommandAllowed("Remove-Item -Recurse -Force C:\\Temp"));
        assertTrue(allowlist.isCommandAllowed("Get-Process"));
        assertTrue(allowlist.isCommandAllowed("Get-ChildItem C:\\"));
        assertTrue(allowlist.isCommandAllowed("Compress-Archive -Path C:\\Temp -DestinationPath C:\\backup.zip"));
        assertTrue(allowlist.isCommandAllowed("Copy-Item C:\\file.txt C:\\backup.txt"));
        assertTrue(allowlist.isCommandAllowed("New-Item -ItemType Directory -Path C:\\NewDir"));
        assertTrue(allowlist.isCommandAllowed("Set-ExecutionPolicy -ExecutionPolicy RemoteSigned"));
        assertTrue(allowlist.isCommandAllowed("taskkill /IM notepad.exe /F"));
    }
    
    @Test
    void testDisallowedCommands() {
        // Test disallowed commands
        assertFalse(allowlist.isCommandAllowed("format C:"));
        assertFalse(allowlist.isCommandAllowed("del C:\\Windows\\System32\\*"));
        assertFalse(allowlist.isCommandAllowed("rmdir /s C:\\Windows"));
        assertFalse(allowlist.isCommandAllowed("shutdown /s /t 0"));
        assertFalse(allowlist.isCommandAllowed("net user administrator /active:yes"));
        assertFalse(allowlist.isCommandAllowed("reg delete HKLM\\SOFTWARE"));
        assertFalse(allowlist.isCommandAllowed("wmic process delete"));
        assertFalse(allowlist.isCommandAllowed("bcdedit /delete"));
        assertFalse(allowlist.isCommandAllowed("diskpart"));
        assertFalse(allowlist.isCommandAllowed("attrib -r -s -h C:\\Windows\\*"));
    }
    
    @Test
    void testCommandsWithOperators() {
        // Test commands with PowerShell operators
        assertTrue(allowlist.isCommandAllowed("ipconfig /flushdns; netsh winsock reset"));
        assertTrue(allowlist.isCommandAllowed("Get-Service | Where-Object {$_.Status -eq 'Running'}"));
        assertTrue(allowlist.isCommandAllowed("Get-Process && Stop-Process -Name notepad"));
    }
    
    @Test
    void testNullAndEmptyCommands() {
        assertFalse(allowlist.isCommandAllowed(null));
        assertFalse(allowlist.isCommandAllowed(""));
        assertFalse(allowlist.isCommandAllowed("   "));
    }
    
    @Test
    void testGetAllowedCommands() {
        var allowedCommands = allowlist.getAllowedCommands();
        
        assertNotNull(allowedCommands);
        assertTrue(allowedCommands.contains("ipconfig"));
        assertTrue(allowedCommands.contains("netsh"));
        assertTrue(allowedCommands.contains("Get-MpComputerStatus"));
        assertTrue(allowedCommands.contains("winget"));
        assertTrue(allowedCommands.contains("taskkill"));
        
        // Should be unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> {
            allowedCommands.add("newcommand");
        });
    }
    
    @Test
    void testCaseSensitivity() {
        // Commands should be case-sensitive as defined in the allowlist
        assertTrue(allowlist.isCommandAllowed("Get-Service"));
        assertFalse(allowlist.isCommandAllowed("get-service"));
        assertFalse(allowlist.isCommandAllowed("GET-SERVICE"));
    }
}
