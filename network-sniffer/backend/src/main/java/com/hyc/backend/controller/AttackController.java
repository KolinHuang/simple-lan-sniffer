package com.hyc.backend.controller;

import com.hyc.backend.dao.RedisMapper;
import com.hyc.backend.dto.AttackStatisticDTO;
import com.hyc.backend.dto.ResultDTO;
import com.hyc.backend.packet.NetWorkInterface;
import com.hyc.backend.pojo.AttackConfig;
import com.hyc.backend.redis.AttackKey;
import com.hyc.backend.service.AttackService;
import jpcap.NetworkInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kol Huang
 * @date 2021/3/22
 */
@Controller
@RequestMapping("attack")
public class AttackController {

    private static final Logger logger = LoggerFactory.getLogger(AttackController.class);

    @Autowired
    private AttackService attackService;
    @Resource
    private RedisMapper redisMapper;

    private boolean deviceOpened = false;

    @GetMapping(value = "/getDeviceList")
    @ResponseBody
    public List<NetWorkInterface> getDeviceList(){
        List<NetWorkInterface> devices = new ArrayList<>();
        NetworkInterface[] interfaces = attackService.getDevices();
        if(interfaces != null){
            for (NetworkInterface networkInterface : interfaces) {
                NetWorkInterface ni = new NetWorkInterface();
                ni.setName(networkInterface.name);
                ni.setDescription(networkInterface.description);
                ni.setDataLinkName(networkInterface.datalink_name);
                ni.setDataLinkDescription(networkInterface.datalink_description);
                devices.add(ni);
            }
        }
        return devices;
    }

    @GetMapping("/isDeviceOpened")
    @ResponseBody
    public ResultDTO isDeviceOpened(){
        return new ResultDTO(deviceOpened);
    }

    @PostMapping("/updateConfigAndOpenDevice")
    @ResponseBody
    public ResultDTO updateConfigAndOpenDevice(@RequestBody AttackConfig req) throws IOException {
        //如果攻击配置已经初始化完毕
        if(req.getSrcIp() != null  && req.getSrcMac() != null &&
            req.getDestIp() != null && req.getDestMac() != null &&
            req.getGateMac() != null && req.getGateMac() != null &&
            req.getFilterDomain() != null){
            req.setSrcMac(req.getSrcMac().replace('-', ':').toLowerCase());
            req.setDestMac(req.getDestMac().replace('-', ':').toLowerCase());
            req.setGateMac(req.getGateMac().replace('-', ':').toLowerCase());

            attackService.updateConfigAndOpenDevice(req);
            deviceOpened = true;
            AttackConfig config = (AttackConfig) redisMapper.get(AttackKey.config, "config", AttackConfig.class);
            if(config != null){
                req.setId(config.getId());
            }

            redisMapper.set(AttackKey.config, "config", req);
            return new ResultDTO(true);
        }else{
            return new ResultDTO(false);
        }
    }

    /**
     * 返回拦截的数据包的统计信息
     * @return
     */
    @GetMapping("/getAttackStatistic")
    @ResponseBody
    public AttackStatisticDTO getAttackStatistic(){
        AttackStatisticDTO dto = new AttackStatisticDTO();

        dto.setUpStreamNum(attackService.getUpStreamNum());
        dto.setDownStreamNum(attackService.getDownStreamNum());

        dto.setUpArpNum(attackService.getUpArpNum());
        dto.setDownArpNum(attackService.getDownArpNum());

        dto.setUpIcmpNum(attackService.getUpIcmpNum());
        dto.setDownIcmpNum(attackService.getDownIcmpNum());

        dto.setUpTcpNum(attackService.getUpTcpNum());
        dto.setDownTcpNum(attackService.getDownTcpNum());

        dto.setUpUdpNum(attackService.getUpUdpNum());
        dto.setDownUdpNum(attackService.getDownUdpNum());

        return dto;
    }

    @GetMapping("/isAttacking")
    @ResponseBody
    public ResultDTO isAttacking(){
        ResultDTO dto = new ResultDTO();
        dto.setResult(attackService.isAttacking());
        return dto;
    }

    @GetMapping("/startAttack")
    @ResponseBody
    public ResultDTO startAttacking(){
        logger.info("start attacking");
        attackService.attack();
        return new ResultDTO(true);
    }

    @GetMapping("/stopAttack")
    @ResponseBody
    public ResultDTO stopAttacking(){
        logger.info("stop attacking");
        Integer batchId = attackService.stopAttack();
        return new ResultDTO(batchId);
    }

    @GetMapping("/getAttackConfig")
    @ResponseBody
    public ResultDTO getAttackConfig(){
        AttackConfig config = (AttackConfig) redisMapper.get(AttackKey.config, "config", AttackConfig.class);
        if(config == null){
            config = new AttackConfig();
        }
        return new ResultDTO(config);
    }
}
