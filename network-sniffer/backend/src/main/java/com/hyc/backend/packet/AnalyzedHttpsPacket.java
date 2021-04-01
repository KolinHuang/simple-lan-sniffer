package com.hyc.backend.packet;


/**
 * @author Frankie
 */
public class AnalyzedHttpsPacket extends AbsAnalyzedPacket {

    private String id;

    private Integer batchId;

    private boolean decrypted;

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

    public boolean isDecrypted() {
        return decrypted;
    }

    public void setDecrypted(boolean decrypted) {
        this.decrypted = decrypted;
    }
}
