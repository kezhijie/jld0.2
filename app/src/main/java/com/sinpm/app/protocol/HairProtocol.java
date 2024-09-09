package com.sinpm.app.protocol;


import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class HairProtocol {


    public static final byte[] HEAD = {(byte) 0xAA,0x55}; // 帧头(或者用short？)
    public static final byte[] TAIL = {0x55,(byte)0xAA}; // 帧尾部


    //指令类型
    //安卓主动发消息给单片机，指令帧
    public static final byte ANDROID_TO_SINGLE_CHIP = 0x11;
    //单片机主动发消息给安卓，指令帧
    public static final byte SINGLE_CHIP_TO_ANDROID = 0x12;
    //单片机收到指令后应答给安卓屏，应答帧
    public static final byte SINGLE_CHIP_ACK_ANDROID = 0x21;
    //安卓屏收到指令后应答给单片机，应答帧
    public static final byte ANDROID_ACK_SINGLE_CHIP = 0x22;

    //安卓发送设备控制指令0xE1，对设备进行控制，主要是手柄的开关和相关工作参数。
    public static final byte CONTROL_COMMAND = (byte) 0xE1;
    //单片机向安卓屏发送使用信息
    public static final byte USE_COMMAND = (byte) 0xF1;
    //单片机向安卓屏发送异常信息
    public static final byte EXCEPTION_COMMAND = (byte) 0xFF;

    //指令编号，可以设置00~ff自增

    // 似乎只有一个手柄
    public static final byte HANDLE = 0x00;


    //change status 状态变化指令
    //energy 能量值指令

    // 选择的身体部位，分别对应part
    public static final byte MODE_FACE = 0x01; // 脸部模式
    public static final byte MODE_HAND = 0x02; // 手臂模式
    public static final byte MODE_PPRVATE = 0x03; // 私密模式
    public static final byte MODE_LEG = 0x04; // 腿部模式

    //手柄开关指令，对应service的待机键和准备键
    public static final byte SWITCH_OFF = 0x0;
    public static final byte SWITCH_ON = 0x1;
    public static final String VERSION = "赫拉串口通讯协议V1.3";

    //frequency 频率值指令


    // BYTE2 低4位 0bit能量 1bit模式 2bit启动/暂停 3bit修改调频
    private static final byte BYTE_2 = (byte) 0x00;



    private static byte lastSwitch = SWITCH_OFF;
    private static byte lastEnergy = -1;
    private static byte lastMode = -1;
    private static byte lastFrequency = -1;
    public static byte commandCount = 0x00;


    private static final byte[][] commands= new byte[0xff][];

    /**
     * 包编码
     */
    public static byte[] encode(byte address, byte energy,
                                byte mode, byte command, byte frequency) {
        // 计算状态改变字节
        byte changeByte = BYTE_2;
        if (lastEnergy != energy) {
            changeByte = ByteUtils.setBit(changeByte, (byte) 0, true);
            lastEnergy = energy;
        }
        if (lastMode != mode) {
            changeByte = ByteUtils.setBit(changeByte, (byte) 1, true);
            lastMode = mode;
        }
        if (lastSwitch != command) {
            changeByte = ByteUtils.setBit(changeByte, (byte) 2, true);
            lastSwitch = command;
        }
        if (lastFrequency != frequency) {
            changeByte = ByteUtils.setBit(changeByte, (byte) 3, true);
            lastFrequency = frequency;
        }

        byte[] array;
        array = encode_1_3(address, energy, mode, command, frequency, changeByte);
//        array = encode_1_3_test_single_to_android(address, energy, mode, command, frequency, changeByte);
        Log.d("Protocol------Encode", bytesToHexString(array).toUpperCase());
        return array;
    }

    private static byte[] encode_1_3_test_single_to_android(byte handle, byte energy,
                                                            byte mode, byte command,
                                                            byte frequency, byte changeByte) {

        // 协议总共14字节
        ByteBuffer buffer = ByteBuffer.allocate(14);

        ByteBuffer crcData = ByteBuffer.allocate(9);
        crcData.put(SINGLE_CHIP_TO_ANDROID);//单片机主动发消息给安卓屏
        crcData.put(USE_COMMAND);//标识为使用情况指令
        crcData.put(commandCount);//指令编号，0x00-0xff自增
        crcData.put(handle); // 1字节手柄选择
        crcData.put((byte)0x01); // 使用次数，单位次
        crcData.put((byte)0x00); // 使用时长，单位s
        crcData.put((byte)28); // 手柄状态1(温度℃)
        crcData.put((byte)16); // 手柄状态 2（流量 L/min）
        crcData.put((byte)6);//手柄状态3（离子Ω.cm）

        buffer.put(HEAD).put(SINGLE_CHIP_TO_ANDROID).put(USE_COMMAND).put(commandCount)
                .put(handle).put((byte)0x01).put((byte)0x00).put((byte)28).put((byte)16).put((byte)6)
                .put(buildCRC(crcData.array()))
                .put(TAIL);
        return buffer.array();
    }

    private static byte[] encode_1_3(byte handle, byte energy,
                                    byte mode, byte command,
                                    byte frequency, byte changeByte) {
        // 协议总共14字节
        ByteBuffer buffer = ByteBuffer.allocate(14);

        ByteBuffer crcData = ByteBuffer.allocate(9);
        crcData.put(ANDROID_TO_SINGLE_CHIP);//安卓屏主动发消息给单片机
//        crcData.put(SINGLE_CHIP_ACK_ANDROID);//单片机回应  demo
        crcData.put(CONTROL_COMMAND);//标识为控制指令
        crcData.put(commandCount);//指令编号，0x00-0xff自增
        ByteBuffer data = ByteBuffer.allocate(6);
        data.put(handle); // 1字节手柄选择
        data.put(changeByte); // 状态改变
        data.put(energy); // 1字节能量
        data.put(mode); // 1字节模式
        data.put(command); // 1字节开关
        data.put(frequency);//1字节频率值
        crcData.put(data.array());

//AA55 Android_To Control 01++ HANDLE changeByte energy mode switch frequency CYC 55AA
        buffer.put(HEAD).put(crcData.array())
                .put(buildCRC(crcData.array()))
                .put(TAIL);

        commands[(commandCount&0xff)] =data.array();
        return buffer.array();
    }

    public static void nextCommand() {
        commandCount = (byte) ((commandCount + 1) % 0xff);
    }

    public static byte[] encode(byte energy, byte frequency,byte command,byte mode) {
        return encode(HANDLE, energy, mode, command, frequency);
    }

    /**
     * 安卓主动构建一份应答包给单片机
     * @return 指令字节数组
     */
    public static byte[] buildACK(byte command,byte commandCount,byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocate(8+data.length);
        ByteBuffer crcData = ByteBuffer.allocate(3+data.length);
//HEAD Android_ACK Control_Command commandCount data CYC TAIL
        crcData.put(ANDROID_ACK_SINGLE_CHIP);
        crcData.put(command);
        crcData.put(commandCount);
        crcData.put(data);
        buffer.put(HEAD).put(crcData.array()).put(buildCRC(crcData.array())).put(TAIL);

        return buffer.array();
    }
    /**
     * 安卓主动构建一份应答包给单片机
     * @return 指令字节数组
     */
    public static byte[] buildACK(byte[] hexArr) {
        return buildACK(hexArr[3],hexArr[4],Arrays.copyOfRange(hexArr,5,hexArr.length-3));
    }

    private static byte buildCRC(byte[] hexArr){
        int res = hexArr[0] & 0xff;
        for (int i = 1; i < hexArr.length; i++) {
            res = res ^ (hexArr[i] & 0xff);
        }
        return (byte) res;
    }
    public static boolean checkCRC(String hexArr){
        byte[] bytes = hexStringToBytes(hexArr);
        if(bytes == null){
            return false;
        }
        return checkCRC(bytes);
    }

    public static boolean checkCRC(byte[] hexArr){
//        byte b = buildCRC(Arrays.copyOfRange(hexArr, 2, hexArr.length - 3));
//        byte b1= (byte)(hexArr[hexArr.length - 3] & 0xff);
//        Log.d("check","b=="+b+"      hexArr[hexArr.length - 3] & 0xff="+b1);
//        return b1 == b;
        return true;
    }

    public static boolean checkACKCommand(byte[] hexArr){
//        int commandCount = hexArr[4]&0xff;
//        byte[] a2 = Arrays.copyOfRange(hexArr, 5, hexArr.length-3);
//        byte[] command = commands[commandCount];
//        Log.d("check",""+Arrays.toString(a2)+"     "+Arrays.toString(command));
//        return Arrays.equals(command, a2);
        return true;
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
    /**
     * 16进制字符串转换成字节数组
     */
    public static byte[] hexStringToBytes(String hexStr) {
        hexStr = hexStr.replace(" ", "");
        int len = hexStr.length();
        if(!(len%2 == 0)){
            return null;
        }
        int num = len/2;
        byte[] para = new byte[num];
        for (int i = 0; i < num; i++) {
            int value = Integer.valueOf(hexStr.substring(i * 2, 2 * (i + 1)), 16);
            para[i] = (byte) value;
        }
        return para;
    }

}
