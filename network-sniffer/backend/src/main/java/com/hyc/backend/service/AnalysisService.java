package com.hyc.backend.service;

import com.hyc.backend.analysis.IAnalysisRealm;
import com.hyc.backend.analysis.arp.ArpAnalysisRealm;
import com.hyc.backend.analysis.arp.ArpPacketFilter;
import com.hyc.backend.analysis.http.EncryptedHttpsAnalysisRealm;
import com.hyc.backend.analysis.http.HttpAnalysisRealm;
import com.hyc.backend.analysis.http.HttpPacketFilter;
import com.hyc.backend.analysis.http.HttpsPacketFilter;
import com.hyc.backend.analysis.icmp.ICMPPacketFilter;
import com.hyc.backend.analysis.icmp.IcmpAnalysisRealm;
import com.hyc.backend.analysis.tcp.TcpAnalysisRealm;
import com.hyc.backend.analysis.tcp.TcpPacketFilter;
import com.hyc.backend.analysis.udp.UdpAnalysisRealm;
import com.hyc.backend.analysis.udp.UdpPacketFilter;
import com.hyc.backend.dao.RedisMapper;
import com.hyc.backend.packet.*;
import com.hyc.backend.pojo.*;
import com.hyc.backend.redis.AnalysisKey;
import com.hyc.backend.redis.AttackKey;
import com.hyc.backend.redis.BasePrefix;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**数据包分析服务
 * @author kol Huang
 * @date 2021/3/29
 */
@Service
public class AnalysisService {

    @Resource
    @Qualifier("redisMapper")
    private RedisMapper redisMapper;

    private boolean analyzing = false;

    public void cleanByBatchId(Integer batchId){
        redisMapper.delList(AnalysisKey.analyzedHttpPackets, "list");
    }

    public long analysisByBatchId(Integer batchId){
        analyzing = true;
        long size = 0;
        size += analysisHttp(batchId);
        size += analysisHttps(batchId);
        size += analysisTCP(batchId);

        return size;
    }

    public boolean isAnalyzing(){
        return analyzing;

    }

    private long analysisHttp(Integer batchId){
        this.analyzing = true;
        List<TCPCapturedPacket> fromList = (List<TCPCapturedPacket>) redisMapper.getFromList(AttackKey.cap_packet, "batch_id" + batchId + "_TCP_list", TCPCapturedPacket.class);
        HttpPacketFilter httpPacketFilter = new HttpPacketFilter();
        Map<Long, IAnalysisRealm> analysisRealmMap = new HashMap<>();
        long size = 0;
        for (TCPCapturedPacket tcpCapturedPacket : fromList) {
            TCPPacket tcpPacket = tcpCapturedPacket.getPacket();
            //根据端口号判断是否是HTTP协议
            if(!httpPacketFilter.filter(tcpPacket)){
                size++;
                if(tcpPacket.getData() != null && tcpPacket.getData().length > 0){
                    //根据TCP包的ACK号作为Key
                    if(!analysisRealmMap.containsKey(tcpPacket.getAckNum())){
                        HttpAnalysisRealm httpAnalysisRealm = new HttpAnalysisRealm();
                        httpAnalysisRealm.initPacket(tcpCapturedPacket.getBatchId(), tcpCapturedPacket.getId(), tcpCapturedPacket.isUpStream(), tcpPacket);
                        analysisRealmMap.put(tcpPacket.getAckNum(), httpAnalysisRealm);
                    }else{
                        analysisRealmMap.get(tcpPacket.getAckNum()).appendPacket(tcpCapturedPacket.getId(),tcpPacket);
                    }
                }
            }
        }

        saveRealmPackets(analysisRealmMap, batchId, new AnalyzedHttpPacket());
        this.analyzing = false;
        return size;
    }
    private long analysisHttps(Integer batchId){
        this.analyzing = true;
        Map<Long, IAnalysisRealm> analysisRealmMap = new HashMap<>();
        HttpsPacketFilter httpsPacketFilter = new HttpsPacketFilter();
        List<TCPCapturedPacket> fromList = (List<TCPCapturedPacket>) redisMapper.getFromList(AttackKey.cap_packet, "batch_id" + batchId + "_TCP_list", TCPCapturedPacket.class);
        long size = 0;
        for (TCPCapturedPacket tcpCapturedPacket : fromList) {
            TCPPacket tcpPacket = tcpCapturedPacket.getPacket();
            //根据端口号判断是否是HTTP协议
            if(!httpsPacketFilter.filter(tcpPacket)){
                size++;
                if(tcpPacket.getData() != null && tcpPacket.getData().length > 0){
                    //根据TCP包的ACK号作为Key
                    if(!analysisRealmMap.containsKey(tcpPacket.getAckNum())){
                        EncryptedHttpsAnalysisRealm httpsAnalysisRealm = new EncryptedHttpsAnalysisRealm();
                        httpsAnalysisRealm.initPacket(tcpCapturedPacket.getBatchId(), tcpCapturedPacket.getId(), tcpCapturedPacket.isUpStream(), tcpPacket);
                        analysisRealmMap.put(tcpPacket.getAckNum(), httpsAnalysisRealm);
                    }else{
                        analysisRealmMap.get(tcpPacket.getAckNum()).appendPacket(tcpCapturedPacket.getId(),tcpPacket);
                    }
                }
            }
        }

        this.saveRealmPackets(analysisRealmMap, batchId, new AnalyzedHttpsPacket());
        this.analyzing = false;
        return size;
    }

    private long analysisTCP(Integer batchId){
        this.analyzing = true;
        Map<Long, IAnalysisRealm> analysisRealmMap = new HashMap<>();
        TcpPacketFilter tcpPacketFilter = new TcpPacketFilter();
        List<TCPCapturedPacket> fromList = (List<TCPCapturedPacket>) redisMapper.getFromList(AttackKey.cap_packet, "batch_id" + batchId + "_TCP_list", TCPCapturedPacket.class);
        long size = 0;
        for (TCPCapturedPacket tcpCapturedPacket : fromList) {
            TCPPacket tcpPacket = tcpCapturedPacket.getPacket();
            //根据端口号判断是否是HTTP协议
            if(!tcpPacketFilter.filter(tcpPacket)){
                size++;
                if(tcpPacket.getData() != null && tcpPacket.getData().length > 0){
                    //根据TCP包的ACK号作为Key
                    if(!analysisRealmMap.containsKey(tcpPacket.getAckNum())){
                        TcpAnalysisRealm httpsAnalysisRealm = new TcpAnalysisRealm();
                        httpsAnalysisRealm.initPacket(tcpCapturedPacket.getBatchId(), tcpCapturedPacket.getId(), tcpCapturedPacket.isUpStream(), tcpPacket);
                        analysisRealmMap.put(tcpPacket.getAckNum(), httpsAnalysisRealm);
                    }else{
                        analysisRealmMap.get(tcpPacket.getAckNum()).appendPacket(tcpCapturedPacket.getId(),tcpPacket);
                    }
                }
            }
        }
        this.saveRealmPackets(analysisRealmMap, batchId, new AnalyzedArpPacket());
        this.analyzing = false;
        return size;
    }

    private long analysisUDP(Integer batchId){
        this.analyzing = true;
        UdpPacketFilter udpPacketFilter = new UdpPacketFilter();
        long size = 0;
        List<CapturedUDPPacket> fromList = (List<CapturedUDPPacket>)redisMapper.getFromList(AttackKey.cap_packet, "batch_id" + batchId + "_UDP_list", CapturedUDPPacket.class);

        for (CapturedUDPPacket capturedUDPPacket : fromList) {
            UDPPacket packet = capturedUDPPacket.getPacket();
            if(!udpPacketFilter.filter(packet)){
                UdpAnalysisRealm udpAnalysisRealm = new UdpAnalysisRealm();
                udpAnalysisRealm.initPacket(capturedUDPPacket.getBatchId(), capturedUDPPacket.getId(),capturedUDPPacket.isUpStream(),packet);
                Serializable toSavePacket = udpAnalysisRealm.makePacket4Save();
                if(toSavePacket != null){
                    redisMapper.addToList(AnalysisKey.analyzedUDPPackets, "batchid_"+batchId+"list", toSavePacket);
                }
                size++;
            }
        }
        this.analyzing = false;
        return size;
    }
    private long analysisICMP(Integer batchId){
        this.analyzing = true;
        ICMPPacketFilter icmpPacketFilter = new ICMPPacketFilter();
        long size = 0;
        List<CapturedICMPPacket> fromList = (List<CapturedICMPPacket>)redisMapper.getFromList(AttackKey.cap_packet, "batch_id" + batchId + "_ICMP_list", CapturedICMPPacket.class);

        for (CapturedICMPPacket capturedICMPPacket : fromList) {
            ICMPPacket packet = capturedICMPPacket.getPacket();
            if(!icmpPacketFilter.filter(packet)){
                size++;
                IcmpAnalysisRealm icmpAnalysisRealm = new IcmpAnalysisRealm();
                icmpAnalysisRealm.initPacket(capturedICMPPacket.getBatchId(), capturedICMPPacket.getId(),capturedICMPPacket.isUpStream(),packet);
                Serializable toSavePacket = icmpAnalysisRealm.makePacket4Save();
                if(toSavePacket != null){
                    redisMapper.addToList(AnalysisKey.analyzedICMPPackets, "batchid_"+batchId+"list", toSavePacket);
                }
            }
        }
        this.analyzing = false;
        return size;
    }
//
    private long analysisArp(Integer batchId){
        this.analyzing = true;
        ArpPacketFilter arpPacketFilter = new ArpPacketFilter();
        long size = 0;
        List<CapturedARPPacket> fromList = (List<CapturedARPPacket>)redisMapper.getFromList(AttackKey.cap_packet, "batch_id" + batchId + "_ARP_list", CapturedARPPacket.class);
        for (CapturedARPPacket capturedARPPacket : fromList) {
            ARPPacket packet = capturedARPPacket.getPacket();
            if(!arpPacketFilter.filter(packet)){
                size++;
                ArpAnalysisRealm arpAnalysisRealm = new ArpAnalysisRealm();
                arpAnalysisRealm.initPacket(capturedARPPacket.getBatchId(), capturedARPPacket.getId(), capturedARPPacket.isUpStream(),packet);
                Serializable toSavePacket = arpAnalysisRealm.makePacket4Save();
                if(toSavePacket != null){
                    redisMapper.addToList(AnalysisKey.analyzedARPPackets, "batchid_"+batchId+"list", toSavePacket);
                }
            }
        }
        analyzing = false;
        return size;
    }

//    private long analysisArp(List<CapturedPacket> capturedPackets){
//
//    }


    private void saveRealmPackets(Map<Long, IAnalysisRealm> analysisRealmMap, Integer batchId, AbsAnalyzedPacket packet){
        for (Map.Entry<Long, IAnalysisRealm> entry : analysisRealmMap.entrySet()) {
            IAnalysisRealm value = entry.getValue();
            AbsAnalyzedPacket toSavePacket = value.makePacket4Save();
            if (toSavePacket != null) {
                BasePrefix prefix = null;
                if(packet instanceof AnalyzedTcpPacket){
                    prefix = AnalysisKey.analyzedTCPPackets;
                }

                if(packet instanceof AnalyzedHttpPacket){
                    prefix = AnalysisKey.analyzedHttpPackets;
                }
                if(packet instanceof AnalyzedHttpsPacket){
                    prefix = AnalysisKey.analyzedHttpsPackets;
                }

                redisMapper.addToList(prefix, "batchid_"+batchId+"list", toSavePacket);

            }
        }
    }
}
