package com.sinpm.app.beans;

public class PayUrlBean {
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

    String deviceCode;
    String url;
    Double money;
    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }


}
