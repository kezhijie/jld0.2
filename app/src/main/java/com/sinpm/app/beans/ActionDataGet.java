package com.sinpm.app.beans;


import com.hjq.toast.ToastUtils;
import com.sinpm.app.Utils.CRC16Util;

import java.util.Locale;

public class ActionDataGet {
    public ActionDataGet() {
        this.head1 = "1A";
        this.head2 = "2B";
        this.head3 = "0C";
        this.head4 = "02";
        this.function5 = "01";
        this.param6 = "01";
        this.status7 = "01";
        this.end8 = "00";
        this.end11 = "3C";
        this.end12 = "4D";
    }

    private String head1;
    private String head2;
    private String head3;
    private String head4;

    /**
     * 工作页面：01/02/03/04/05/06/07 （字节5）
     * 01代表气动力测试，
     * 02代表启动定位系统，
     * 03代表装入针筒，
     * 04代表试打，
     * 05代表单次施打
     * 06代表连续施打
     * 07施打发数统计；
     */
    private String function5;
    /**
     * 启停：0a/0b/0c（字节7），
     * 0a代表确认或者开始，
     * 0b代表暂停或者取消；
     * 0c代表
     */
    private String param6;

    private String status7;
    private String end8;

    private String crc9;

    private String crc10;

    private String end11;
    private String end12;

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

    public String getHead3() {
        return head3;
    }

    public void setHead3(String head3) {
        this.head3 = head3;
    }

    public String getHead4() {
        return head4;
    }

    public void setHead4(String head4) {
        this.head4 = head4;
    }

    public String getFunction5() {
        return function5;
    }


    public void setFunction5(String function5) {
        this.function5 = function5;
    }

    public String getParam6() {
        return param6;
    }

    public String getStatus7() {
        return status7;
    }

    public void setStatus7(String status7) {
        this.status7 = status7;
    }

    public void setParam6(String param6) {
        this.param6 = param6;
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

    public String getEnd11() {
        return end11;
    }

    public void setEnd11(String end11) {
        this.end11 = end11;
    }

    public String getEnd12() {
        return end12;
    }

    public void setEnd12(String end12) {
        this.end12 = end12;
    }

    public String getCmdAndCrc() {
        String hexInstruction = head1 + head2 + head3 + head4 + function5 + param6 + status7 + end8;
        String crcHex = CRC16Util.calculateCRC16(hexInstruction).toUpperCase(Locale.ROOT);
        this.crc9 = crcHex.substring(0, 2);
        this.crc10 = crcHex.substring(2, 4);
        return head1 + " " + head2 + " " + head3 + " " + head4 + " " + function5 + " " + param6 + " " + status7 + " " + end8 + " " + crc9 + " " + crc10 + " " + end11 + " " + end12;
    }

    public ActionDataGet(String cmd) {
        String[] cmdArray = cmd.replace(" ", " ").split(" ");
        if (cmdArray.length < 12) {
            ToastUtils.show("指令信息有误");
            return;
        }
        this.head1 = cmdArray[0];
        this.head2 = cmdArray[1];
        this.head3 = cmdArray[2];
        this.head4 = cmdArray[3];
        this.function5 = cmdArray[4];
        this.param6 = cmdArray[5];
        this.status7 = cmdArray[6];
        this.end8 = cmdArray[7];
        this.crc9 = cmdArray[8];
        this.crc10 = cmdArray[9];
        this.end11 = cmdArray[10];
        this.end12 = cmdArray[11];
    }
}
