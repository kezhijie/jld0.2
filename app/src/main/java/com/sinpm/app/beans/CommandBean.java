package com.sinpm.app.beans;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

public class CommandBean {
    private String content;
    private Integer msgType;
    private content contentObj;

    public CommandBean() {
        this.contentObj = new content();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        init();

    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public CommandBean.content getContentObj() {
        return contentObj;
    }

    private void init() {
        this.contentObj = JSON.parseObject(content, contentObj.getClass());
        this.contentObj.init();
    }

    public static class content {
        private String command;
        private Long commandId;
        private Integer commandType;
        private Long creator;
        private String creatorName;
        private List<String> deviceCodes;
        private String data;

        public Map<String, Object> getDataObj() {
            return dataObj;
        }

        public void init() {
            this.dataObj = JSONObject.parseObject(data);
        }

        private Map<String, Object> dataObj;

        public content() {
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }


        public void setCommand(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }

        public void setCommandId(Long commandId) {
            this.commandId = commandId;
        }

        public Long getCommandId() {
            return commandId;
        }

        public void setCommandType(Integer commandType) {
            this.commandType = commandType;
        }

        public Integer getCommandType() {
            return commandType;
        }

        public void setCreator(Long creator) {
            this.creator = creator;
        }

        public Long getCreator() {
            return creator;
        }

        public void setCreatorName(String creatorName) {
            this.creatorName = creatorName;
        }

        public String getCreatorName() {
            return creatorName;
        }

        public void setDeviceCodes(List<String> deviceCodes) {
            this.deviceCodes = deviceCodes;
        }

        public List<String> getDeviceCodes() {
            return deviceCodes;
        }
    }
}

