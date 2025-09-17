package com.acme.mcp.core.tools;

import com.acme.mcp.core.security.Allowlist;
import com.acme.mcp.core.tool.ToolRegistry;
import com.acme.mcp.core.windows.PowerShellRunner;

/**
 * Windows Starter Pack - collection of commonly used Windows administration tools.
 * Provides easy registration of all tools with their aliases.
 */
public class WindowsStarterPack {
    
    /**
     * Register all Windows Starter Pack tools in the given registry.
     * @param registry the tool registry to register tools in
     */
    public static void registerAll(ToolRegistry registry) {
        // System tools
        registry.register(new SystemResetNetworkTool());
        registry.register(new SystemRestartServiceTool());
        registry.register(new SystemClearTempTool());
        
        // Application tools
        registry.register(new AppsInstallTool());
        registry.register(new AppsRepairTeamsTool());
        
        // Security tools
        registry.register(new SecurityQuickScanDefenderTool());
        registry.register(new SecurityCheckBitlockerTool());
        
        // File tools
        registry.register(new FilesBackupUserDocsTool());
    }
    
    /**
     * Register all Windows Starter Pack tools with custom PowerShell runner and allowlist.
     * @param registry the tool registry to register tools in
     * @param powerShellRunner custom PowerShell runner (for dry-run mode, etc.)
     * @param allowlist custom allowlist for security
     */
    public static void registerAll(ToolRegistry registry, PowerShellRunner powerShellRunner, Allowlist allowlist) {
        // System tools
        registry.register(new SystemResetNetworkTool(powerShellRunner, allowlist));
        registry.register(new SystemRestartServiceTool(powerShellRunner, allowlist));
        registry.register(new SystemClearTempTool(powerShellRunner, allowlist));
        
        // Application tools
        registry.register(new AppsInstallTool(powerShellRunner, allowlist));
        registry.register(new AppsRepairTeamsTool(powerShellRunner, allowlist));
        
        // Security tools
        registry.register(new SecurityQuickScanDefenderTool(powerShellRunner, allowlist));
        registry.register(new SecurityCheckBitlockerTool(powerShellRunner, allowlist));
        
        // File tools
        registry.register(new FilesBackupUserDocsTool(powerShellRunner, allowlist));
    }
    
    /**
     * Get the list of all tool names in the Windows Starter Pack.
     * @return list of tool names
     */
    public static String[] getToolNames() {
        return new String[]{
            "system.reset_network",
            "system.restart_service", 
            "system.clear_temp",
            "apps.install",
            "apps.repair_teams",
            "security.quick_scan_defender",
            "security.check_bitlocker",
            "files.backup_user_docs"
        };
    }
    
    /**
     * Get the list of all legacy aliases in the Windows Starter Pack.
     * @return list of legacy alias names
     */
    public static String[] getLegacyAliases() {
        return new String[]{
            "system_reset_network"
        };
    }
}
