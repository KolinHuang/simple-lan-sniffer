package com.hyc.backend.service;

import com.hyc.backend.dao.RedisMapper;
import com.hyc.backend.pojo.AttackConfig;
import com.hyc.backend.redis.AttackKey;
import com.hyc.backend.utils.NetworkUtils;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

/**
 * @author kol Huang
 * @date 2021/3/22
 */
@Service
public class AttackService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AttackService.class);

    @Resource
    RedisMapper redisMapper;

    private NetworkInterface[] devices;

    private NetworkInterface mainDevice;

    private String mainDeviceName;

    JpcapCaptor captor;

    @PostConstruct
    public void initDefaultConfig(){
        this.initDeviceList();

        AttackConfig config = (AttackConfig) redisMapper.get(AttackKey.config, "config");
        if(config == null){
            String dstIP = "";
            String dstMAC = "";
            String gateIP = "";
            String gateMAC = "";
            String srcIP = null;
            String srcMAC = null;

            //获取本地IP和MAC地址
            Map<String, String> addrs = NetworkUtils.getLocalAddress();
            if(addrs == null){
                addrs = NetworkUtils.getPublicAddress();

            }
            if(addrs != null){
                srcIP = addrs.get("ip");
                srcMAC = addrs.get("mac");
            }else{
                logger.error("can not acquire source IP/MAC address");
            }
            config = new AttackConfig();
            config.setDeviceName(null);
            if(srcMAC != null){
                srcMAC = srcMAC.replace('-', ':').toLowerCase();
            }
            config.setSrcMAC(srcMAC);
            config.setSrcIP(srcIP);
            if(dstMAC != null){
                dstMAC = dstMAC.replace('-', ':').toLowerCase();
            }
            config.setDstMAC(dstMAC);
            config.setDstIP(dstIP);
            if(gateMAC != null){
                gateMAC = gateMAC.replace('-', ':').toLowerCase();
            }
            config.setGateMac(gateMAC);
            config.setGateIP(gateIP);
            redisMapper.set(AttackKey.config, "config", config);
        }

    }

    /**
     * 枚举网卡
     */
    private void initDeviceList(){
        this.devices = JpcapCaptor.getDeviceList();
        if(this.devices != null && this.devices.length > 0){
            mainDevice = devices[0];
            this.mainDeviceName = mainDevice.name;
        }
    }

    public NetworkInterface[] getDevices(){
        return devices;
    }
}
