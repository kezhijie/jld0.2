package com.sinpm.app.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public enum ViewEnum {

    operate("ll_operate", "工作流程"),
    unlock("ll_unlock", "解锁"),
    working("ll_working", "施打"),
    stop("ll_stop", "停止"),


    ;

    private String id;
    private String desc;

    public String getId() {
        return id;
    }


    public String getDesc() {
        return desc;
    }


    ViewEnum(String id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public static List<String> getIdList() {
        List<String> ids=new ArrayList<>();
        for (ViewEnum value : ViewEnum.values()) {
            ids.add(value.getId());
        }
        return ids;
    }

    public static ViewEnum getById(String id) {
        ViewEnum result = null; // 假设YourEnum是你的枚举类型名称
        for (ViewEnum e : ViewEnum.values()) {
            if (Objects.equals(e.id, id)) {
                result = e;
                break;
            }
        }

        if (result == null) {

            result = ViewEnum.unlock;
        }
        return result;
    }
}
