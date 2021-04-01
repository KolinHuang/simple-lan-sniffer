package com.hyc.backend.redis;

/**
 * @author kol Huang
 * @date 2021/3/30
 */
public class AnalysisKey extends BasePrefix {
    public AnalysisKey(String prefix) {
        super(prefix);
    }

    public static AnalysisKey analyzedHttpPackets = new AnalysisKey("analyzedHttpPackets_");
    public static AnalysisKey analyzedHttpsPackets = new AnalysisKey("analyzedHttpsPackets_");
    public static AnalysisKey analyzedTCPPackets = new AnalysisKey("analyzedTCPPackets_");
    public static AnalysisKey analyzedUDPPackets = new AnalysisKey("analyzedUDPPackets_");
    public static AnalysisKey analyzedICMPPackets = new AnalysisKey("analyzedICMPPackets_");
    public static AnalysisKey analyzedARPPackets = new AnalysisKey("analyzedARPPackets_");
}
