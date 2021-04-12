package com.hyc.pojo;

import com.hyc.packet.UDPPacket;

/**
 * @author kol Huang
 * @date 2021/4/1
 */
public class CapturedUDPPacket extends AbsCapturedPacket {

    private UDPPacket packet;

    @Override
    public UDPPacket getPacket() {
        return packet;
    }

    public void setPacket(UDPPacket packet) {
        this.packet = packet;
    }
}
