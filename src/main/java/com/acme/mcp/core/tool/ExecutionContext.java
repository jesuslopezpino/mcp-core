package com.acme.mcp.core.tool;

/**
 * Context information for tool execution.
 * Contains metadata about who is executing the tool and on what asset.
 */
public class ExecutionContext {
    
    private final String userId;
    private final String assetId;
    private final String correlationId;
    
    public ExecutionContext(String userId, String assetId, String correlationId) {
        this.userId = userId;
        this.assetId = assetId;
        this.correlationId = correlationId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getAssetId() {
        return assetId;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }
    
    @Override
    public String toString() {
        return "ExecutionContext{" +
                "userId='" + userId + '\'' +
                ", assetId='" + assetId + '\'' +
                ", correlationId='" + correlationId + '\'' +
                '}';
    }
}
