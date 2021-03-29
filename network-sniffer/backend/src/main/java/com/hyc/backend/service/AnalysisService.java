package com.hyc.backend.service;

import com.hyc.backend.dao.RedisMapper;
import com.hyc.backend.pojo.CapturedPacket;
import com.hyc.backend.redis.AttackKey;
import com.hyc.backend.redis.KeyPrefix;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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

    public long analysisByBatchId(Integer batchId){
        analyzing = true;
        long size = 0;
        List<CapturedPacket> capturedPackets = (List<CapturedPacket>) redisMapper.getFromList(AttackKey.cap_packet, "batch_id_" + batchId + "_list");
        size +=
        return size;
    }

    private long analysisHttp(List<CapturedPacket> capturedPackets){

    }
    private long analysisHttps(List<CapturedPacket> capturedPackets){

    }
    private long analysisTcp(List<CapturedPacket> capturedPackets){

    }
    private long analysisUdp(List<CapturedPacket> capturedPackets){

    }
    private long analysisIcmp(List<CapturedPacket> capturedPackets){

    }
    private long analysisArp(List<CapturedPacket> capturedPackets){

    }

}
