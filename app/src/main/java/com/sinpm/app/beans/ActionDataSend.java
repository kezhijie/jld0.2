package com.sinpm.app.beans;


import com.sinpm.app.Utils.CRC16Util;

import java.util.Locale;
import java.util.Map;

public class ActionDataSend {
    public ActionDataSend() {
        this.head1 = "AA";
        this.head2 = "78";
        this.function3 = "F0";
        this.param4 = "01";

        this.end5 = "CC";
        this.end6 = "33";
        this.end7 = "C3";
        this.end8 = "3C";

    }

    private String head1;
    private String head2;
    private String function3;
    private String param4;

    private String end5;
    private String end6;

    private String end7;

    private String end8;




    private String crc9;

    private String crc10;
    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getEnd7() {
        return end7;
    }

    public void setEnd7(String end7) {
        this.end7 = end7;
    }

    public String getHead1() {
        return head1;
    }

    public void setHead1(String head1) {
        this.head1 = head1;
    }

    public String getHead2() {
        return head2;
    }

    public void setHead2(String head2) {
        this.head2 = head2;
    }

    public String getFunction3() {
        return function3;
    }

    public void setFunction3(String function3) {
        this.function3 = function3;
    }

    public void setFunctionAndParam(EnumFunction enumFunction) {
        this.function3 = enumFunction.getCode1();
        this.param4 = enumFunction.getCode2();
        this.desc = enumFunction.getDesc();
    }

    public String getParam4() {
        return param4;
    }

    public void setParam4(String param4) {
        this.param4 = param4;
    }

    public String getEnd5() {
        return end5;
    }


    private Map<Integer, String> processFunctionMap;


    public void setEnd5(String end5) {
        this.end5 = end5;
    }

    public String getEnd6() {
        return end6;
    }

    public void setEnd6(String end6) {
        this.end6 = end6;
    }

    public String getEnd8() {
        return end8;
    }

    public void setEnd8(String end8) {
        this.end8 = end8;
    }

    public String getCrc9() {
        return crc9;
    }

    public void setCrc9(String crc9) {
        this.crc9 = crc9;
    }

    public String getCrc10() {
        return crc10;
    }

    public void setCrc10(String crc10) {
        this.crc10 = crc10;
    }


    public String getCmdAndCrc() {
        String hexInstruction = head1 + head2 + function3 + param4 + end5 + end6 + end7 + end8;
//        String crcHex = CRC16Util.calculateCRC16(hexInstruction).toUpperCase(Locale.ROOT);
//        this.crc9 = crcHex.substring(0, 2);
//        this.crc10 = crcHex.substring(2, 4);
        return head1 + " " + head2 + " " + function3 + " " + param4 + " " + end5 + " " + end6 + " " + end7 + " " + end8  ;
    }

    public void complete() {
        this.function3 = EnumFunction.BACK.getCode1();
    }

}
