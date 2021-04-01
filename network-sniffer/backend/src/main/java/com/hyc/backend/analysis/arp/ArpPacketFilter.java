/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.hyc.backend.analysis.arp;

import com.hyc.backend.analysis.IPacketFilter;
import com.hyc.backend.packet.ARPPacket;
import com.hyc.backend.packet.Packet;

/**
 * 过滤出ARP包
 *
 * @author Frankie
 */
public class ArpPacketFilter implements IPacketFilter {
    @Override
    public boolean filter(Packet packet) {
        if (packet instanceof ARPPacket) {
            return false;
        }
        return true;
    }
}
