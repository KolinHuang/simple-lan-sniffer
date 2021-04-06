package com.hyc.backend.analysis;

import com.hyc.backend.packet.AbsAnalyzedPacket;
import com.hyc.backend.packet.Packet;

import java.io.UnsupportedEncodingException;

/**
 * @author kol Huang
 * @date 2021/3/30
 */
public interface IAnalysisRealm {
    String protocol();

    void initPacket(Integer batchId, boolean upstream, Packet packet);

    void appendPacket( Packet packet);

    AbsAnalyzedPacket makePacket4Save();
}
