package com.hyc.backend.controller;

import com.hyc.dto.ResultDTO;
import com.hyc.packet.AbsAnalyzedPacket;
import com.hyc.packet.AnalyzedHttpPacket;
import com.hyc.packet.AnalyzedHttpsPacket;
import com.hyc.backend.req.PacketFilterParamsReq;
import com.hyc.backend.service.AnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kol Huang
 * @date 2021/3/22
 */
@Controller
@RequestMapping("analyze")
public class AnalyzeController {

    private static final Logger logger = LoggerFactory.getLogger(AnalyzeController.class);

    @Autowired
    AnalysisService analysisService;

    @GetMapping("/analysisByBatchId")
    @ResponseBody
    public ResultDTO analysisByBatchId(@RequestParam("batchId") Integer batchId){
        logger.info("Begin to analysis packets by batchId: ", batchId);
        analysisService.cleanByBatchId(batchId);
        long size = analysisService.analysisByBatchId(batchId);
        logger.info("Finish the analysis packets for batchId: " + + batchId + ", n: " + size);
        return new ResultDTO(size);
    }

    @GetMapping("/isAnalyzing")
    @ResponseBody
    public ResultDTO isAnalyzing(){
        return new ResultDTO(analysisService.isAnalyzing());
    }

    @GetMapping("/distinctBatchIds")
    @ResponseBody
    public List<Integer> distinctBatchIds(){
        return analysisService.getBatchIdList();
    }

    @PostMapping("/filterPackets")
    @ResponseBody
    public List<AbsAnalyzedPacket> filterPackets(@RequestBody PacketFilterParamsReq req){
        List<AbsAnalyzedPacket> packets = new ArrayList<>();
        List<Integer> batchIds = req.getBatchIds();
        if(CollectionUtils.isEmpty(batchIds)){//如果前端没传参数进来，我们就选择查询所有的batch
            batchIds = analysisService.getBatchIdList();
        }
        List<String> protocols = req.getProtocols();
        if(CollectionUtils.isEmpty(protocols)){
            protocols = new ArrayList<>(analysisService.getAllProtocols());
        }
        List<Boolean> directions = req.getDirections();
        if(CollectionUtils.isEmpty(directions)){
            directions = new ArrayList<>();
            directions.add(true);
            directions.add(false);
        }
        List<String> contentTypes = req.getContentTypes();
        if(CollectionUtils.isEmpty(contentTypes)){
            contentTypes = new ArrayList<>(analysisService.getAllHTTPContentTypes());
        }
        //先通过batch_id取数据，然后再按upStream,协议、content type取数据
        //将协议放入集合
        for (Integer batchId : batchIds) {
            //根据batch_id取数据，然后按规则过滤，再放入列表
            List<AbsAnalyzedPacket> packetList = analysisService.getAnalyzedPacketsByBatchId(batchId);
            for (AbsAnalyzedPacket analyzedPacket : packetList) {
                String protocol = analyzedPacket.getProtocol();
                boolean isUpStream = analyzedPacket.isUpstream();
                if(analyzedPacket instanceof AnalyzedHttpPacket){
                    AnalyzedHttpPacket analyzedHttpPacket = (AnalyzedHttpPacket) analyzedPacket;
                    String contentType = analyzedHttpPacket.getContentType();
                    if(!contentTypes.contains(contentType)) continue;
                }else if(analyzedPacket instanceof AnalyzedHttpsPacket){
                    //TODO HTTTPS的ContentType没实现
                }
                if(protocols.contains(protocol) && directions.contains(isUpStream)){
                    packets.add(analyzedPacket);
                }
            }
        }
        return packets;
    }


}
