package com.hyc.packet;

import java.io.Serializable;

/**
 * @author kol Huang
 * @date 2021/3/22
 */
public class NetWorkInterface implements Serializable {

    private String name;

    private String description;

    private String dataLinkName;

    private String dataLinkDescription;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDataLinkName() {
        return dataLinkName;
    }

    public void setDataLinkName(String dataLinkName) {
        this.dataLinkName = dataLinkName;
    }

    public String getDataLinkDescription() {
        return dataLinkDescription;
    }

    public void setDataLinkDescription(String dataLinkDescription) {
        this.dataLinkDescription = dataLinkDescription;
    }
}
