package com.hyc.backend.dao;

import java.io.Serializable;

/**
 * @author kol Huang
 * @date 2021/3/23
 */
public class User implements Serializable {
    private String  name;

    private Integer id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
