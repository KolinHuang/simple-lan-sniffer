package com.hyc.pojo;

import com.hyc.packet.ARPPacket;

/**
 * @author kol Huang
 * @date 2021/4/1
 */
public class CapturedARPPacket extends AbsCapturedPacket{
    private ARPPacket packet;

    @Override
    public ARPPacket getPacket() {
        return packet;
    }

    public void setPacket(ARPPacket packet) {
        this.packet = packet;
    }
}
