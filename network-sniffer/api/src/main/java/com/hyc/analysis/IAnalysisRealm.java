package com.hyc.analysis;

import com.hyc.packet.AbsAnalyzedPacket;
import com.hyc.packet.Packet;

/**
 * @author kol Huang
 * @date 2021/3/30
 */
public interface IAnalysisRealm {
    String protocol();

    void initPacket(Integer batchId, boolean upstream, Packet packet);

    void appendPacket(Packet packet);

    AbsAnalyzedPacket makePacket4Save();
}
