/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.hyc.backend.analysis.icmp;

import com.hyc.backend.analysis.ISingleAnalysisRealm;
import com.hyc.backend.analysis.ProtocolEnum;
import com.hyc.backend.packet.AbsAnalyzedPacket;
import com.hyc.backend.packet.AnalyzedIcmpPacket;
import com.hyc.backend.packet.ICMPPacket;
import com.hyc.backend.packet.Packet;
import com.hyc.backend.utils.Helper;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Frankie
 */
public class IcmpAnalysisRealm implements ISingleAnalysisRealm {
    protected Date time;
    protected boolean upstream;
    protected Integer batchId;
    protected List<String> capturedPacketIds = new ArrayList<>();
    protected byte[] contentBytes;
    protected String srcMac;
    protected String destMac;
    protected String srcIp;
    protected String destIp;

    @Override
    public String protocol() {
        return ProtocolEnum.ICMP.name();
    }

    public void initPacket(Integer batchId, String capturePacketId, boolean upstream, Packet packet) {
        this.batchId = batchId;
        this.capturedPacketIds.add(capturePacketId);
        this.upstream = upstream;

        ICMPPacket icmpPacketModel = (ICMPPacket) packet;
        this.time = new Date(packet.getSec() * 1000L + packet.getUsec());
        this.contentBytes = icmpPacketModel.getData();
        this.srcIp = icmpPacketModel.getSrcIP().getHostAddress();
        this.destIp = icmpPacketModel.getDstIP().getHostAddress();
        if (icmpPacketModel.getEthernetPacket() != null) {
            this.srcMac = icmpPacketModel.getEthernetPacket().readSourceAddress();
            this.destMac = icmpPacketModel.getEthernetPacket().readDestinationAddress();
        }
    }

    @Override
    public AbsAnalyzedPacket makePacket4Save() {
        String realContent = null;
        try {
            realContent = new String(this.contentBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        AnalyzedIcmpPacket analyzedIcmpPacket = new AnalyzedIcmpPacket();
        analyzedIcmpPacket.setTime(this.time);
        if (this.time != null) {
            analyzedIcmpPacket.setMinuteTimeStr(Helper.MINUTE_DATE_FORMAT.format(this.time));
        }
        analyzedIcmpPacket.setProtocol(this.protocol());
        analyzedIcmpPacket.setUpstream(this.upstream);
        analyzedIcmpPacket.setBatchId(this.batchId);
        analyzedIcmpPacket.setCapturedPacketIds(this.capturedPacketIds);
        analyzedIcmpPacket.setData(this.contentBytes);
        analyzedIcmpPacket.setContent(realContent);
        analyzedIcmpPacket.setSrcIp(this.srcIp);
        analyzedIcmpPacket.setDstIp(this.destIp);
        analyzedIcmpPacket.setSrcMac(this.srcMac);
        analyzedIcmpPacket.setDstMac(this.destMac);
        System.out.println("++++++save icmp packet");
        return analyzedIcmpPacket;
    }
}
