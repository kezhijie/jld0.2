package com.sinpm.app.beans;

import java.io.Serializable;

public class DeviceHandleBean implements Serializable {
    private Long id;
    private Long modelId;
    private String serialId;
    private Integer usedShots;
    private Integer totalShots;
    private Integer remainShots;
    private String code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public String getSerialId() {
        return serialId;
    }

    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }

    public Integer getUsedShots() {
        return usedShots;
    }

    public void setUsedShots(Integer usedShots) {
        this.usedShots = usedShots;
    }

    public Integer getTotalShots() {
        return totalShots;
    }

    public void setTotalShots(Integer totalShots) {
        this.totalShots = totalShots;
    }

    public Integer getRemainShots() {
        return remainShots;
    }

    public void setRemainShots(Integer remainShots) {
        this.remainShots = remainShots;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
