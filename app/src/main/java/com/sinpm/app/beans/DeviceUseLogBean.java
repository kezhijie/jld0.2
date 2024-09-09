package com.sinpm.app.beans;

import java.util.List;

public class DeviceUseLogBean {

    private Long id;
    private String code;
    private Integer duration;
    private Integer totalDuration;
    private Integer type;
    private Integer unlockType;

    public DeviceUseLogBean(Long id, String code, Integer duration,
                            Integer totalDuration, Integer type, Integer unlockType, Boolean isUpload, Integer cId) {
        this.id = id;
        this.code = code;
        this.duration = duration;
        this.totalDuration = totalDuration;
        this.type = type;
        this.unlockType = unlockType;
        this.isUpload = isUpload;
        this.cId = cId;
    }

    public DeviceUseLogBean() {
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Integer totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getUnlockType() {
        return unlockType;
    }

    public void setUnlockType(Integer unlockType) {
        this.unlockType = unlockType;
    }

    public Integer getCmdId() {
        return cmdId;
    }

    public void setCmdId(Integer cmdId) {
        this.cmdId = cmdId;
    }

    Integer cmdId;
    Boolean isUpload;
    /**
     * 设备探头详细使用时长
     */
    private List<DeviceUseLogDetailDTO> detailList;

    public List<DeviceUseLogDetailDTO> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<DeviceUseLogDetailDTO> detailList) {
        this.detailList = detailList;
    }

    public Integer getcId() {
        return cId;
    }

    public void setcId(Integer cId) {
        this.cId = cId;
    }

    Integer cId;

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    Long startTime;

    public Boolean getUpload() {
        return isUpload;
    }

    public void setUpload(Boolean upload) {
        isUpload = upload;
    }


}
