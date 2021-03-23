package com.hyc.backend.packet;

/**
 * @author kol Huang
 * @date 2021/3/22
 */
public class ARPPacket extends Packet{

    private short hardType;
    private static final short HARDTYPE_ETHER = 1;
    private static final short HARDTYPE_IEEE802 = 6;
    private static final short HARDTYPE_FRAMERELAY = 15;

    private short prototype;

    private static final short PROTOTYPE_IP = 2048;

    private short hLen;

    private short pLen;

    private short operation;

    private static final short ARP_REQUEST = 1;
    private static final short ARP_REPLY = 2;
    private static final short RARP_REQUEST = 3;
    private static final short RARP_REPLY = 4;
    private static final short INV_REQUEST = 8;
    private static final short INV_REPLY = 9;

    private byte[] senderHardAddr;

    private byte[] senderProtoAddr;

    private byte[] targetHardAddr;

    private byte[] targetProtoAddr;

    public ARPPacket(jpcap.packet.ARPPacket packet) {
        super(packet);
        this.hardType = packet.hardtype;
        this.prototype = packet.prototype;
        this.hLen = packet.hlen;
        this.pLen = packet.plen;
        this.operation = packet.operation;
        this.senderHardAddr = packet.sender_hardaddr;
        this.senderProtoAddr = packet.sender_protoaddr;
        this.targetHardAddr = packet.target_hardaddr;
        this.targetProtoAddr = packet.target_protoaddr;
    }

    public short getHardType() {
        return hardType;
    }

    public void setHardType(short hardType) {
        this.hardType = hardType;
    }

    public short getPrototype() {
        return prototype;
    }

    public void setPrototype(short prototype) {
        this.prototype = prototype;
    }

    public short gethLen() {
        return hLen;
    }

    public void sethLen(short hLen) {
        this.hLen = hLen;
    }

    public short getpLen() {
        return pLen;
    }

    public void setpLen(short pLen) {
        this.pLen = pLen;
    }

    public short getOperation() {
        return operation;
    }

    public void setOperation(short operation) {
        this.operation = operation;
    }

    public byte[] getSenderHardAddr() {
        return senderHardAddr;
    }

    public void setSenderHardAddr(byte[] senderHardAddr) {
        this.senderHardAddr = senderHardAddr;
    }

    public byte[] getSenderProtoAddr() {
        return senderProtoAddr;
    }

    public void setSenderProtoAddr(byte[] senderProtoAddr) {
        this.senderProtoAddr = senderProtoAddr;
    }

    public byte[] getTargetHardAddr() {
        return targetHardAddr;
    }

    public void setTargetHardAddr(byte[] targetHardAddr) {
        this.targetHardAddr = targetHardAddr;
    }

    public byte[] getTargetProtoAddr() {
        return targetProtoAddr;
    }

    public void setTargetProtoAddr(byte[] targetProtoAddr) {
        this.targetProtoAddr = targetProtoAddr;
    }
}
