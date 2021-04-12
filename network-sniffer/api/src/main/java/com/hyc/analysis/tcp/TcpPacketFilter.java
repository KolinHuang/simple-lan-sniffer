/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.hyc.analysis.tcp;

import com.hyc.analysis.IPacketFilter;
import com.hyc.packet.Packet;
import com.hyc.packet.TCPPacket;

/**
 * 过滤出非HTTP/HTTPS的TCP包
 */
public class TcpPacketFilter implements IPacketFilter {
    @Override
    public boolean filter(Packet packet) {
        //分析TCP协议
        if (packet instanceof TCPPacket) {
//            TCPPacket tcpPacketModel = (TCPPacket) packet;
            return true;
//            if (tcpPacketModel.getSrcPort() != 80 &&
//                    tcpPacketModel.getDstPort() != 80 &&
//                    tcpPacketModel.getSrcPort() != 443 &&
//                    tcpPacketModel.getDstPort() != 443) {
//                return false;
//            }
        }
        return false;

    }
}
