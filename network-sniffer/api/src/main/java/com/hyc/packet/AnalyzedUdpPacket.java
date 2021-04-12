
package com.hyc.packet;


public class AnalyzedUdpPacket extends AbsAnalyzedPacket {

    private String id;

    private Integer batchId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }
}
