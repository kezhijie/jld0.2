package com.sinpm.app.beans;

public class QrBean {
    private String deviceCode;
    private String url;
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }
    public String getDeviceCode() {
        return deviceCode;
    }
}
