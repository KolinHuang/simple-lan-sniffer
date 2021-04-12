
package com.hyc.analysis.udp;

import com.hyc.analysis.ISingleAnalysisRealm;
import com.hyc.analysis.ProtocolEnum;

import com.hyc.packet.AbsAnalyzedPacket;
import com.hyc.packet.AnalyzedUdpPacket;
import com.hyc.packet.Packet;
import com.hyc.packet.UDPPacket;
import com.hyc.utils.Helper;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * @author Frankie
 */
public class UdpAnalysisRealm implements ISingleAnalysisRealm {
    protected Date time;
    protected boolean upstream;
    protected Integer batchId;
//    protected List<String> capturedPacketIds = new ArrayList<>();
    protected byte[] contentBytes;
    protected String srcMac;
    protected String destMac;
    protected String srcIp;
    protected String destIp;
    protected int srcPort;
    protected int destPort;

    @Override
    public String protocol() {
        return ProtocolEnum.UDP.name();
    }

    @Override
    public void initPacket(Integer batchId, boolean upstream, Packet packet) {
        this.batchId = batchId;
        this.upstream = upstream;

        UDPPacket tcpPacketModel = (UDPPacket) packet;
        this.time = new Date(packet.getSec() * 1000L + packet.getUsec());
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
    public AbsAnalyzedPacket makePacket4Save() {
        String realContent = null;
        try {
            realContent = new String(this.contentBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        AnalyzedUdpPacket analyzedUdpPacket = new AnalyzedUdpPacket();
        analyzedUdpPacket.setTime(this.time);
        if (this.time != null) {
            analyzedUdpPacket.setMinuteTimeStr(Helper.MINUTE_DATE_FORMAT.format(this.time));
        }
        analyzedUdpPacket.setProtocol(this.protocol());
        analyzedUdpPacket.setUpstream(this.upstream);
        analyzedUdpPacket.setBatchId(this.batchId);
//        analyzedUdpPacket.setCapturedPacketIds(this.capturedPacketIds);
        analyzedUdpPacket.setData(this.contentBytes);
        analyzedUdpPacket.setContent(realContent);
        analyzedUdpPacket.setSrcIp(this.srcIp);
        analyzedUdpPacket.setDstIp(this.destIp);
        analyzedUdpPacket.setSrcPort(this.srcPort);
        analyzedUdpPacket.setDstPort(this.destPort);
        analyzedUdpPacket.setSrcMac(this.srcMac);
        analyzedUdpPacket.setDstMac(this.destMac);
        System.out.println("++++++save udp packet");
        return analyzedUdpPacket;
    }
}
