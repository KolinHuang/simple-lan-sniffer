/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.hyc.backend.analysis.http;

import com.hyc.backend.analysis.IAnalysisRealm;
import com.hyc.backend.analysis.ProtocolEnum;
import com.hyc.backend.packet.AbsAnalyzedPacket;
import com.hyc.backend.packet.AnalyzedHttpsPacket;
import com.hyc.backend.packet.Packet;
import com.hyc.backend.packet.TCPPacket;
import com.hyc.backend.utils.Helper;
import com.hyc.backend.utils.NetworkUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Frankie
 */
@SuppressWarnings("Duplicates")
public class EncryptedHttpsAnalysisRealm implements IAnalysisRealm {
    protected Date time;
    protected boolean upstream;
    protected Integer batchId;
    protected List<String> capturedPacketIds = new ArrayList<>();
    protected Long ackNum;
    protected byte[] contentBytes;
    protected String srcMac;
    protected String destMac;
    protected String srcIp;
    protected String destIp;

    @Override
    public String protocol() {
        return ProtocolEnum.HTTPS.name();
    }

    @Override
    public void initPacket(Integer batchId, String capturePacketId, boolean upstream, Packet packet) {
        this.batchId = batchId;
        this.capturedPacketIds.add(capturePacketId);
        this.upstream = upstream;

        TCPPacket ipPacketModel = (TCPPacket) packet;
        this.time = new Date(packet.getSec() * 1000L + packet.getUsec());
        this.ackNum = ipPacketModel.getAckNum();
        this.contentBytes = ipPacketModel.getData();
        this.srcIp = ipPacketModel.getSrcIP().getHostAddress();
        this.destIp = ipPacketModel.getDstIP().getHostAddress();
        if (ipPacketModel.getEthernetPacket() != null) {
            this.srcMac = ipPacketModel.getEthernetPacket().readSourceAddress();
            this.destMac = ipPacketModel.getEthernetPacket().readDestinationAddress();
        }
    }

    @Override
    public void appendPacket(String capturePacketId, Packet packet) {
        this.capturedPacketIds.add(capturePacketId);

        TCPPacket tcpPacketModel = (TCPPacket) packet;
        byte[] newBytes = NetworkUtils.concatBytes(this.contentBytes, tcpPacketModel.getData());
        this.contentBytes = newBytes;
    }

    @Override
    public AbsAnalyzedPacket makePacket4Save() {
        String realContent = null;
        try {
            realContent = new String(this.contentBytes, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        AnalyzedHttpsPacket analyzedHttpsPacket = new AnalyzedHttpsPacket();
        analyzedHttpsPacket.setTime(this.time);
        if (this.time != null) {
            analyzedHttpsPacket.setMinuteTimeStr(Helper.MINUTE_DATE_FORMAT.format(this.time));
        }
        analyzedHttpsPacket.setProtocol(this.protocol());
        analyzedHttpsPacket.setUpstream(this.upstream);
        analyzedHttpsPacket.setBatchId(this.batchId);
        analyzedHttpsPacket.setCapturedPacketIds(this.capturedPacketIds);
        analyzedHttpsPacket.setAckNum(this.ackNum);
        analyzedHttpsPacket.setData(this.contentBytes);
        analyzedHttpsPacket.setContent(realContent);
        analyzedHttpsPacket.setSrcIp(this.srcIp);
        analyzedHttpsPacket.setDstIp(this.destIp);
        analyzedHttpsPacket.setSrcMac(this.srcMac);
        analyzedHttpsPacket.setDstMac(this.destMac);
        System.out.println("++++++save https packet: " + this.ackNum);
        return analyzedHttpsPacket;
    }
}
