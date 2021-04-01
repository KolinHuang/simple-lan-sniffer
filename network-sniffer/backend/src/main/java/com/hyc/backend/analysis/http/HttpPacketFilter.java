package com.hyc.backend.analysis.http;

import com.hyc.backend.analysis.IPacketFilter;
import com.hyc.backend.packet.Packet;
import com.hyc.backend.packet.TCPPacket;

/**
 * @author kol Huang
 * @date 2021/3/30
 */
public class HttpPacketFilter implements IPacketFilter {
    @Override
    public boolean filter(Packet packet) {
        if(packet == null)  return false;
        if(packet instanceof TCPPacket){
            TCPPacket tcpPacket = (TCPPacket) packet;
            return tcpPacket.getSrcPort() != 80 && tcpPacket.getDstPort() != 80;
        }
        return true;
    }
}
