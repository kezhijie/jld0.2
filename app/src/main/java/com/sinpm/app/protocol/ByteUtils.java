package com.sinpm.app.protocol;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author Legend
 * @data by on 20-4-10.
 * @description 字节操作工具类
 */
public class ByteUtils {

    /**
     * 获取字节b的第index位
     * @param b
     * @param index
     * @return
     */
    public static int getBit(byte b, int index) {
        return (b >> index) & 0x1;
    }

    public static byte setBit(byte b, byte index, boolean val) {
        if (val) {
            b = (byte) (b | (1 << index));
        } else {
            b = (byte) (b & ~(1 << index));
        }
        return b;
    }
    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        if (TextUtils.isEmpty(base64Data)) {
            return null;
        }
        try {
            base64Data = URLDecoder.decode(base64Data, "UTF-8");
            base64Data = base64Data.replace("data:image/jpeg;base64,", "");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
//        return decodeSampledBitmapFromResource(bytes, 60, 60);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
