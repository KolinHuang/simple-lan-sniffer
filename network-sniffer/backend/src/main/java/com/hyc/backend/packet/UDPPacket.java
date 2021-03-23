package com.hyc.backend.packet;

/**
 * @author kol Huang
 * @date 2021/3/22
 */
public class UDPPacket extends IPPacket {
    private int srcPort;

    private int dstPort;

    private int uLength;

    public UDPPacket() {
    }

    public UDPPacket(jpcap.packet.UDPPacket packet) {
        super(packet);

        this.srcPort = packet.src_port;
        this.dstPort = packet.dst_port;
        this.uLength = packet.length;
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

    public int getuLength() {
        return uLength;
    }

    public void setuLength(int uLength) {
        this.uLength = uLength;
    }
}
