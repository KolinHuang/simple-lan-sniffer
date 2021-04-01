
package com.hyc.backend.analysis.icmp;

import com.hyc.backend.analysis.IPacketFilter;
import com.hyc.backend.packet.ICMPPacket;
import com.hyc.backend.packet.Packet;


/**
 * 过滤出ICMP包
 *
 * @author Frankie
 */
public class ICMPPacketFilter implements IPacketFilter {
    @Override
    public boolean filter(Packet packet) {
        if (packet instanceof ICMPPacket) {
            return false;
        }
        return true;
    }
}
