package com.hyc.packet;

import java.io.Serializable;

/**
 * 数据链路层以上的所有类型数据包的基类
 * @author kol Huang
 * @date 2021/3/19
 */
public class Packet implements Serializable {
    private long sec;

    private long usec;

    private int capLen;
    //数据包长度
    private int len;
    //以太网数据帧的引用
    public EthernetPacket ethernetPacket;
    //头部字段
    private byte[] header;
    ////数据字段
    private byte[] data;


    public Packet() {
    }

    public Packet(jpcap.packet.Packet packet) {
        this.sec = packet.sec;
        this.usec = packet.usec;
        this.capLen = packet.caplen;
        this.len = packet.len;
        this.ethernetPacket = new EthernetPacket(packet.datalink);
        this.header = packet.header;
        this.data = packet.data;
    }

    public long getSec() {
        return sec;
    }

    public void setSec(long sec) {
        this.sec = sec;
    }

    public long getUsec() {
        return usec;
    }

    public void setUsec(long usec) {
        this.usec = usec;
    }

    public int getCapLen() {
        return capLen;
    }

    public void setCapLen(int capLen) {
        this.capLen = capLen;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public EthernetPacket getEthernetPacket() {
        return ethernetPacket;
    }

    public void setEthernetPacket(EthernetPacket ethernetPacket) {
        this.ethernetPacket = ethernetPacket;
    }

    public byte[] getHeader() {
        return header;
    }

    public void setHeader(byte[] header) {
        this.header = header;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
