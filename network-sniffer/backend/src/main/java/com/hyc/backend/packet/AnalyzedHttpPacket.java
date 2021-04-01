package com.hyc.backend.packet;

import org.springframework.data.annotation.Id;

import java.util.Map;

/**
 * @author kol Huang
 * @date 2021/3/30
 */
public class AnalyzedHttpPacket extends AbsAnalyzedPacket {

    private String id;

    private Integer batchId;

    private boolean compressed;

    private String contentType;

    private Map<String, String> httpHeaders;

    private Boolean httpReq;

    private String method;

    public Boolean isHttpReq() {
        return httpReq;
    }

    public void setHttpReq(Boolean httpReq) {
        this.httpReq = httpReq;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

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

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }
}
