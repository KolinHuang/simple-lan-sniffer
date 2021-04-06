package com.hyc.backend.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hyc.backend.packet.Packet;

import java.util.Date;
import java.util.List;

/**
 * @author kol Huang
 * @date 2021/4/1
 */
public abstract class AbsCapturedPacket {

    private Date created;

    private boolean upStream;
    @JsonIgnore
    private Packet packet;

    private Integer batchId;

    private Date startAttackTime;

    private List<String> tags;


    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isUpStream() {
        return upStream;
    }

    public void setUpStream(boolean upStream) {
        this.upStream = upStream;
    }

    public Integer getBatchId() {
        return batchId;
    }

    public void setBatchId(Integer batchId) {
        this.batchId = batchId;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public Date getStartAttackTime() {
        return startAttackTime;
    }

    public void setStartAttackTime(Date startAttackTime) {
        this.startAttackTime = startAttackTime;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
