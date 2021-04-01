package com.hyc.backend.pojo;

import com.hyc.backend.packet.ICMPPacket;

/**
 * @author kol Huang
 * @date 2021/4/1
 */
public class CapturedICMPPacket extends AbsCapturedPacket{

    private ICMPPacket packet;

    public ICMPPacket getPacket() {
        return packet;
    }

    public void setPacket(ICMPPacket packet) {
        this.packet = packet;
    }
}
