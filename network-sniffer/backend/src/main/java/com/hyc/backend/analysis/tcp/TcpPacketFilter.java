/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.hyc.backend.analysis.tcp;

import com.hyc.backend.analysis.IPacketFilter;
import com.hyc.backend.packet.Packet;
import com.hyc.backend.packet.TCPPacket;

/**
 * 过滤出非HTTP/HTTPS的TCP包
 */
public class TcpPacketFilter implements IPacketFilter {
    @Override
    public boolean filter(Packet packet) {
        //分析TCP协议
        if (packet instanceof TCPPacket) {
            TCPPacket tcpPacketModel = (TCPPacket) packet;
            if (tcpPacketModel.getSrcPort() != 80 &&
                    tcpPacketModel.getDstPort() != 80 &&
                    tcpPacketModel.getSrcPort() != 443 &&
                    tcpPacketModel.getDstPort() != 443) {
                return false;
            }
        }
        return true;
    }
}
