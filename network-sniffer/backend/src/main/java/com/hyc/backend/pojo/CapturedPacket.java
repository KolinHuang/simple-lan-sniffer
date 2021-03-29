package com.hyc.backend.pojo;

import com.hyc.backend.packet.Packet;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author kol Huang
 * @date 2021/3/29
 */
public class CapturedPacket implements Serializable {
    private String id;

    private Date created = new Date();

    private boolean isUpStream;

    private Integer batchId;

    private Date startAttackTime;

    private Packet packet;

    private List<String> tags;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isUpStream() {
        return isUpStream;
    }

    public void setUpStream(boolean upStream) {
        isUpStream = upStream;
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public Date getStartAttackTime() {
        return startAttackTime;
    }

    public void setStartAttackTime(Date startAttackTime) {
        this.startAttackTime = startAttackTime;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
