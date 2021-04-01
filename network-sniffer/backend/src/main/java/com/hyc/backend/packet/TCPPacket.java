package com.hyc.backend.packet;

import jpcap.packet.DatalinkPacket;

/**
 * @author kol Huang
 * @date 2021/3/22
 */
public class TCPPacket extends IPPacket{
    //源端口号
    private int srcPort;
    //目的端口号
    private int dstPort;
    //序号：指派给本报文段第一个数据字节的序号
    private long sequence;
    //确认号：接收方期望从对方接收的字节编号
    private long ackNum;
    //为true：紧急脂针有效
    private boolean urg;
    //为true：确认号有效
    private boolean ack;
    //为true：请求推送
    private boolean psh;
    //为true：连接复位
    private boolean rst;
    //为true：同步序号
    private boolean syn;
    //为true：终止连接
    private boolean fin;
    //保留字段
    private boolean rsv1;
    //保留字段
    private boolean rsv2;
    //接收窗口大小
    private int window;
    //紧急指针
    private short urgentPointer;
    //可选：40字节
    private byte[] option;

    public TCPPacket() {
    }

    public TCPPacket(jpcap.packet.TCPPacket packet) {

        TCPPacket model = new TCPPacket();
        model.setSec(packet.sec);
        model.setUsec(packet.usec);
        model.setCapLen(packet.caplen);
        model.setLen(packet.len);
        model.setHeader(packet.header);
        model.setData(packet.data);
        model.setEthernetPacket(new EthernetPacket(packet.datalink));

        model.setVersion(packet.version);
        model.setPriority(packet.priority);
        model.setdFlag(packet.d_flag);
        model.settFlag(packet.t_flag);
        model.setrFlag(packet.r_flag);
        model.setRsvTos(packet.rsv_tos);
        model.setLength(packet.length);
        model.setRsvFrag(packet.rsv_frag);
        model.setDontFrag(packet.dont_frag);
        model.setMoreFrag(packet.more_frag);
        model.setOffset(packet.offset);
        model.setHopLimit(packet.hop_limit);
        model.setProtocol(packet.protocol);
        model.setIdent(packet.ident);
        model.setFlowLabel(packet.flow_label);
        model.setSrcIP(packet.src_ip);
        model.setDstIP(packet.dst_ip);
        model.setOption(packet.option);
        model.setOptions(packet.options);

        this.srcPort = packet.src_port;
        this.dstPort = packet.dst_port;
        this.sequence = packet.sequence;
        this.ackNum = packet.ack_num;
        this.urg = packet.urg;
        this.ack = packet.ack;
        this.psh = packet.psh;
        this.rst = packet.rst;
        this.syn = packet.syn;
        this.fin = packet.fin;
        this.rsv1 = packet.rsv1;
        this.rsv2 = packet.rsv2;
        this.window = packet.window;
        this.urgentPointer = packet.urgent_pointer;
        this.option = packet.option;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    public int getDstPort() {
        return dstPort;
    }

    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public long getAckNum() {
        return ackNum;
    }

    public void setAckNum(long ackNum) {
        this.ackNum = ackNum;
    }

    public boolean isUrg() {
        return urg;
    }

    public void setUrg(boolean urg) {
        this.urg = urg;
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

    public boolean isPsh() {
        return psh;
    }

    public void setPsh(boolean psh) {
        this.psh = psh;
    }

    public boolean isRst() {
        return rst;
    }

    public void setRst(boolean rst) {
        this.rst = rst;
    }

    public boolean isSyn() {
        return syn;
    }

    public void setSyn(boolean syn) {
        this.syn = syn;
    }

    public boolean isFin() {
        return fin;
    }

    public void setFin(boolean fin) {
        this.fin = fin;
    }

    public boolean isRsv1() {
        return rsv1;
    }

    public void setRsv1(boolean rsv1) {
        this.rsv1 = rsv1;
    }

    public boolean isRsv2() {
        return rsv2;
    }

    public void setRsv2(boolean rsv2) {
        this.rsv2 = rsv2;
    }

    public int getWindow() {
        return window;
    }

    public void setWindow(int window) {
        this.window = window;
    }

    public short getUrgentPointer() {
        return urgentPointer;
    }

    public void setUrgentPointer(short urgentPointer) {
        this.urgentPointer = urgentPointer;
    }

    @Override
    public byte[] getOption() {
        return option;
    }

    @Override
    public void setOption(byte[] option) {
        this.option = option;
    }


}
