package com.acme.mcp.core.windows;

import com.acme.mcp.core.tool.ExecuteResult;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Executes PowerShell commands on Windows systems.
 * Uses secure execution policy and no profile for consistent behavior.
 */
public class PowerShellRunner {
    
    private static final String POWERSHELL_CMD = "powershell.exe";
    private static final String[] POWERSHELL_ARGS = {
        "-NoProfile",
        "-ExecutionPolicy", "Bypass",
        "-Command"
    };
    
    /**
     * Execute a PowerShell command.
     * @param command the PowerShell command to execute
     * @return execution result with output and exit code
     */
    public ExecuteResult execute(String command) {
        String executionId = UUID.randomUUID().toString();
        
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(POWERSHELL_CMD, POWERSHELL_ARGS[0], POWERSHELL_ARGS[1], 
                                 POWERSHELL_ARGS[2], POWERSHELL_ARGS[3], command);
            
            Process process = processBuilder.start();
            
            // Read output streams
            String stdout = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
            String stderr = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
            
            // Wait for process completion with timeout
            boolean finished = process.waitFor(30, TimeUnit.SECONDS);
            int exitCode = finished ? process.exitValue() : -1;
            
            ExecuteResult.Status status;
            if (!finished) {
                status = ExecuteResult.Status.ERROR;
                process.destroyForcibly();
            } else if (exitCode == 0) {
                status = ExecuteResult.Status.SUCCESS;
            } else {
                status = ExecuteResult.Status.FAILURE;
            }
            
            return new ExecuteResult(executionId, exitCode, stdout, stderr, status);
            
        } catch (IOException | InterruptedException e) {
            return new ExecuteResult(executionId, -1, "", 
                "Execution failed: " + e.getMessage(), ExecuteResult.Status.ERROR);
        }
    }
    
    /**
     * Execute multiple PowerShell commands sequentially.
     * @param commands array of PowerShell commands
     * @return execution result of the last command
     */
    public ExecuteResult execute(String... commands) {
        if (commands == null || commands.length == 0) {
            return new ExecuteResult(UUID.randomUUID().toString(), -1, "", 
                "No commands provided", ExecuteResult.Status.ERROR);
        }
        
        // Join commands with semicolon separator
        String combinedCommand = String.join(" ; ", commands);
        return execute(combinedCommand);
    }
}
