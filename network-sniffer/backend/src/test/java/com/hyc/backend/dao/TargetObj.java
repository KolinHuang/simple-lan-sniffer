package com.hyc.backend.dao;

import java.io.Serializable;

/**
 * @author kol Huang
 * @date 2021/3/31
 */
public class TargetObj implements Serializable {
    public int id;
    public String name;


    public Father property;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Father getProperty() {
        return property;
    }

    public void setProperty(Father property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return "TargetObj{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", property=" + property +
                '}';
    }
}
