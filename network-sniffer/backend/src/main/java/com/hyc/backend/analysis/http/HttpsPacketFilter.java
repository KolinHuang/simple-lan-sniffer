/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.hyc.backend.analysis.http;

import com.hyc.backend.analysis.IPacketFilter;
import com.hyc.backend.packet.Packet;
import com.hyc.backend.packet.TCPPacket;

/**
 * 过滤出HTTPS包
 *
 * @author Frankie
 */
public class HttpsPacketFilter implements IPacketFilter {

    @Override
    public boolean filter(Packet packet) {
        if(packet instanceof TCPPacket){
            TCPPacket tcpPacket = (TCPPacket) packet;
            if(tcpPacket.getSrcPort() == 443 || tcpPacket.getDstPort() == 443){
                return false;
            }
        }
        return true;
    }
}
