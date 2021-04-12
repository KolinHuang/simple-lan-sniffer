/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.hyc.analysis.udp;

import com.hyc.analysis.IPacketFilter;
import com.hyc.packet.Packet;
import com.hyc.packet.UDPPacket;

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
