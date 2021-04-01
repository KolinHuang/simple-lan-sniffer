package com.hyc.backend.pojo;

import com.hyc.backend.packet.TCPPacket;

/**
 * @author kol Huang
 * @date 2021/4/1
 */
public class TCPCapturedPacket extends AbsCapturedPacket {

    private TCPPacket packet;

    public TCPPacket getPacket() {
        return packet;
    }

    public void setPacket(TCPPacket packet) {
        this.packet = packet;
    }
}
