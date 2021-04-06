package com.hyc.backend.packet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author kol Huang
 * @date 2021/3/30
 */
public class AbsAnalyzedPacket implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(AbsAnalyzedPacket.class);

    private static final int THRESHOLD_BYTE_SIZE = 16000000;

    protected String protocol;

    protected Long ackNum;

    protected List<String> capturedPacketIds;

    protected boolean upstream;
    protected boolean minimized = false;
    protected byte[] data;
    protected String content;
    protected Date created = new Date();
    protected Date time;//包内的真实时间
    protected String minuteTimeStr;

    protected String srcMac;
    protected String dstMac;
    protected String srcIp;
    protected String dstIp;
    protected int srcPort;
    protected int dstPort;

    public void minimizeContent() {
        logger.info("the packet is too large, so minimize it.");
        this.minimized = true;
        setContent("--Too large to display--");
        if (data.length >= THRESHOLD_BYTE_SIZE) {
            setData(new byte[0]);
        }
    }


    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Long getAckNum() {
        return ackNum;
    }

    public void setAckNum(Long ackNum) {
        this.ackNum = ackNum;
    }

    public List<String> getCapturedPacketIds() {
        return capturedPacketIds;
    }

    public void setCapturedPacketIds(List<String> capturedPacketIds) {
        this.capturedPacketIds = capturedPacketIds;
    }

    public boolean isUpstream() {
        return upstream;
    }

    public void setUpstream(boolean upstream) {
        this.upstream = upstream;
    }

    public boolean isMinimized() {
        return minimized;
    }

    public void setMinimized(boolean minimized) {
        this.minimized = minimized;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getMinuteTimeStr() {
        return minuteTimeStr;
    }

    public void setMinuteTimeStr(String minuteTimeStr) {
        this.minuteTimeStr = minuteTimeStr;
    }

    public String getSrcMac() {
        return srcMac;
    }

    public void setSrcMac(String srcMac) {
        this.srcMac = srcMac;
    }

    public String getDstMac() {
        return dstMac;
    }

    public void setDstMac(String dstMac) {
        this.dstMac = dstMac;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public String getDstIp() {
        return dstIp;
    }

    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    public int getDstPort() {
        return dstPort;
    }

    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

}