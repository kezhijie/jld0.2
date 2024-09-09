package com.sinpm.app.Utils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.sinpm.app.base.BaseApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 外部文件存储
 */
public class PropertiesUtils {
    static String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
    static String fileName = Constants.modelCode + ".properties";
    static File file = new File(filePath, fileName);
    private static Properties properties;

    //    单例获取
    public static synchronized Properties getPropertie() {
        if (properties == null) {
            if (isExsitProperties()) {
                properties = loadExistProperties();
            } else {
                properties = newPropertie();
            }
        }
        return properties;
    }

    //新建
    private static synchronized Properties newPropertie() {
        Properties properties = new Properties();
        try {
            File fileDir = new File(filePath);
            fileDir.mkdirs();
            file.createNewFile();
            InputStream inputStream = new FileInputStream(file);
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    //判断文件是否存在
    private static boolean isExsitProperties() {
        return file.exists();
    }

    //加载已经存在的properties
    private static Properties loadExistProperties() {

        Properties properties = new Properties();
        try {
            InputStream inputStream = new FileInputStream(file);
            properties.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

    //    设值
    public synchronized static void setValue(String key, String value) {
        SharedPreferencesUtil.saveData(BaseApplication.getInstance(), key, value);
        Log.e("setValue", key + "/" + value);
        String baseKey = Base64.encodeToString(key.getBytes(), Base64.DEFAULT);
        String baseValue = Base64.encodeToString(value.getBytes(), Base64.DEFAULT);
        getPropertie().setProperty(baseKey, baseValue);
        savePropertie();
    }

    //    取值
    public static String getValue(String key, String defaultValue) {
        String spValue = (String) SharedPreferencesUtil.getData(BaseApplication.getInstance(), key, defaultValue);
        String baseKey = Base64.encodeToString(key.getBytes(), Base64.DEFAULT);
        String value = getPropertie().getProperty(baseKey, defaultValue);
        String baseValue = "";
        Log.d("getValue", key + "/" + spValue);
        if (!value.equals(defaultValue)) {
            baseValue = new String(Base64.decode(value.getBytes(), Base64.DEFAULT));
        }

        if (spValue.equals(baseValue)) {//如果两个值一样
            return baseValue;
        } else {
            if (TextUtils.isEmpty(spValue)) {//只要sp里的值不为空 就以sp为主
                setValue(key, baseValue);
                return baseValue;
            } else {
                setValue(key, spValue);
                return spValue;
            }
        }


    }

    //    保存
    private static void savePropertie() {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            getPropertie().store(outputStream, "save data");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                    outputStream.flush();
                    outputStream.getFD().sync();
                } catch (IOException exception) {

                }
            }
        }
    }

    public static void cleanProFile() {
        SharedPreferencesUtil.clearData(BaseApplication.getInstance());
        properties.clear();
        if (isExsitProperties()) {
            file.delete();
        }
    }
}
