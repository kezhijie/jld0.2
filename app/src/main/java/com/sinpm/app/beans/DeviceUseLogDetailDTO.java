package com.sinpm.app.beans;

public class DeviceUseLogDetailDTO {
    /**
     * 项目id
     */
    private Integer projectId;
    /**
     * 项目名字
     */
    private String projectName;

    /**
     * 模式
     */
    private String model;
    /**
     * 发数、次数
     */
    private Integer times;
    /**
     * 强度
     */
    private Integer strength;

    /**
     * 强度区间最小值
     */
    private Integer strengthMin;

    /**
     * 强度区间最大值
     */
    private Integer strengthMax;

    private Integer duration;

    public DeviceUseLogDetailDTO(Integer projectId, String projectName, String model, Integer times, Integer strength, Integer strengthMin, Integer strengthMax, Integer duration) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.model = model;
        this.times = times;
        this.strength = strength;
        this.strengthMin = strengthMin;
        this.strengthMax = strengthMax;
        this.duration = duration;
    }

    public DeviceUseLogDetailDTO() {
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }


    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }

    public Integer getStrength() {
        return strength;
    }

    public void setStrength(Integer strength) {
        this.strength = strength;
    }

    public Integer getStrengthMin() {
        return strengthMin;
    }

    public void setStrengthMin(Integer strengthMin) {
        this.strengthMin = strengthMin;
    }

    public Integer getStrengthMax() {
        return strengthMax;
    }

    public void setStrengthMax(Integer strengthMax) {
        this.strengthMax = strengthMax;
    }

}
