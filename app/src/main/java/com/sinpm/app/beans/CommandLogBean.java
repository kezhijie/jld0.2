package com.sinpm.app.beans;

public class CommandLogBean {

    private Long commandId;
    private String deviceCode;
    private String deviceId;
    private String command;
    private String content;
    private int type;
    private String result;
    private int status;
    private int executed;

    public void setCommandId(Long commandId) {
        this.commandId = commandId;
    }
    public Long getCommandId() {
        return commandId;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }
    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getDeviceId() {
        return deviceId;
    }

    public void setCommand(String command) {
        this.command = command;
    }
    public String getCommand() {
        return command;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getContent() {
        return content;
    }

    public void setType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }

    public void setResult(String result) {
        this.result = result;
    }
    public String getResult() {
        return result;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }

    public void setExecuted(int executed) {
        this.executed = executed;
    }
    public int getExecuted() {
        return executed;
    }

    @Override
    public String toString() {
        return "CommandLogBean{" +
                "commandId=" + commandId +
                ", deviceCode='" + deviceCode + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", command='" + command + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", result='" + result + '\'' +
                ", status=" + status +
                ", executed=" + executed +
                '}';
    }
}
