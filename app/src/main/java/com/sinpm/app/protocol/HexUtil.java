package com.sinpm.app.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * @title: 转化工具类
 * @Discription: 转化字节为16进制 方便调试看日志信息
 * @author： yanmi
 * @date： 2021/5/11 9:51
 * @version：V 1.0.0
 * @Company: 湖南华凯网络科技有限公司
 */
public class HexUtil {


    /**
     * 字节数组转16进制字符 显示日志加空格
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexStringToLog(byte[] bytes) {
        String result = "";
        try {
            for (int i = 0; i < bytes.length; i++) {
                String hexString = Integer.toHexString(bytes[i] & 0xFF);
                if (hexString.length() == 1) {
                    hexString = '0' + hexString;
                }
                result += hexString.toUpperCase();
            }

            StringBuffer last = new StringBuffer();
            String temp = result;
            int num = temp.length() / 2;
            for (int j = 0; j < num; j++) {
                if (j == 0) {
                    last.append(temp.substring(0, 2));
                } else {
                    last.append(temp.substring(j * 2, j * 2 + 2));
                }
                last.append(" ");
            }
            return last.toString();
        } catch (Exception e) {
            return "指令数据字节数组转16进制字符，可能存在异常";
        }


    }


    /**
     * 字节数组转16进制字符
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexString(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }


    /**
     * 十六进制数转字节数组
     *
     * @param s
     * @return
     */
    public static byte[] hexStringToByteArray(String s) {
        if (s.length() % 2 != 0) {
            StringBuilder stringBuilder = new StringBuilder(s);
            stringBuilder.insert(s.length() - 1, "0");
            s = stringBuilder.toString();
        }
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    /**
     * int转十六进制数
     *
     * @param
     * @return
     */
    public static String intToHex(int n) {
        String temp = Integer.toHexString(n);
        if (n < 16) {
            return "0" + temp;
        } else {
            return temp;
        }

    }

    /**
     * int转十六进制数
     *
     * @param
     * @return
     */
    public static String intToHex(int n, int length) {
        String temp = Integer.toHexString(n);
        String zero = "0";
        for (int i = 0; i < length - 1; i++) {
            zero += "0";
        }
        return (zero.substring(0, 4 - temp.length()) + temp).toUpperCase();

    }

    /**
     * 十六进转int
     *
     * @param
     * @return
     */
    public static int hexToInt(String content) {
        int number = 0;
        String[] HighLetter = {"A", "B", "C", "D", "E", "F"};
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i <= 9; i++) {
            map.put(i + "", i);
        }
        for (int j = 10; j < HighLetter.length + 10; j++) {
            map.put(HighLetter[j - 10], j);
        }
        String[] str = new String[content.length()];
        for (int i = 0; i < str.length; i++) {
            str[i] = content.substring(i, i + 1);
        }
        for (int i = 0; i < str.length; i++) {
            number += map.get(str[i]) * Math.pow(16, str.length - 1 - i);
        }
        return number;

    }


}
