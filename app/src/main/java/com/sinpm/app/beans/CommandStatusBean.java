package com.sinpm.app.beans;

import com.google.gson.annotations.SerializedName;

public class CommandStatusBean {

    @SerializedName("traceId")
    private String traceId;
    @SerializedName("code")
    private String code;
    @SerializedName("msg")
    private String msg;
    @SerializedName("timestamp")
    private String timestamp;
    @SerializedName("data")
    private DataDTO data;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public static class DataDTO {
        @SerializedName("creator")
        private Integer creator;
        @SerializedName("creatorName")
        private String creatorName;
        @SerializedName("modifier")
        private Object modifier;
        @SerializedName("modifierName")
        private Object modifierName;
        @SerializedName("createTime")
        private String createTime;
        @SerializedName("updateTime")
        private Object updateTime;
        @SerializedName("id")
        private Integer id;
        @SerializedName("commandId")
        private Integer commandId;
        @SerializedName("deviceCode")
        private String deviceCode;
        @SerializedName("deviceId")
        private Object deviceId;
        @SerializedName("command")
        private String command;
        @SerializedName("content")
        private String content;
        @SerializedName("type")
        private Integer type;
        @SerializedName("result")
        private String result;
        @SerializedName("status")
        private Integer status;
        @SerializedName("executed")
        private Integer executed;
        @SerializedName("disable")
        private Integer disable;

        public Integer getCreator() {
            return creator;
        }

        public void setCreator(Integer creator) {
            this.creator = creator;
        }

        public String getCreatorName() {
            return creatorName;
        }

        public void setCreatorName(String creatorName) {
            this.creatorName = creatorName;
        }

        public Object getModifier() {
            return modifier;
        }

        public void setModifier(Object modifier) {
            this.modifier = modifier;
        }

        public Object getModifierName() {
            return modifierName;
        }

        public void setModifierName(Object modifierName) {
            this.modifierName = modifierName;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public Object getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Object updateTime) {
            this.updateTime = updateTime;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getCommandId() {
            return commandId;
        }

        public void setCommandId(Integer commandId) {
            this.commandId = commandId;
        }

        public String getDeviceCode() {
            return deviceCode;
        }

        public void setDeviceCode(String deviceCode) {
            this.deviceCode = deviceCode;
        }

        public Object getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(Object deviceId) {
            this.deviceId = deviceId;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Integer getExecuted() {
            return executed;
        }

        public void setExecuted(Integer executed) {
            this.executed = executed;
        }

        public Integer getDisable() {
            return disable;
        }

        public void setDisable(Integer disable) {
            this.disable = disable;
        }
    }
}
