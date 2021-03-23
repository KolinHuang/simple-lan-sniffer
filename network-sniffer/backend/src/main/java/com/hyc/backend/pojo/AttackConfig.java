package com.hyc.backend.pojo;

import org.springframework.cache.annotation.EnableCaching;

import java.io.Serializable;
import java.lang.annotation.Documented;

/**
 * @author kol Huang
 * @date 2021/3/23
 */
public class AttackConfig implements Serializable {

    private String id;

    private String deviceName;

    private String dstIP;

    private String dstMAC;

    private String srcIP;

    private String srcMAC;

    private String gateIP;

    private String gateMac;

    private String filterDomain;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDstIP() {
        return dstIP;
    }

    public void setDstIP(String dstIP) {
        this.dstIP = dstIP;
    }

    public String getDstMAC() {
        return dstMAC;
    }

    public void setDstMAC(String dstMAC) {
        this.dstMAC = dstMAC;
    }

    public String getSrcIP() {
        return srcIP;
    }

    public void setSrcIP(String srcIP) {
        this.srcIP = srcIP;
    }

    public String getSrcMAC() {
        return srcMAC;
    }

    public void setSrcMAC(String srcMAC) {
        this.srcMAC = srcMAC;
    }

    public String getGateIP() {
        return gateIP;
    }

    public void setGateIP(String gateIP) {
        this.gateIP = gateIP;
    }

    public String getGateMac() {
        return gateMac;
    }

    public void setGateMac(String gateMac) {
        this.gateMac = gateMac;
    }

    public String getFilterDomain() {
        return filterDomain;
    }

    public void setFilterDomain(String filterDomain) {
        this.filterDomain = filterDomain;
    }
}
