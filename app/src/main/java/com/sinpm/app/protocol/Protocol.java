package com.sinpm.app.protocol;


import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.TreeMap;

public class Protocol {

    // 老版本协议 0
    public static final int VERSION_OLD = 0;
    // 新版本协议
    public static final int VERSION_NEW = 1;
    public static int VERSION = VERSION_OLD;

    public static final byte HEAD = (byte) 0xAA; // 帧头
    public static final byte TAIL = (byte) 0x00; // 帧尾部
    public static final byte HANDLE_SPRAY = 0x01; // 1.喷笔
    public static final byte HANDLE_SHOVEL = 0x02; // 2.铲刀
    public static final byte HANDLE_OCULAR_ULTRASONIC = 0x03; // 3.眼部超声波
    public static final byte HANDLE_RADIO_FREQUENCY = 0x04; // 4.射频
    public static final byte HANDLE_FACIAL_ULTRASONIC = 0x05; // 5.脸部超声波
    public static final byte HANDLE_ION_CLIP = 0x06; // 6.离子夹
    public static final byte HANDLE_ION_ROLLER = 0x07; // 7.离子滚轮
    public static final byte HANDLE_ELECTRICITY_CLIP = 0x08; // 8.电流夹
    public static final byte HANDLE_COLD_HOT_IMPORT = 0x09; // 9.冷热导入


    public static final byte SWITCH_OFF = 0x0; // 关
    public static final byte SWITCH_ON = 0x1; // 开
    // 护理模式
    public static final byte MODE_A = 0x00; // A护理模式
    public static final byte MODE_B = 0x01; // B护理模式
    public static final byte MODE_C = 0x02; // C护理模式

    // 护理强度
    public static final byte POWER_1 = 0x00; // 护理强度1
    public static final byte POWER_2 = 0x01; // 护理强度2
    public static final byte POWER_3 = 0x02; // 护理强度3
    public static final byte POWER_4 = 0x03; // 护理强度4
    public static final byte POWER_5 = 0x04; // 护理强度5

    // BYTE6 铲刀是否调频
    public static final byte FM_OFF = 0x0;
    public static final byte FM_ON = 0x1;
    // BYTE7 铲刀调频码
    public static final byte DEFAULT_SHOVEL_FM_VALUE = 81;

    public static final byte ACK = (byte) 0xCC;

    // BYTE2 低4位 0bit能量 1bit模式 2bit启动/暂停 3bit修改调频
    private static final byte BYTE_2 = (byte) 0x00;

    public static final byte SINGLE_CHIP_TO_ANDROID = (byte) 0x82;
    public static final byte ANDROID_TO_SINGLE_CHIP = (byte) 0x83;

    private static final Map<String, Short> commandMap = new TreeMap<>();

    private static byte lastSwitch = SWITCH_OFF;
    private static byte lastPower = -1;
    private static byte lastMode = -1;
    private static byte lastFM = FM_OFF;

    static {
//        commandMap.put(Constants.CUTIN_SHOVEL,HANDLE_2);
//        commandMap.put(Constants.ALLIGATOR_CLIP, HANDLE_8);
//        commandMap.put(Constants.COLD_HOT_COMPRESS, HANDLE_9);
//        commandMap.put(Constants.FACIAL_ULTRASOUND, HANDLE_5);
//        commandMap.put(Constants.SPRAY_GUN_1, HANDLE_1);
//        commandMap.put(Constants.SPRAY_GUN_2, HANDLE_10);
    }

    /**
     * 包编码
     * @param address
     * @param command
     * @param power
     * @param mode
     * @return
     */
    public static byte[] encode(byte address, byte power,
                                byte mode, byte command, byte adjustFMSwitch,
                                byte fmValue) {
        // 计算状态改变字节
        byte changeByte = BYTE_2;
        if (lastPower != power) {
            changeByte = ByteUtils.setBit(changeByte, (byte) 0, true);
            lastPower = power;
        }
        if (lastMode != mode) {
            changeByte = ByteUtils.setBit(changeByte, (byte) 1, true);
            lastMode = mode;
        }
        if (lastSwitch != command) {
            changeByte = ByteUtils.setBit(changeByte, (byte) 2, true);
            lastSwitch = command;
        }
        if (lastFM != adjustFMSwitch) {
            changeByte = ByteUtils.setBit(changeByte, (byte) 3, true);
            lastFM = adjustFMSwitch;
        }

        byte[] array;
        if (VERSION == VERSION_OLD) {
            array = oldEncode(address, power, mode, command, adjustFMSwitch, fmValue, changeByte);
        } else {
            array = newEncode(address, power, mode, command, fmValue, changeByte);
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(String.format("%02x", b));
        }
        Log.d("Protocol------Encode", sb.toString().toUpperCase());
        return array;
    }

    private static byte[] oldEncode(byte address, byte power,
                                    byte mode, byte command, byte adjustFMSwitch,
                                    byte fmValue, byte changeByte) {
        byte[] KEEP = new byte[3];
        // 数据长度 12字节
        byte len = 12;
        ByteBuffer buffer = ByteBuffer.allocate(len);
        buffer.put(HEAD); // 1字节头部
        buffer.put(address); // 1字节手柄选择
        buffer.put(changeByte); // 状态改变
        buffer.put(power); // 1字节能量
        buffer.put(mode); // 1字节模式
        buffer.put(command); // 1字节开关
        buffer.put(adjustFMSwitch);
        buffer.put(adjustFMSwitch == 0? 0x0 : fmValue);
        // 中间 3字节保留
        buffer.put(KEEP);
        buffer.put(TAIL); // 1字节尾部
        return buffer.array();
    }

    private static byte[] newEncode(byte address, byte power,
                                    byte mode, byte command,
                                    byte fmValue, byte changeByte) {
        // 协议总共9字节
        byte len = 7;
        ByteBuffer buffer = ByteBuffer.allocate(9);
        buffer.put(HEAD); // 1字节头部
        buffer.put(len);
        buffer.put(ANDROID_TO_SINGLE_CHIP);
        buffer.put(address); // 1字节手柄选择
        buffer.put(changeByte); // 状态改变
        buffer.put(power); // 1字节能量
        buffer.put(mode); // 1字节模式
        buffer.put(command); // 1字节开关
        buffer.put(fmValue);
        return buffer.array();
    }

    public static byte[] encode(byte address, byte power,
                                byte mode, byte command) {
        power = (byte) Math.min(power, POWER_5);
        return encode(address, power, mode, command, FM_OFF, (byte) 81);
    }

    public static byte[] adjustFrequencyEncode(byte address, byte value) {
        return encode(address, lastPower, lastMode, lastSwitch, FM_ON, value);
    }

    /**
     * 安卓主动构建一份应答包给单片机
     * @param address 手柄编号
     * @param flag 异常标识
     * @return 指令字节数组
     */
    public static byte[] buildACK(byte address, byte flag) {
        byte len = 4;
        ByteBuffer buffer = ByteBuffer.allocate(6);
        buffer.put(HEAD);
        buffer.put(len);
        buffer.put(ANDROID_TO_SINGLE_CHIP);
        buffer.put(address);
        buffer.put(flag);
        buffer.put(ACK);
        return buffer.array();
    }

    /**
     * short转字节数组
     * @param num
     * @return
     */
    public static byte[] short2Bytes(short num) {
        byte[] bytes = new byte[2];
        bytes[0] =  (byte)((num>>8) & 0xFF);
        bytes[1] = (byte)num;
        return bytes;
    }

    /**
     * short数组转字节数组
     * @param shorts
     * @return
     */
    public static byte[] shorts2Bytes(short[] shorts) {
        byte[] bytes = new byte[shorts.length * 2];
        byte[] temp = null;
        int i = 0;
        for (short s : shorts) {
            temp = short2Bytes(s);
            bytes[i++] = temp[0];
            bytes[i++] = temp[1];
        }
        return bytes;
    }

    /**
     * 是否相等
     * @param src
     * @param target
     * @return
     */
    public static boolean endWith(byte[] src, byte[] target) {
        if (target.length == 0) return false;
        if (src.length < target.length) return false;
        for (int i = 0;i < target.length;i++) {
            if (target[target.length - i - 1] != src[src.length - i - 1]) {
                return false;
            }
        }
        return true;
    }

    public static boolean endWith(Byte[] src, byte[] target) {
        if (target.length == 0) return false;
        if (src.length < target.length) return false;
        for (int i = 0;i < target.length;i++) {
            if (target[target.length - i - 1] != src[src.length - i - 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 字节数组转int
     * @param bytes
     * @return
     */
    public static int bytesToInt(byte[] bytes) {
        int res = 0;
        for (int i = 0; i < bytes.length; i++) {
            res = (res << 8) | (bytes[i] & 0xff);
        }
        return res;
    }

    /**
     * 字节数组转换成16进制字符串
     * @param bytes
     * @return
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static Map<String, Short> getCommandMap() {
        return commandMap;
    }
}
