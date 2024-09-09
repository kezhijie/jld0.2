package com.sinpm.app.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProcessData {
    private static Map<Integer, ProcessInfo> processInfoMap;

    public static ProcessInfo getProcess(Integer process) {
        ProcessInfo processInfo = processInfoMap.get(process);
        if (processInfo == null) {
            processInfo = new ProcessInfo();
            processInfo.setProcessIndex(process);
            processInfo.setTextInfoError(new ArrayList<>());
            processInfo.setTextInfoDefault(new ArrayList<>());
            processInfo.setTextInfoTip(new ArrayList<>());
        }
        return processInfo;
    }

    public static ProcessInfo addDefault(Integer process, TextInfo... textInfos) {
        ProcessInfo processInfo = processInfoMap.get(process);
        if (processInfo == null) {
            processInfo = new ProcessInfo();
        }
        processInfo.setTextInfoDefault(Arrays.asList(textInfos));
        processInfo.setTextInfoError(processInfo.getTextInfoDefault());
        processInfo.setTextInfoTip(processInfo.getTextInfoDefault());
        processInfoMap.put(process, processInfo);
        return processInfo;
    }

    public static ProcessInfo addTip(Integer process, TextInfo... textInfos) {
        ProcessInfo processInfo = processInfoMap.get(process);
        if (processInfo == null) {
            processInfo = new ProcessInfo();
        }
        processInfo.setTextInfoError(Arrays.asList(textInfos));
        processInfoMap.put(process, processInfo);
        return processInfo;
    }

    public static ProcessInfo addError(Integer process, TextInfo... textInfos) {
        ProcessInfo processInfo = processInfoMap.get(process);
        if (processInfo == null) {
            processInfo = new ProcessInfo();
        }
        processInfo.setTextInfoError(Arrays.asList(textInfos));
        processInfoMap.put(process, processInfo);
        return processInfo;
    }

    static {
        processInfoMap = new HashMap<>();
        ProcessData.addDefault(1, new TextInfo("請按此处進行測試"));
        ProcessData.addTip(1,
                new TextInfo("測試時槍口請勿", true),
                new TextInfo("朝向人", true),
                new TextInfo("按兩下右鍵完成"),
                new TextInfo("測試")
        );

        ProcessData.addDefault(2, new TextInfo("按此處开始定位"), new TextInfo("請稍候"));
        ProcessData.addError(2, new TextInfo("打開投遞器上蓋"), new TextInfo("檢查有無異物", true), new TextInfo("排除後請按此處"));

        ProcessData.addDefault(3, new TextInfo("装入針筒並蓋上"), new TextInfo("上蓋後請按此處"));
        ProcessData.addTip(3, new TextInfo("請確認針筒", true), new TextInfo("是否安装正確", true), new TextInfo("並按此處繼續"));


        ProcessData.addDefault(4, new TextInfo("請按此进行試打並扣"), new TextInfo("兩下板機完成試打"));

    }


}
