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