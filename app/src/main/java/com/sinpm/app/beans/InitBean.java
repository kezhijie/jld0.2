package com.sinpm.app.beans;

public class InitBean {
    private String deviceCode;
    private String url;

    public Integer getNeedActive() {
        return needActive;
    }

    public void setNeedActive(Integer needActive) {
        this.needActive = needActive;
    }

    private Integer needActive;
    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }
    public String getDeviceCode() {
        return deviceCode;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }
}
