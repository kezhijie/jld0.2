package com.sinpm.app.beans;

public class UseInfoBean {
    private int id;
    private Long logId;
    private String name;
    private String code;
    private String storeId;
    private int modelId;
    private String storeName;
    private int deviceId;
    private String customerName;
    private String customerPhone;
    private String startTime;
    private String endTime;
    private int duration;
    private int totalDuration;
    private int unlockType;

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }
    public Long getLogId() {
        return logId;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
    public String getStoreId() {
        return storeId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }
    public int getModelId() {
        return modelId;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }
    public String getStoreName() {
        return storeName;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
    public int getDeviceId() {
        return deviceId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getStartTime() {
        return startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    public String getEndTime() {
        return endTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
    public int getDuration() {
        return duration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }
    public int getTotalDuration() {
        return totalDuration;
    }

    public void setUnlockType(int unlockType) {
        this.unlockType = unlockType;
    }
    public int getUnlockType() {
        return unlockType;
    }

}
