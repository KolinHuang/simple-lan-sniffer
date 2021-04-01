package com.hyc.backend.analysis;

import com.hyc.backend.packet.Packet;

/**
 * @author kol Huang
 * @date 2021/3/30
 */
public interface IPacketFilter {

    boolean filter(Packet packet);

}
