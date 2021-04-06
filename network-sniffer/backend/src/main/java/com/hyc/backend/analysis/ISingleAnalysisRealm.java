/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.hyc.backend.analysis;


import com.hyc.backend.packet.AbsAnalyzedPacket;
import com.hyc.backend.packet.Packet;

/**
 * @author Frankie
 */
public interface ISingleAnalysisRealm {
    String protocol();

    void initPacket(Integer batchId, boolean upstream, Packet packet);

    AbsAnalyzedPacket makePacket4Save();
}
