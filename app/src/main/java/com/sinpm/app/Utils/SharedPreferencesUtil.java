package com.sinpm.app.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

    //存储的sharedpreferences文件名
    private static final String FILE_NAME = "save_file_name";
    public static void clearData(Context context){
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
    /**
     * 保存数据到文件
     * @param context
     * @param key
     * @param data
     */
    public static void saveData(Context context, String key, Object data){

        String type = data.getClass().getSimpleName();
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if ("Integer".equals(type)){
            editor.putInt(key, (Integer)data);
        }else if ("Boolean".equals(type)){
            editor.putBoolean(key, (Boolean)data);
        }else if ("String".equals(type)){
            editor.putString(key, (String)data);
        }else if ("Float".equals(type)){
            editor.putFloat(key, (Float)data);
        }else if ("Long".equals(type)){
            editor.putLong(key, (Long)data);
        }

        editor.commit();
    }

    /**
     * 从文件中读取数据
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static Object getDataCache(Context context, String key, Object defValue){

        String type = defValue.getClass().getSimpleName();
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (FILE_NAME, Context.MODE_PRIVATE);

        //defValue为为默认值，如果当前获取不到数据就返回它
        if ("Integer".equals(type)){
            return sharedPreferences.getInt(key, (Integer)defValue);
        }else if ("Boolean".equals(type)){
            return sharedPreferences.getBoolean(key, (Boolean)defValue);
        }else if ("String".equals(type)){
            return sharedPreferences.getString(key, (String)defValue);
        }else if ("Float".equals(type)){
            return sharedPreferences.getFloat(key, (Float)defValue);
        }else if ("Long".equals(type)){
            return sharedPreferences.getLong(key, (Long)defValue);
        }

        return null;
    }

    // 泛型支持的重载方法
    public static <T> T getData(Context context, String key, T defaultValue) {
        // 确保defaultValue不是基础类型，因为基础类型不能作为泛型参数
        if (defaultValue == null) {
            throw new IllegalArgumentException("defaultValue cannot be null for generic method.");
        }
        // 利用类型判断将泛型参数转为基础类型对象，再调用原方法
        if (defaultValue instanceof Integer) {
            return (T) Integer.valueOf((Integer) getDataCache(context, key, defaultValue));
        } else if (defaultValue instanceof Boolean) {
            return (T) Boolean.valueOf((Boolean) getDataCache(context, key, defaultValue));
        } else if (defaultValue instanceof String) {
            return (T) getDataCache(context, key, defaultValue);
        } else if (defaultValue instanceof Float) {
            return (T) Float.valueOf((Float) getDataCache(context, key, defaultValue));
        } else if (defaultValue instanceof Long) {
            return (T) Long.valueOf((Long) getDataCache(context, key, defaultValue));
        }
        return defaultValue;
    }

    public static void remove(Context context, String key){


        SharedPreferences sharedPreferences = context
                .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove(key).commit();
    }


}