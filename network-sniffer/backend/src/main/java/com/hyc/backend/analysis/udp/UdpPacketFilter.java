/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.hyc.backend.analysis.udp;

import com.hyc.backend.analysis.IPacketFilter;
import com.hyc.backend.packet.Packet;
import com.hyc.backend.packet.UDPPacket;

/**
 * 过滤出UDP包
 *
 * @author Frankie
 */
public class UdpPacketFilter implements IPacketFilter {
    @Override
    public boolean filter(Packet packet) {
        if (packet instanceof UDPPacket) {
            return false;
        }
        return true;
    }
}
