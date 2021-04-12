/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.hyc.analysis.arp;

import com.hyc.analysis.IPacketFilter;
import com.hyc.packet.ARPPacket;
import com.hyc.packet.Packet;

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
