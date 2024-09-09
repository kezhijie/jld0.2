package com.sinpm.app.Utils;

import static com.xuexiang.xupdate.utils.UpdateUtils.startActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;


import com.sinpm.app.base.ActivityManager;

import java.util.HashMap;
import java.util.Locale;

/**
 * 功能描述：修改app内部的语言工具类
 */
public class LanguageUtil {

    /*语言类型：
     * 此处支持3种语言类型，更多可以自行添加。
     * */
//    private static final String ENGLISH = "en";
//    private static final String CHINESE = "zh";

    private static final HashMap<String, Locale> languagesList = new HashMap<String, Locale>(3) {{
        put(Locale.ENGLISH.getLanguage(), Locale.ENGLISH);
        put(Locale.CHINESE.getLanguage(), Locale.CHINESE);
    }};

    /**
     * 修改语言
     *
     * @param activity 上下文
     * @param locale   例如修改为 英文传Locale.ENGLISH，参考上文字符串常量
     */
    public static void changeAppLanguage(Activity activity, Locale locale) {

        Resources resources = activity.getResources();
        Configuration configuration = resources.getConfiguration();
        // app locale 默认英文
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);
        Log.e("Log", "设置的语言：" + locale.getLanguage());
        //finish();
        //加载动画
        //activity.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        //activity.overridePendingTransition(0, 0);

        PropertiesUtils.setValue(Constants.localOld, PropertiesUtils.getValue(Constants.locale, Locale.CHINESE.getLanguage()));
        PropertiesUtils.setValue(Constants.locale, locale.getLanguage());
    }


    /**
     * 修改语言
     *
     * @param activity 上下文
     * @param
     */
    public static void refreshAppLanguage(Activity activity, Class target) {
        String oldLanguage = PropertiesUtils.getValue(Constants.localOld, Locale.CHINESE.getLanguage());
        String newLanguage = PropertiesUtils.getValue(Constants.locale, Locale.CHINESE.getLanguage());
        if (oldLanguage.equals(newLanguage)) {
            activity.finish();
            return;
        }
        Locale locale = Locale.CHINESE;
        if (newLanguage.equals(Locale.ENGLISH.getLanguage())) {
            locale = Locale.ENGLISH;
        }

        Intent intent = new Intent(activity, target);
        switchLanguage(activity, locale, intent);
    }

    public static void switchLanguage(Activity activity, Locale locale, Intent intent) {
        Resources resources = activity.getResources();
        Configuration configuration = resources.getConfiguration();
        // app locale 默认英文
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);
        Log.e("Log", "设置的语言：" + locale.getLanguage());
        //finish();
        //加载动画
        //activity.overridePendingTransition(R.anim.anim_right_in, R.anim.anim_left_out);
        //activity.overridePendingTransition(0, 0);
        ActivityManager.finishActivity(intent.getClass());

        startActivity(intent);
        PropertiesUtils.setValue(Constants.locale, locale.getLanguage());
    }


    /**
     * 获取指定语言的locale信息，如果指定语言不存在
     * 返回本机语言，如果本机语言不是语言集合中的一种，返回英语
     */
    public static Locale getLocaleByLanguage() {
        String language = String.valueOf(PropertiesUtils.getValue(Constants.locale, Locale.CHINESE.getLanguage()));
        if (isContainsKeyLanguage(language)) {
            return languagesList.get(language);
        } else {
            Locale locale = Locale.getDefault();
            for (String key : languagesList.keySet()) {
                if (TextUtils.equals(languagesList.get(key).getLanguage(), locale.getLanguage())) {
                    return locale;
                }
            }
        }
        return Locale.ENGLISH;
    }


    /**
     * 如果此映射包含指定键的映射关系，则返回 true
     */
    private static boolean isContainsKeyLanguage(String language) {
        return languagesList.containsKey(language);
    }

}
 