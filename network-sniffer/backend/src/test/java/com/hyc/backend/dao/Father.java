package com.hyc.backend.dao;

import java.io.Serializable;

/**
 * @author kol Huang
 * @date 2021/3/31
 */
public class Father implements Serializable {

    public String fatherName;

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    @Override
    public String toString() {
        return "Father{" +
                "fatherName='" + fatherName + '\'' +
                '}';
    }
}
