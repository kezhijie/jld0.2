package com.sinpm.app.beans;

import java.util.List;

public class DeviceUnLockDTO {

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DeviceUnLockDTO> getChildren() {
        return children;
    }

    public void setChildren(List<DeviceUnLockDTO> children) {
        this.children = children;
    }

    private Integer type;
    private String name;
    private List<DeviceUnLockDTO> children;

}
