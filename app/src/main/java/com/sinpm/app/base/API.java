package com.sinpm.app.base;

public interface API {
//    String Base_URL = "http://192.168.3.10:8082/app/";

    String Base_URL = "http://open.sinpm.com/app/";
    String Base_PRD_URL = "http://open.sinpm.com/app/";
    String CheckUpdate = "upgrade/check";
    //    String PUSH_URL = "http://192.168.3.50:9999/";
    String PUSH_URL = "http://alloc.sinpm.com/";
    String PUSH_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCAAi480rPPJ5g0igYkSUonxc3ftFpIW1c+wgvCyJYKmZ+rh34+iCe+E29YVS576NVM5PPQZX2fk/0m2if7m4/TEBh6e5M5ysACP9zA1mEvT+NbtV9V7Lr9sojqgoHrKrfN2RPLIP6Kl9y6JX6wdJBbS8lZv61cRx1TP6Y3sdWaqwIDAQAB";
    String TestCommand = "device/command/test";
    String LoginCode = "device/customer";
    String Init = "device/init";
    String FRESH_QRCODE = "device/qrCode";
    String TEST_QRCODE = "device/qrCode/test";
    String TEST_LOG = "device/test/change";
    String UPLOAD_IMG = "gs/file/upload";
    String GET_SCORE = "gs";
    String UPLOAD_VERSION = "upgrade/log";
    //    String UPLOAD_USETIME = Base_URL+"device/use/log";
    String UPLOAD_USETIME = "device/use/log/save";

    String UPLOAD_USE_TIME_PUSH = "device/use/log/push";
    //    生成检测报告
    String INIT_REPORT = "gs/total";
    //区分GET_SCORE 一个是post 一个是get
    String GET_REPORT = "report";
    String GET_REPORT_INFO = "report/info";
    String getScanPayUrl = "pay/url/user";
    //    String GET_REPORTID = Base_URL+"gs/initReport";
    String GET_REPORTID = "report/init";
    String GET_REPORT_LIST = "report/list";
    String GET_REPORT_FIRST = "report/first";
    String GET_REPORT_QR = "report/qrcode";
    String BIND_QR="device/qrCode";
    String GET_STATUS = "device/status";

    String getUnlockType = "device/unlock/type/list";
    String getPayTime = "pay/list";
    String getUnlockQrCode = "device/unlock";
    //余额抵扣 获取token
    String getAmountToken = "pay/deposit/token";
    String confirmAmountPay = "pay/deposit/confirm";
    String changeCommandState = "device/command/change";
    String checkMsg = "device/command/check";

    String handelInfo = "device/handle/info";

    String handleUpload = "device/handle/up/count";

}
