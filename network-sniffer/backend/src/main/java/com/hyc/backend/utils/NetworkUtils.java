package com.hyc.backend.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.*;
import java.util.*;

/**
 * @author kol Huang
 * @date 2021/3/23
 */
@SuppressWarnings("all")
public class NetworkUtils {
    private static final Logger logger = LoggerFactory.getLogger(NetworkUtils.class);

    /**
     * 获取本地IP地址以及MAC地址，遍历多网卡，找出en0
     * @return
     */
    public static Map<String, String> getLocalAddress(){
        Map<String, String> addrs;
        try{
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while(networkInterfaces.hasMoreElements()){
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while(inetAddresses.hasMoreElements()){
                    InetAddress inetAddress = inetAddresses.nextElement();
                    addrs = chooseLocalInetAddress(inetAddress, networkInterface);
                    if(addrs != null){
                        logger.info("local ip and mac address acquired!", addrs);
                        return addrs;
                    }

                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取公网地址
     * @return
     */
    public static Map<String, String> getPublicAddress(){
        Map<String, String> addrs;
        try{
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while(networkInterfaces.hasMoreElements()){
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while(inetAddresses.hasMoreElements()){
                    InetAddress inetAddress = inetAddresses.nextElement();
                    addrs = choosePublicAddress(inetAddress, networkInterface);
                    if(addrs != null){
                        logger.info("public ip and mac address acquired!", addrs);
                        return addrs;
                    }

                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Map<String, String> choosePublicAddress(InetAddress inetAddress, NetworkInterface networkInterface){
        try{
            String name = networkInterface.getDisplayName();
            if(name.contains("Adapter") ||
                name.contains("Virtual") ||
                name.contains("VMnet") ||
                name.contains("#")){
                return null;
            }
            if(networkInterface.isVirtual() ||
                    !networkInterface.isUp() ||
                    !networkInterface.supportsMulticast()){
                return null;
            }
            if(!inetAddress.isSiteLocalAddress() &&
                    !inetAddress.isLoopbackAddress() &&
                    inetAddress.getHostAddress().indexOf(":") == -1){
                Formatter formatter = new Formatter();
                String srcMAC = null;
                byte[] macBuf = networkInterface.getHardwareAddress();
                for(int i = 0; i < macBuf.length; ++i){
                    srcMAC = formatter.format(Locale.getDefault(),
                            "%02X%s",macBuf[i],
                            (i < macBuf.length - 1) ? "-" : "")
                            .toString();
                }
                formatter.close();
                Map<String, String> info = new HashMap<>();
                info.put("hostname", inetAddress.getHostName());
                info.put("ip", inetAddress.getHostAddress());
                info.put("ipnet", inetAddressTypeName(inetAddress));
                info.put("os", System.getProperty("os.name"));
                info.put("cpu-arch", System.getProperty("os.arch"));
                info.put("network-arch", networkInterface.getDisplayName());
                return info;

            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Map<String, String> chooseLocalInetAddress(InetAddress inetAddress, NetworkInterface networkInterface){
        try{
            String name = networkInterface.getDisplayName();
            if(name.contains("Adapter") ||
                    name.contains("Virtual") ||
                    name.contains("VMnet") ||
                    name.contains("#")){
                return null;
            }
            if(networkInterface.isVirtual() ||
                    !networkInterface.isUp() ||
                    !networkInterface.supportsMulticast()){
                return null;
            }
            if(inetAddress.isSiteLocalAddress() &&
                    !inetAddress.isLoopbackAddress() &&
                    inetAddress.getHostAddress().indexOf(":") == -1){
                Formatter formatter = new Formatter();
                String srcMAC = null;
                byte[] macBuf = networkInterface.getHardwareAddress();
                for(int i = 0; i < macBuf.length; ++i){
                    srcMAC = formatter.format(Locale.getDefault(),
                            "%02X%s",macBuf[i],
                            (i < macBuf.length - 1) ? "-" : "")
                            .toString();
                }
                formatter.close();
                Map<String, String> info = new HashMap<>();
                info.put("hostname", inetAddress.getHostName());
                info.put("ip", inetAddress.getHostAddress());
                info.put("mac", srcMAC);
                info.put("ipnet", inetAddressTypeName(inetAddress));
                info.put("os", System.getProperty("os.name"));
                info.put("cpu-arch", System.getProperty("os.arch"));
                info.put("network-arch", networkInterface.getDisplayName());
                return info;

            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String inetAddressTypeName(InetAddress inetAddress) {
        return (inetAddress instanceof Inet4Address) ? "ipv4" : "ipv6";
    }

    public static byte[] stomac(String s){
        if(StringUtils.isEmpty(s))  return null;

        byte[] mac = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        String[] s1 = s.split(":");
        if (s1 == null || s1.length == 0)
            s1 = s.split("-");
        for (int x = 0; x < s1.length; x++) {
            mac[x] = (byte) ((Integer.parseInt(s1[x], 16)) & 0xff);
        }
        return mac;
    }

    public static Set<String> lookup(String domain){
        if(!StringUtils.hasLength(domain))  return null;
        Set<String> inetAddresses = new HashSet<>();
        try {
            InetAddress[] arr = InetAddress.getAllByName(domain);
            for (InetAddress inetAddress : arr) {
                inetAddresses.add(inetAddress.getHostAddress());
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return inetAddresses;
    }

    public static String bytesToIp(byte[] src){
        if(src == null && src.length == 0)  return null;
        return (src[0] & 0xff) + "." + (src[1] & 0xff) + "." + (src[2] & 0xff)
                + "." + (src[3] & 0xff);
    }


}