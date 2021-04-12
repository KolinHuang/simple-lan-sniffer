package com.hyc.pojo;

import com.hyc.packet.TCPPacket;

/**
 * @author kol Huang
 * @date 2021/4/1
 */
public class CapturedTCPPacket extends AbsCapturedPacket{

    private TCPPacket packet;

    public TCPPacket getPacket() {
        return packet;
    }

    public void setPacket(TCPPacket packet) {
        this.packet = packet;
    }
}
