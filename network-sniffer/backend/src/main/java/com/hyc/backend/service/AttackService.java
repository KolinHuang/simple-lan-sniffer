package com.hyc.backend.service;

import com.hyc.backend.dao.RedisMapper;
import com.hyc.backend.pojo.AttackConfig;
import com.hyc.backend.redis.AttackKey;
import com.hyc.backend.redis.CommonKey;
import com.hyc.backend.utils.NetworkUtils;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.ARPPacket;
import jpcap.packet.EthernetPacket;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
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

    private Date startAttackTimeStamp;
    private Integer batchId;


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
        AttackConfig config = (AttackConfig) redisMapper.get(AttackKey.config, "config", AttackConfig.class);
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
    @PostConstruct
    public void initBatchId(){
        redisMapper.setnx(CommonKey.COMMON_KEY,"batch_id", 0);
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

    public synchronized void attack(){
        //当前处于攻击状态，说明已经有线程在攻击了，所以当前线程直接返回
        if(attacking)   return;
        attacking = true;

        startAttackTimeStamp = new Date();//记录当前时间
        //为了方便统计数据，我们为每次攻击都设置一个批次ID，即batchId
        //这个batchId需要是自增的，如何获取一个自增的batchId呢？
        //那就还从redis里取呗，但是这个batchId必须跟每次攻击的数据包对应好，否则就乱了
        //OK，那我们就在初始化Servlet的时候，就在Redis里设置一个存id的键，如果不存在，那就创建一个，见方法initBatchId
        batchId = (Integer) redisMapper.get(CommonKey.COMMON_KEY, "batch_id", Integer.class);
        //这里没考虑并发修改redis的batchId的问题，因为同一时间只有一个线程会执行攻击方法
        //因为AttackService默认是单例的，锁上之后，别的线程也没法调用attack方法，也就不会并发修改batchId
        //但是仍旧是线程不安全的，如果要实现线程安全，可以选择加分布式锁，即执行redis的set lock:batchid true ex 5 nx
        //这里就不实现了
        batchId++;

        //设置ARP包欺骗目标主机，将自己伪装成网关
        ARPPacket arpToDst = createARPPacket(srcMACBT, gateIPIA.getAddress(), dstMACBT, dstIPIA.getAddress());


        //设置ARP包欺骗网关，假装自己是目标主机，因此源IP地址需要改成攻击目标的IP地址，但是MAC地址修改成本地主机的MAC地址
        //这样就能让网关认为本地主机的MAC地址是攻击目标的MAC地址，进而将需要发到目标主机的数据包通过ARP表发到本地主机的网卡上
        ARPPacket arpToGate = createARPPacket(srcMACBT, dstIPIA.getAddress(), gateMACBT, gateIPIA.getAddress());


    }

    private ARPPacket createARPPacket(byte[] sHardAddr, byte[] sProtoAddr,byte[] tHardAddr, byte[] tProtoAddr){
        ARPPacket arpPacket = new ARPPacket();
        arpPacket.hardtype = ARPPacket.HARDTYPE_ETHER;
        arpPacket.prototype = ARPPacket.PROTOTYPE_IP;
        arpPacket.operation = ARPPacket.ARP_REPLY;//设置操作类型为应答
        arpPacket.hlen = 6;//硬件地址长度
        arpPacket.plen = 4;//协议类型长度
        arpPacket.sender_hardaddr = sHardAddr;//发送端MAC地址
        arpPacket.sender_protoaddr = sProtoAddr;//发送端IP地址
        arpPacket.target_hardaddr = tHardAddr;//目标MAC地址
        arpPacket.target_protoaddr = tProtoAddr;//目标IP地址

        //定义以太网首部
        EthernetPacket ethernetPacket = new EthernetPacket();
        ethernetPacket.frametype = EthernetPacket.ETHERTYPE_ARP;//设置帧类型为ARP帧
        ethernetPacket.src_mac = sHardAddr;//源MAC地址
        ethernetPacket.dst_mac = tHardAddr;//目标MAC地址
        //添加以太网首部
        arpPacket.datalink = ethernetPacket;
        return arpPacket;
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
