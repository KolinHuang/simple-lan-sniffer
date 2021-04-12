package com.hyc.packet;

import jpcap.packet.DatalinkPacket;

import java.io.Serializable;

/**Ethernet II 帧，DIX帧
 * @author kol Huang
 * @date 2021/3/19
 */
public class EthernetPacket implements Serializable {
    //目地MAC地址
    private byte[] dstMac;
    //源MAC地址
    private byte[] srcMac;
    /*
    * 以太网帧数据类型字段：2 bytes
    *   0x0800 -> IP
    *   0x0806 -> ARP
    *   0xffcb-> REVARP
    *   0x8100 -> IEEE 802.1Q
    *   0x9100 -> VLAN
    *   0x86DD -> IPV6
    *   0x8864 -> PPPoE
    *   0x9000 -> LOOPBACK
    * */
    private short frameType;
    //帧类型定义
    private static final short ETHERTYPE_PUP = 512;//PARC Universal Packet 帕洛阿尔托研究中心通用包的协议
    private static final short ETHERTYPE_IP = 2048;
    private static final short ETHERTYPE_ARP = 2054;
    private static final short ETHERTYPE_REVARP = -32715;
    private static final short ETHERTYPE_VLAN = -32512;
    private static final short ETHERTYPE_IPV6 = -31011;
    private static final short ETHERTYPE_LOOPBACK = -28672;

    public EthernetPacket() {
    }

    public EthernetPacket(DatalinkPacket packet) {
        if(packet == null){
            throw new NullPointerException(" An empty packet!");
        }
        //只处理以太网帧
        if(packet instanceof jpcap.packet.EthernetPacket){
            jpcap.packet.EthernetPacket ethernetPacket = (jpcap.packet.EthernetPacket) packet;
            this.dstMac = ethernetPacket.dst_mac;
            this.srcMac = ethernetPacket.src_mac;
            this.frameType = ethernetPacket.frametype;
        }else{
            throw new IllegalArgumentException(" Could not execute other Data link layer packets except ethernet packets");
        }
    }


    /**
     * 将源MAC地址以字符串形式返回
     * 由于src_mac字节数组中存储的是十进制，因此我们需要将其翻译成十六进制
     * @return
     */
    public String readSourceAddress(){

        StringBuffer sb = new StringBuffer();
        for(int i = 0; i <= 5; ++i){
            sb.append(hexUpperChar(srcMac[i]));
            sb.append(hexLowerChar(srcMac[i]));
            if(i != 5){
                sb.append(':');
            }
        }
        return sb.toString();
    }

    /**
     * 将目地MAC地址以字符串形式返回
     * 由于dst_mac字节数组中存储的是十进制，因此我们需要将其翻译成十六进制
     * @return
     */
    public String readDestinationAddress(){

        StringBuffer sb = new StringBuffer();
        for(int i = 0; i <= 5; ++i){
            sb.append(hexUpperChar(dstMac[i]));
            sb.append(hexLowerChar(dstMac[i]));
            if(i != 5){
                sb.append(':');
            }
        }
        return sb.toString();
    }



    /**
     * 取高4位，转字符
     */
    private char hexUpperChar(byte b) {
        b = (byte) (b >> 4 & 15);
        if (b == 0) {
            return '0';
        } else {
            return b < 10 ? (char) (48 + b) : (char) (97 + b - 10);
        }
    }

    /**
     * 取低4位，转字符
     * @param b
     * @return
     */
    private char hexLowerChar(byte b) {
        b = (byte) (b & 15);
        if (b == 0) {
            return '0';
        } else {
            return b < 10 ? (char) (48 + b) : (char) (97 + b - 10);
        }
    }

    public byte[] getDstMac() {
        return dstMac;
    }

    public void setDstMac(byte[] dstMac) {
        this.dstMac = dstMac;
    }

    public byte[] getSrcMac() {
        return srcMac;
    }

    public void setSrcMac(byte[] srcMac) {
        this.srcMac = srcMac;
    }

    public short getFrameType() {
        return frameType;
    }

    public void setFrameType(short frameType) {
        this.frameType = frameType;
    }


}
