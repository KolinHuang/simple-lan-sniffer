/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.hyc.analysis.icmp;

import com.hyc.analysis.ISingleAnalysisRealm;
import com.hyc.analysis.ProtocolEnum;
import com.hyc.packet.AbsAnalyzedPacket;
import com.hyc.packet.AnalyzedIcmpPacket;
import com.hyc.packet.ICMPPacket;
import com.hyc.packet.Packet;
import com.hyc.utils.Helper;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author Frankie
 */
public class IcmpAnalysisRealm implements ISingleAnalysisRealm {
    protected Date time;
    protected boolean upstream;
    protected Integer batchId;
    protected byte[] contentBytes;
    protected String srcMac;
    protected String destMac;
    protected String srcIp;
    protected String destIp;

    @Override
    public String protocol() {
        return ProtocolEnum.ICMP.name();
    }

    public void initPacket(Integer batchId, boolean upstream, Packet packet) {
        this.batchId = batchId;
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
        realContent = new String(this.contentBytes, StandardCharsets.UTF_8);

        AnalyzedIcmpPacket analyzedIcmpPacket = new AnalyzedIcmpPacket();
        analyzedIcmpPacket.setTime(this.time);
        if (this.time != null) {
            analyzedIcmpPacket.setMinuteTimeStr(Helper.MINUTE_DATE_FORMAT.format(this.time));
        }
        analyzedIcmpPacket.setProtocol(this.protocol());
        analyzedIcmpPacket.setUpstream(this.upstream);
        analyzedIcmpPacket.setBatchId(this.batchId);
//        analyzedIcmpPacket.setCapturedPacketIds(this.capturedPacketIds);
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
