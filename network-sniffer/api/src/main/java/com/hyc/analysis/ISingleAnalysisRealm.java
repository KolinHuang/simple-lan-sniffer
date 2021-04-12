/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.hyc.analysis;


import com.hyc.packet.AbsAnalyzedPacket;
import com.hyc.packet.Packet;

/**
 * @author Frankie
 */
public interface ISingleAnalysisRealm {
    String protocol();

    void initPacket(Integer batchId, boolean upstream, Packet packet);

    AbsAnalyzedPacket makePacket4Save();
}
