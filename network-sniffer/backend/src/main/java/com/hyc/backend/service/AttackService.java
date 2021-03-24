package com.hyc.backend.service;

import com.hyc.backend.dao.RedisMapper;
import com.hyc.backend.pojo.AttackConfig;
import com.hyc.backend.redis.AttackKey;
import com.hyc.backend.utils.NetworkUtils;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

    private AttackConfig config;

    JpcapCaptor captor;
    JpcapSender sender;

    //根据配置获取的地址实体对象
    private InetAddress dstIPIA;
    private byte[] dstMACBT;
    private InetAddress srcIPIA;
    private byte[] srcMACBT;
    private InetAddress gateIPIA;
    private byte[] gateMACBT;

    private boolean attacking = false;


    private long upStreamNum;
    private long downStreamNum;
    private long upTcpNum;
    private long upUdpNum;
    private long upIcmpNum;
    private long upArpNum;
    private long downTcpNum;
    private long downUdpNum;
    private long downIcmpNum;
    private long downArpNum;

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
            config.setSrcMac(srcMAC);
            config.setSrcIp(srcIP);
            if(StringUtils.hasLength(dstMAC)){
                dstMAC = dstMAC.replace('-', ':').toLowerCase();
            }
            config.setDestMac(dstMAC);
            config.setDestIp(dstIP);
            if(StringUtils.hasLength(gateMAC)){
                gateMAC = gateMAC.replace('-', ':').toLowerCase();
            }
            config.setGateMac(gateMAC);
            config.setGateIp(gateIP);
            redisMapper.set(AttackKey.config, "config", config);
        }

    }

    public synchronized void updateConfigAndOpenDevice(AttackConfig config) throws IOException {
        this.mainDeviceName = config.getDeviceName();
        this.config = config;
        openDevice(config.getDeviceName());
        initAddresses();
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

    /**
     * 根据传入的设备名参数开启网卡，如果对应名称的设备不存在，那么就使用默认的设备
     * @param deviceName
     */
    private void openDevice(String deviceName) throws IOException {
        this.mainDevice = getDeviceByName(deviceName);
        if(mainDevice == null){
            this.mainDevice = devices[0];
            logger.error("Cannot get device named" + deviceName + ", so use the default device: " + mainDevice.name);
        }
        this.mainDeviceName = mainDevice.name;
        captor = JpcapCaptor.openDevice(mainDevice, 2000, false, 10000);
        //可设置过滤器
        //add Filter here

        sender = captor.getJpcapSenderInstance();//打开网卡设备
    }

    private void initAddresses() throws UnknownHostException {
        dstIPIA = InetAddress.getByName(config.getDestIp());
        dstMACBT = NetworkUtils.stomac(config.getDestMac());
        srcIPIA = InetAddress.getByName(config.getSrcIp());
        srcMACBT = NetworkUtils.stomac(config.getSrcMac());
        gateIPIA = InetAddress.getByName(config.getGateIp());
        gateMACBT = NetworkUtils.stomac(config.getGateMac());
    }

    private NetworkInterface getDeviceByName(String deviceName){
        if(devices == null || !StringUtils.hasLength(deviceName))   return null;
        for (NetworkInterface device : devices) {
            if(deviceName.equals(device.name)){
                return device;
            }
        }
        return null;
    }


    public boolean isAttacking() {
        return attacking;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public NetworkInterface[] getDevices(){
        return devices;
    }


    public long getUpStreamNum() {
        return upStreamNum;
    }

    public long getDownStreamNum() {
        return downStreamNum;
    }

    public long getUpTcpNum() {
        return upTcpNum;
    }

    public long getUpUdpNum() {
        return upUdpNum;
    }

    public long getUpIcmpNum() {
        return upIcmpNum;
    }

    public long getUpArpNum() {
        return upArpNum;
    }

    public long getDownTcpNum() {
        return downTcpNum;
    }

    public long getDownUdpNum() {
        return downUdpNum;
    }

    public long getDownIcmpNum() {
        return downIcmpNum;
    }

    public long getDownArpNum() {
        return downArpNum;
    }
}
