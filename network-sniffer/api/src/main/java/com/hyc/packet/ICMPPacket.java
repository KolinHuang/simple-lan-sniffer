package com.hyc.packet;

import java.net.InetAddress;

/**
 * @author kol Huang
 * @date 2021/3/22
 */
public class ICMPPacket extends IPPacket {
    private static final long serialVersionUID = 208303875185246732L;
    private static final short ICMP_ECHOREPLY = 0;
    private static final short ICMP_UNREACH = 3;
    private static final short ICMP_UNREACH_NET = 0;
    private static final short ICMP_UNREACH_HOST = 1;
    private static final short ICMP_UNREACH_PROTOCOL = 2;
    private static final short ICMP_UNREACH_PORT = 3;
    private static final short ICMP_UNREACH_NEEDFRAG = 4;
    private static final short ICMP_UNREACH_SRCFAIL = 5;
    private static final short ICMP_UNREACH_NET_UNKNOWN = 6;
    private static final short ICMP_UNREACH_HOST_UNKNOWN = 7;
    private static final short ICMP_UNREACH_ISOLATED = 8;
    private static final short ICMP_UNREACH_NET_PROHIB = 9;
    private static final short ICMP_UNREACH_HOST_PROHIB = 10;
    private static final short ICMP_UNREACH_TOSNET = 11;
    private static final short ICMP_UNREACH_TOSHOST = 12;
    private static final short ICMP_UNREACH_FILTER_PROHIB = 13;
    private static final short ICMP_UNREACH_HOST_PRECEDENCE = 14;
    private static final short ICMP_UNREACH_PRECEDENCE_CUTOFF = 15;
    private static final short ICMP_SOURCEQUENCH = 4;
    private static final short ICMP_REDIRECT = 5;
    private static final short ICMP_REDIRECT_NET = 0;
    private static final short ICMP_REDIRECT_HOST = 1;
    private static final short ICMP_REDIRECT_TOSNET = 2;
    private static final short ICMP_REDIRECT_TOSHOST = 3;
    private static final short ICMP_ECHO = 8;
    private static final short ICMP_ROUTERADVERT = 9;
    private static final short ICMP_ROUTERSOLICIT = 10;
    private static final short ICMP_TIMXCEED = 11;
    private static final short ICMP_TIMXCEED_INTRANS = 0;
    private static final short ICMP_TIMXCEED_REASS = 1;
    private static final short ICMP_PARAMPROB = 12;
    private static final short ICMP_PARAMPROB_ERRATPTR = 0;
    private static final short ICMP_PARAMPROB_OPTABSENT = 1;
    private static final short ICMP_PARAMPROB_LENGTH = 2;
    private static final short ICMP_TSTAMP = 13;
    private static final short ICMP_TSTAMPREPLY = 14;
    private static final short ICMP_IREQ = 15;
    private static final short ICMP_IREQREPLY = 16;
    private static final short ICMP_MASKREQ = 17;
    private static final short ICMP_MASKREPLY = 18;

    private byte type;

    private byte code;

    private short checksum;

    private short id;

    private short seq;

    private int subNetmask;

    private int origTimestamp;

    private int recvTimestamp;

    private int transTimestamp;

    private short mtu;

    private jpcap.packet.IPPacket ipPacket;

    private InetAddress redirIP;

    private byte addrNum;

    private byte addrEntrySize;

    private short aliveTime;

    private InetAddress[] routerIP;

    private int[] preference;

    public ICMPPacket() {
    }

    public ICMPPacket(jpcap.packet.ICMPPacket packet) {
        super(packet);

        this.type = packet.type;
        this.code = packet.code;
        this.checksum = packet.checksum;
        this.id = packet.id;
        this.seq = packet.seq;
        this.subNetmask = packet.subnetmask;
        this.origTimestamp = packet.orig_timestamp;
        this.recvTimestamp = packet.recv_timestamp;
        this.transTimestamp = packet.trans_timestamp;
        this.mtu = packet.mtu;
        this.ipPacket = packet.ippacket;
        this.redirIP = packet.redir_ip;
        this.addrNum = packet.addr_num;
        this.addrEntrySize = packet.addr_entry_size;
        this.aliveTime = packet.alive_time;
        this.routerIP = packet.router_ip;
        this.preference = packet.preference;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getCode() {
        return code;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    public short getChecksum() {
        return checksum;
    }

    public void setChecksum(short checksum) {
        this.checksum = checksum;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public short getSeq() {
        return seq;
    }

    public void setSeq(short seq) {
        this.seq = seq;
    }

    public int getSubNetmask() {
        return subNetmask;
    }

    public void setSubNetmask(int subNetmask) {
        this.subNetmask = subNetmask;
    }

    public int getOrigTimestamp() {
        return origTimestamp;
    }

    public void setOrigTimestamp(int origTimestamp) {
        this.origTimestamp = origTimestamp;
    }

    public int getRecvTimestamp() {
        return recvTimestamp;
    }

    public void setRecvTimestamp(int recvTimestamp) {
        this.recvTimestamp = recvTimestamp;
    }

    public int getTransTimestamp() {
        return transTimestamp;
    }

    public void setTransTimestamp(int transTimestamp) {
        this.transTimestamp = transTimestamp;
    }

    public short getMtu() {
        return mtu;
    }

    public void setMtu(short mtu) {
        this.mtu = mtu;
    }

    public jpcap.packet.IPPacket getIpPacket() {
        return ipPacket;
    }

    public void setIpPacket(jpcap.packet.IPPacket ipPacket) {
        this.ipPacket = ipPacket;
    }

    public InetAddress getRedirIP() {
        return redirIP;
    }

    public void setRedirIP(InetAddress redirIP) {
        this.redirIP = redirIP;
    }

    public byte getAddrNum() {
        return addrNum;
    }

    public void setAddrNum(byte addrNum) {
        this.addrNum = addrNum;
    }

    public byte getAddrEntrySize() {
        return addrEntrySize;
    }

    public void setAddrEntrySize(byte addrEntrySize) {
        this.addrEntrySize = addrEntrySize;
    }

    public short getAliveTime() {
        return aliveTime;
    }

    public void setAliveTime(short aliveTime) {
        this.aliveTime = aliveTime;
    }

    public InetAddress[] getRouterIP() {
        return routerIP;
    }

    public void setRouterIP(InetAddress[] routerIP) {
        this.routerIP = routerIP;
    }

    public int[] getPreference() {
        return preference;
    }

    public void setPreference(int[] preference) {
        this.preference = preference;
    }
}
