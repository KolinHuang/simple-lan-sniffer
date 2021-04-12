package com.hyc.pojo;

import java.io.Serializable;

/**
 * @author kol Huang
 * @date 2021/3/23
 */
public class AttackConfig implements Serializable {

    private String id;

    private String deviceName;

    private String destIp;

    private String destMac;

    private String srcIp;

    private String srcMac;

    private String gateIp;

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

    public String getDestIp() {
        return destIp;
    }

    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }

    public String getDestMac() {
        return destMac;
    }

    public void setDestMac(String destMac) {
        this.destMac = destMac;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public String getSrcMac() {
        return srcMac;
    }

    public void setSrcMac(String srcMac) {
        this.srcMac = srcMac;
    }

    public String getGateIp() {
        return gateIp;
    }

    public void setGateIp(String gateIp) {
        this.gateIp = gateIp;
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
