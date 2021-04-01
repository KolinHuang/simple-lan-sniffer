package com.hyc.backend.controller;

import com.hyc.backend.dto.ResultDTO;
import com.hyc.backend.service.AnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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


}
