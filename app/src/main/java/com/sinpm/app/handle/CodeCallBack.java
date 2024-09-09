package com.sinpm.app.handle;

public interface CodeCallBack {
    /**
     * 获取到了设备编号
     */
    void getCode(String code,String qrCodeUrl);
}
