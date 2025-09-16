package com.acme.mcp.core.tool;

/**
 * Result of tool execution.
 * Contains execution metadata and output information.
 */
public class ExecuteResult {
    
    public enum Status {
        SUCCESS,
        FAILURE,
        ERROR
    }
    
    private final String executionId;
    private final int exitCode;
    private final String stdout;
    private final String stderr;
    private final Status status;
    
    public ExecuteResult(String executionId, int exitCode, String stdout, String stderr, Status status) {
        this.executionId = executionId;
        this.exitCode = exitCode;
        this.stdout = stdout;
        this.stderr = stderr;
        this.status = status;
    }
    
    public String getExecutionId() {
        return executionId;
    }
    
    public int getExitCode() {
        return exitCode;
    }
    
    public String getStdout() {
        return stdout;
    }
    
    public String getStderr() {
        return stderr;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public boolean isSuccess() {
        return status == Status.SUCCESS && exitCode == 0;
    }
    
    @Override
    public String toString() {
        return "ExecuteResult{" +
                "executionId='" + executionId + '\'' +
                ", exitCode=" + exitCode +
                ", status=" + status +
                ", stdout='" + stdout + '\'' +
                ", stderr='" + stderr + '\'' +
                '}';
    }
}
