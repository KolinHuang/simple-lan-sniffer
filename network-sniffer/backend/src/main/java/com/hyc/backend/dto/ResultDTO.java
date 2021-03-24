package com.hyc.backend.dto;

import java.io.Serializable;

/**
 * @author kol Huang
 * @date 2021/3/24
 */
public class ResultDTO implements Serializable {

    private Serializable result;

    public ResultDTO() {
    }

    public ResultDTO(Serializable result) {
        this.result = result;
    }

    public Serializable getResult() {
        return result;
    }

    public void setResult(Serializable result) {
        this.result = result;
    }
}
