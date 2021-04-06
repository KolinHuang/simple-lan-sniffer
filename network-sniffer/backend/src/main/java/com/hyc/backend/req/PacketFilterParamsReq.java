package com.hyc.backend.req;

import java.util.List;


public class PacketFilterParamsReq {
    private List<Integer> batchIds;
    private List<Boolean> directions;
    private List<String> protocols;
    private List<String> contentTypes; //only for http packets

    public List<Integer> getBatchIds() {
        return batchIds;
    }

    public void setBatchIds(List<Integer> batchIds) {
        this.batchIds = batchIds;
    }

    public List<Boolean> getDirections() {
        return directions;
    }

    public void setDirections(List<Boolean> directions) {
        this.directions = directions;
    }

    public List<String> getProtocols() {
        return protocols;
    }

    public void setProtocols(List<String> protocols) {
        this.protocols = protocols;
    }

    public List<String> getContentTypes() {
        return contentTypes;
    }

    public void setContentTypes(List<String> contentTypes) {
        this.contentTypes = contentTypes;
    }
}
