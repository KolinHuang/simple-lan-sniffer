/*
 * Copyright (c) 2020. Frankie Fan.
 * All rights reserved.
 */

package com.hyc.backend.req;

/**
 * @author Frankie
 */
public class DeleteAttackHisReq {
    private Long batchId;

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }
}
