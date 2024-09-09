package com.sinpm.app.beans;


import java.io.Serializable;

/**
 * @author weiliu
 */
public class GenerateResponseBean implements Serializable {
    /**
     * 设备编号
     */
    private String deviceCode;
    /**
     * 设备二维码链接
     */
    private String url;

    /**
     * 是否需要激活
     */
    private Integer needActive;

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getNeedActive() {
        return needActive;
    }

    public void setNeedActive(Integer needActive) {
        this.needActive = needActive;
    }
}
