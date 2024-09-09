package com.sinpm.app.Utils;

public class CRC16Util {
    private static final int POLYNOMIAL = 0x8005;
    private static final int PRESET_VALUE = 0xFFFF;

    /**
     * crc算法
     * @param input 参数，不包含空格，例如AABB
     * @return
     */
    public static String calculateCRC16(String input) {
        int crc = 0xFFFF;

        for (int i = 0; i < input.length(); i += 2) {
            String hexByte = input.substring(i, i + 2);
            int dataByte = Integer.parseInt(hexByte, 16);
            crc ^= dataByte & 0xFF;

            for (int j = 0; j < 8; j++) {
                if ((crc & 0x0001) != 0) {
                    crc = (crc >>> 1) ^ 0xA001;
                } else {
                    crc >>>= 1;
                }
            }
        }

        return String.format("%04X", crc);
    }

    public static void main(String[] args) {
        System.out.printf("111");
    }

    public static int calculateCRC16(byte[] data) {
        int crc = PRESET_VALUE;

        for (byte b : data) {
            crc ^= (b << 8);

            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ POLYNOMIAL;
                } else {
                    crc <<= 1;
                }
            }
        }

        return crc & 0xFFFF;
    }

    
    public static byte[] hexStringToByteArray(String hexString) {
        int length = hexString.length();
        byte[] byteArray = new byte[length / 2];
        
        for (int i = 0; i < length; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                                      + Character.digit(hexString.charAt(i + 1), 16));
        }
        
        return byteArray;
    }
}
