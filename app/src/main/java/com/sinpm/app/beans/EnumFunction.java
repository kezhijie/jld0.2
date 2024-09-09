package com.sinpm.app.beans;

public enum EnumFunction {

    HEAD_TEMP("F0", "01", "热能经穴头-默认"),
    HEAD_POWER("F0", "02", "能量经穴头-默认"),
    MODE1_1("60", "00", "热能经穴头-直线波"),
    MODE1_2("60", "01", "热能经穴头-正弦波"),
    MODE2_1("60", "02", "能量经穴头-直线波"),
    MODE2_2("60", "03", "能量经穴头-正弦波"),
    PAUSE("00", "0F", "暂停"),
    START("00", "FF", "开始"),
    BACK("00", "BA", "返回"),
    POWER("50", "00", "强度"),
    MODEL("60", "00", "模式"),
    TEMP("70", "00", "温度");



    private final String code1;
    private final String code2;
    private final String desc;

    EnumFunction(String code1,String code2, String desc) {
        this.code1 = code1;
        this.code2=code2;
        this.desc = desc;
    }

    public String getCode1() {
        return code1;
    }

    public String getCode2() {
        return code2;
    }
    public String getDesc() {
        return desc;
    }


}

