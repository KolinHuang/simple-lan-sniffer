/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.hyc.backend.analysis.tcp;

import com.hyc.backend.analysis.IAnalysisRealm;
import com.hyc.backend.analysis.ProtocolEnum;
import com.hyc.backend.packet.AbsAnalyzedPacket;
import com.hyc.backend.packet.AnalyzedTcpPacket;
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
public class TcpAnalysisRealm implements IAnalysisRealm {
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
    protected int srcPort;
    protected int destPort;

    @Override
    public String protocol() {
        return ProtocolEnum.TCP.name();
    }

    @Override
    public void initPacket(Integer batchId, String capturePacketId, boolean upstream, Packet packet) {
        this.batchId = batchId;
        this.capturedPacketIds.add(capturePacketId);
        this.upstream = upstream;

        TCPPacket tcpPacketModel = (TCPPacket) packet;
        this.time = new Date(packet.getSec() * 1000L + packet.getUsec());
        this.ackNum = tcpPacketModel.getAckNum();
        this.contentBytes = tcpPacketModel.getData();
        this.srcIp = tcpPacketModel.getSrcIP().getHostAddress();
        this.destIp = tcpPacketModel.getDstIP().getHostAddress();
        this.srcPort = tcpPacketModel.getSrcPort();
        this.destPort = tcpPacketModel.getDstPort();
        if (tcpPacketModel.getEthernetPacket() != null) {
            this.srcMac = tcpPacketModel.getEthernetPacket().readSourceAddress();
            this.destMac = tcpPacketModel.getEthernetPacket().readDestinationAddress();
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
            realContent = new String(this.contentBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        AnalyzedTcpPacket analyzedTcpPacket = new AnalyzedTcpPacket();
        analyzedTcpPacket.setTime(this.time);
        if (this.time != null) {
            analyzedTcpPacket.setMinuteTimeStr(Helper.MINUTE_DATE_FORMAT.format(this.time));
        }
        analyzedTcpPacket.setProtocol(this.protocol());
        analyzedTcpPacket.setUpstream(this.upstream);
        analyzedTcpPacket.setBatchId(this.batchId);
        analyzedTcpPacket.setCapturedPacketIds(this.capturedPacketIds);
        analyzedTcpPacket.setAckNum(this.ackNum);
        analyzedTcpPacket.setData(this.contentBytes);
        analyzedTcpPacket.setContent(realContent);
        analyzedTcpPacket.setSrcIp(this.srcIp);
        analyzedTcpPacket.setDstIp(this.destIp);
        analyzedTcpPacket.setSrcPort(this.srcPort);
        analyzedTcpPacket.setDstPort(this.destPort);
        analyzedTcpPacket.setSrcMac(this.srcMac);
        analyzedTcpPacket.setDstMac(this.destMac);
        System.out.println("++++++save tcp packet: " + this.ackNum);
        return analyzedTcpPacket;
    }
}
