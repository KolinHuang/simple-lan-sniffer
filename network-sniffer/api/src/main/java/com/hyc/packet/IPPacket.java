package com.hyc.packet;

import java.net.InetAddress;
import java.util.List;

/**
 * @author kol Huang
 * @date 2021/3/19
 */
public class IPPacket extends Packet {
    //IP协议版本号
    private byte version;
    //服务类型字段，占8位，从未被使用过
    //优先级：3比特
    private byte priority;

    private boolean dFlag;

    private boolean tFlag;

    private boolean rFlag;
    //保留字段
    private byte rsvTos;
    //总长度，占16位
    private short length;
    //标志字段：保留位
    private boolean rsvFrag;
    //标志字段：DF = 1代表不能分片
    private boolean dontFrag;
    //标志字段：MF = 1代表后面还有分片
    private boolean moreFrag;
    //片偏移
    private short offset;
    //生存时间TTL
    private short hopLimit;
    //协议：标识数据报的上层协议
    private short protocol;
    //首部检验和：占16位，只检查数据报的首部
    private int ident;
    //流标签：IPv6的新增字段，用于某些对连接的服务质量有特殊要求的通信,比如音频或视频等实时数据传输。
    private int flowLabel;
    //源IP地址
    private InetAddress srcIP;
    //目地IP地址
    private InetAddress dstIP;
    //可变字段
    private byte[] option;
    private List options;

    public IPPacket() {
    }

    public IPPacket(jpcap.packet.IPPacket packet) {
        super(packet);

        this.version = packet.version;
        this.priority = packet.priority;
        this.dFlag = packet.d_flag;
        this.tFlag = packet.t_flag;
        this.rFlag = packet.r_flag;
        this.rsvTos = packet.rsv_tos;
        this.length = packet.length;
        this.rsvFrag = packet.rsv_frag;
        this.dontFrag = packet.dont_frag;
        this.moreFrag = packet.more_frag;
        this.offset = packet.offset;
        this.hopLimit = packet.hop_limit;
        this.protocol = packet.protocol;
        this.ident = packet.ident;
        this.flowLabel = packet.flow_label;
        this.srcIP = packet.src_ip;
        this.dstIP = packet.dst_ip;
        this.option = packet.option;
        this.options = packet.options;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public boolean isdFlag() {
        return dFlag;
    }

    public void setdFlag(boolean dFlag) {
        this.dFlag = dFlag;
    }

    public boolean istFlag() {
        return tFlag;
    }

    public void settFlag(boolean tFlag) {
        this.tFlag = tFlag;
    }

    public boolean isrFlag() {
        return rFlag;
    }

    public void setrFlag(boolean rFlag) {
        this.rFlag = rFlag;
    }

    public byte getRsvTos() {
        return rsvTos;
    }

    public void setRsvTos(byte rsvTos) {
        this.rsvTos = rsvTos;
    }

    public short getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public boolean isRsvFrag() {
        return rsvFrag;
    }

    public void setRsvFrag(boolean rsvFrag) {
        this.rsvFrag = rsvFrag;
    }

    public boolean isDontFrag() {
        return dontFrag;
    }

    public void setDontFrag(boolean dontFrag) {
        this.dontFrag = dontFrag;
    }

    public boolean isMoreFrag() {
        return moreFrag;
    }

    public void setMoreFrag(boolean moreFrag) {
        this.moreFrag = moreFrag;
    }

    public short getOffset() {
        return offset;
    }

    public void setOffset(short offset) {
        this.offset = offset;
    }

    public short getHopLimit() {
        return hopLimit;
    }

    public void setHopLimit(short hopLimit) {
        this.hopLimit = hopLimit;
    }

    public short getProtocol() {
        return protocol;
    }

    public void setProtocol(short protocol) {
        this.protocol = protocol;
    }

    public int getIdent() {
        return ident;
    }

    public void setIdent(int ident) {
        this.ident = ident;
    }

    public int getFlowLabel() {
        return flowLabel;
    }

    public void setFlowLabel(int flowLabel) {
        this.flowLabel = flowLabel;
    }

    public InetAddress getSrcIP() {
        return srcIP;
    }

    public void setSrcIP(InetAddress srcIP) {
        this.srcIP = srcIP;
    }

    public InetAddress getDstIP() {
        return dstIP;
    }

    public void setDstIP(InetAddress dstIP) {
        this.dstIP = dstIP;
    }

    public byte[] getOption() {
        return option;
    }

    public void setOption(byte[] option) {
        this.option = option;
    }

    public List getOptions() {
        return options;
    }

    public void setOptions(List options) {
        this.options = options;
    }
}
