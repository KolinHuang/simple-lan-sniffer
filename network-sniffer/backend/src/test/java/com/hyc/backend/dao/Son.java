package com.hyc.backend.dao;

import java.io.Serializable;

/**
 * @author kol Huang
 * @date 2021/3/31
 */
public class Son extends Father implements Serializable {
    public String sonName;

    public String getSonName() {
        return sonName;
    }

    public void setSonName(String sonName) {
        this.sonName = sonName;
    }

}
