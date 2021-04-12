
package com.hyc.analysis.icmp;

import com.hyc.analysis.IPacketFilter;
import com.hyc.packet.ICMPPacket;
import com.hyc.packet.Packet;


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
