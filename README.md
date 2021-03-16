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

然后到`usr/local/lib`目录下就能找到名为`libpcap.dylib`文件，然后执行以下命令将其绑定到Java的动态链接库：

```shell
export JAVA_LIBRARY_PATH=usr/local/lib
```

libpcap库安装结束。接下来安装jpcap，首先到https://github.com/jpcap/jpcap 下载源码包，再按以下指示完成步骤：（摘录自https://sites.google.com/site/sipinspectorsite/download/jpcap）

**<Mac OS X>** 

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

如果执行第5步失败了，也可以直接将本项目`resources`目录下的`libjpcap.jnilib`文件复制到`/Library/Java/Extensions/`目录下，再将`jpcap.jar`包复制到`jre/lib/ext`目录下让扩展类加载器加载，或者放到自己设定的类路径下由系统类加载器加载。

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

		parsedData.clear();
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

![image-20210316192859961](/Users/huangyucai/Library/Application Support/typora-user-images/image-20210316192859961.png)

解析结果：

![image-20210316193546007](https://hyc-pic.oss-cn-hangzhou.aliyuncs.com/image-20210316193546007.png)

最后解析TCP报文段。jpcap对TCP报文段的封装类是`TCPPacket`，TCP报文段结构如下所示：

![image-20210316194225746](/Users/huangyucai/Library/Application Support/typora-user-images/image-20210316194225746.png)

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

![image-20210316194333948](/Users/huangyucai/Library/Application Support/typora-user-images/image-20210316194333948.png)

至此一个TCP报文段解析完毕。

再按同样的方法编写ARP、IPv6、UDP、HTTP等常见协议，就能基本实现抓包和解析功能。

