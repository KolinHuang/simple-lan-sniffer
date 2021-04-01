/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.hyc.backend.analysis.arp;

import com.hyc.backend.analysis.ISingleAnalysisRealm;
import com.hyc.backend.analysis.ProtocolEnum;
import com.hyc.backend.packet.ARPPacket;
import com.hyc.backend.packet.AbsAnalyzedPacket;
import com.hyc.backend.packet.AnalyzedArpPacket;
import com.hyc.backend.packet.Packet;
import com.hyc.backend.utils.Helper;
import com.hyc.backend.utils.NetworkUtils;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Frankie
 */
public class ArpAnalysisRealm implements ISingleAnalysisRealm {
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
        return ProtocolEnum.ARP.name();
    }

    @Override
    public void initPacket(Integer batchId, String capturePacketId, boolean upstream, Packet packet) {
        this.batchId = batchId;
        this.capturedPacketIds.add(capturePacketId);
        this.upstream = upstream;

        ARPPacket arpPacketModel = (ARPPacket) packet;
        this.time = new Date(packet.getSec() * 1000L + packet.getUsec());
        this.contentBytes = arpPacketModel.getData();
        this.srcMac = NetworkUtils.bytesToMac(arpPacketModel.getSenderHardAddr());
        this.destMac = NetworkUtils.bytesToMac(arpPacketModel.getTargetHardAddr());
        this.srcIp = NetworkUtils.bytesToIp(arpPacketModel.getSenderProtoAddr());
        this.destIp = NetworkUtils.bytesToIp(arpPacketModel.getTargetProtoAddr());
    }

    @Override
    public AbsAnalyzedPacket makePacket4Save() {
        String realContent = null;
        try {
            realContent = new String(this.contentBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        AnalyzedArpPacket analyzedUdpPacket = new AnalyzedArpPacket();
        analyzedUdpPacket.setTime(this.time);
        if (this.time != null) {
            analyzedUdpPacket.setMinuteTimeStr(Helper.MINUTE_DATE_FORMAT.format(this.time));
        }
        analyzedUdpPacket.setProtocol(this.protocol());
        analyzedUdpPacket.setUpstream(this.upstream);
        analyzedUdpPacket.setBatchId(this.batchId);
        analyzedUdpPacket.setCapturedPacketIds(this.capturedPacketIds);
        analyzedUdpPacket.setData(this.contentBytes);
        analyzedUdpPacket.setContent(realContent);
        analyzedUdpPacket.setSrcIp(this.srcIp);
        analyzedUdpPacket.setDstIp(this.destIp);
        analyzedUdpPacket.setSrcMac(this.srcMac);
        analyzedUdpPacket.setDstMac(this.destMac);
        System.out.println("++++++save arp packet");
        return analyzedUdpPacket;
    }

}
