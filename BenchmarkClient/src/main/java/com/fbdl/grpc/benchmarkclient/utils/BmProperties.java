package com.fbdl.grpc.benchmarkclient.utils;

/**
 *
 * @author FBDL
 */
public enum BmProperties {
    
    INSTANCE;

    private int edgePort;
    private String edgeCertPath;
    private String edgeCertKey;
    private int edgeThreads;
    
    private String targetAddress;
    
    /**
     * @return the targetAddress
     */
    public String getTargetAddress() {
        return targetAddress;
    }

    /**
     * @param targetAddress the targetAddress to set
     */
    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
    }
    
    /**
     * @return the edgePort
     */
    public int getEdgePort() {
        return edgePort;
    }

    /**
     * @param edgePort the edgePort to set
     */
    public void setEdgePort(int edgePort) {
        this.edgePort = edgePort;
    }

    /**
     * @return the edgeCertPath
     */
    public String getEdgeCertPath() {
        return edgeCertPath;
    }

    /**
     * @param edgeCertPath the edgeCertPath to set
     */
    public void setEdgeCertPath(String edgeCertPath) {
        this.edgeCertPath = edgeCertPath;
    }

    /**
     * @return the edgeCertKey
     */
    public String getEdgeCertKey() {
        return edgeCertKey;
    }

    /**
     * @param edgeCertKey the edgeCertKey to set
     */
    public void setEdgeCertKey(String edgeCertKey) {
        this.edgeCertKey = edgeCertKey;
    }

    /**
     * @return the edgeThreads
     */
    public int getEdgeThreads() {
        return edgeThreads;
    }

    /**
     * @param edgeThreads the edgeThreads to set
     */
    public void setEdgeThreads(int edgeThreads) {
        this.edgeThreads = edgeThreads;
    }    
}
