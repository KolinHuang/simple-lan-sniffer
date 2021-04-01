package com.hyc.backend.analysis;

import com.hyc.backend.packet.AbsAnalyzedPacket;
import com.hyc.backend.packet.Packet;

/**
 * @author kol Huang
 * @date 2021/3/30
 */
public interface IAnalysisRealm {
    String protocol();

    void initPacket(Integer batchId, String capturePacketId, boolean upstream, Packet packet);

    void appendPacket(String capturePacketId, Packet packet);

    AbsAnalyzedPacket makePacket4Save();
}
