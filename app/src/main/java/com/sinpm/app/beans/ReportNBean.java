package com.sinpm.app.beans;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportNBean {
    private String traceId;
    private String code;
    private String msg;
    private String timestamp;
    private Data data;
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
    public String getTraceId() {
        return traceId;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getTimestamp() {
        return timestamp;
    }

    public void setData(Data data) {
        this.data = data;
    }
    public Data getData() {
        return data;
    }

    public static class ReportGsList {

        private int creator;
        private String creatorName;
        private int modifier;
        private String modifierName;
        private String createTime;
        private String updateTime;
        private int id;
        private int reportId;
        private int propertyId;
        private String propertyName;
        private int storeId;
        private String storeName;
        private String imgOrigin;
        private String imgFlag;
        private int score;
        private String optionId;
        private String optionName;
        private String recommendIds;
        private String recommendNames;
        private String recommendData;
        private String advice;
        private String status;
        public void setCreator(int creator) {
            this.creator = creator;
        }
        public int getCreator() {
            return creator;
        }

        public void setCreatorName(String creatorName) {
            this.creatorName = creatorName;
        }
        public String getCreatorName() {
            return creatorName;
        }

        public void setModifier(int modifier) {
            this.modifier = modifier;
        }
        public int getModifier() {
            return modifier;
        }

        public void setModifierName(String modifierName) {
            this.modifierName = modifierName;
        }
        public String getModifierName() {
            return modifierName;
        }

        public void setCreateTime(Long createTime) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            this.createTime = simpleDateFormat.format(new Date(createTime));
        }
        public String getCreateTime() {
            return createTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
        public String getUpdateTime() {
            return updateTime;
        }

        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

        public void setReportId(int reportId) {
            this.reportId = reportId;
        }
        public int getReportId() {
            return reportId;
        }

        public void setPropertyId(int propertyId) {
            this.propertyId = propertyId;
        }
        public int getPropertyId() {
            return propertyId;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }
        public String getPropertyName() {
            return propertyName;
        }

        public void setStoreId(int storeId) {
            this.storeId = storeId;
        }
        public int getStoreId() {
            return storeId;
        }

        public void setStoreName(String storeName) {
            this.storeName = storeName;
        }
        public String getStoreName() {
            return storeName;
        }

        public void setImgOrigin(String imgOrigin) {
            this.imgOrigin = imgOrigin;
        }
        public String getImgOrigin() {
            return imgOrigin;
        }

        public void setImgFlag(String imgFlag) {
            this.imgFlag = imgFlag;
        }
        public String getImgFlag() {
            return imgFlag;
        }

        public void setScore(int score) {
            this.score = score;
        }
        public int getScore() {
            return score;
        }

        public void setOptionId(String optionId) {
            this.optionId = optionId;
        }
        public String getOptionId() {
            return optionId;
        }

        public void setOptionName(String optionName) {
            this.optionName = optionName;
        }
        public String getOptionName() {
            return optionName;
        }

        public void setRecommendIds(String recommendIds) {
            this.recommendIds = recommendIds;
        }
        public String getRecommendIds() {
            return recommendIds;
        }

        public void setRecommendNames(String recommendNames) {
            this.recommendNames = recommendNames;
        }
        public String getRecommendNames() {
            return recommendNames;
        }

        public void setRecommendData(String recommendData) {
            this.recommendData = recommendData;
        }
        public String getRecommendData() {
            return recommendData;
        }

        public void setAdvice(String advice) {
            this.advice = advice;
        }
        public String getAdvice() {
            return advice;
        }

        public void setStatus(String status) {
            this.status = status;
        }
        public String getStatus() {
            return status;
        }

    }
    public static class DeviceCustomer {

        private int creator;
        private String creatorName;
        private int modifier;
        private String modifierName;
        private String createTime;
        private String updateTime;
        private int id;
        private int userId;
        private String userNick;
        private int memberId;
        private String memberName;
        private int storeId;
        private int deviceId;
        private String code;
        private int modelId;
        private String userPhone;
        private String userHead;
        private String startTime;
        private String endTime;
        private String reportId;
        public void setCreator(int creator) {
            this.creator = creator;
        }
        public int getCreator() {
            return creator;
        }

        public void setCreatorName(String creatorName) {
            this.creatorName = creatorName;
        }
        public String getCreatorName() {
            return creatorName;
        }

        public void setModifier(int modifier) {
            this.modifier = modifier;
        }
        public int getModifier() {
            return modifier;
        }

        public void setModifierName(String modifierName) {
            this.modifierName = modifierName;
        }
        public String getModifierName() {
            return modifierName;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
        public String getCreateTime() {
            return createTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
        public String getUpdateTime() {
            return updateTime;
        }

        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
        public int getUserId() {
            return userId;
        }

        public void setUserNick(String userNick) {
            this.userNick = userNick;
        }
        public String getUserNick() {
            return userNick;
        }

        public void setMemberId(int memberId) {
            this.memberId = memberId;
        }
        public int getMemberId() {
            return memberId;
        }

        public void setMemberName(String memberName) {
            this.memberName = memberName;
        }
        public String getMemberName() {
            return memberName;
        }

        public void setStoreId(int storeId) {
            this.storeId = storeId;
        }
        public int getStoreId() {
            return storeId;
        }

        public void setDeviceId(int deviceId) {
            this.deviceId = deviceId;
        }
        public int getDeviceId() {
            return deviceId;
        }

        public void setCode(String code) {
            this.code = code;
        }
        public String getCode() {
            return code;
        }

        public void setModelId(int modelId) {
            this.modelId = modelId;
        }
        public int getModelId() {
            return modelId;
        }

        public void setUserPhone(String userPhone) {
            this.userPhone = userPhone;
        }
        public String getUserPhone() {
            return userPhone;
        }

        public void setUserHead(String userHead) {
            this.userHead = userHead;
        }
        public String getUserHead() {
            return userHead;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }
        public String getStartTime() {
            return startTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
        public String getEndTime() {
            return endTime;
        }

        public void setReportId(String reportId) {
            this.reportId = reportId;
        }
        public String getReportId() {
            return reportId;
        }

    }
    public static class Data {

        private int creator;
        private String creatorName;
        private String modifier;
        private String modifierName;
        private String createTime;
        private String updateTime;
        private int id;
        private int customerId;
        private int userId;
        private String userNick;
        private String userOpenId;
        private String phone;
        private int memberId;
        private String memberName;
        private int storeId;
        private String storeName;
        private String partsId;
        private String partsName;
        private String beautician;
        private int modelId;
        private String deviceCode;
        private int score;
        private int status;
        private int subscribeMsg;
        private String advices;
        private String key;
        private String searchDate;
        private String recommend;
        private List<String> adviceList;
        private List<RecommendData> recommendData;
        private List<ReportGsList> reportGsList;
        private DeviceCustomer deviceCustomer;
        public void setCreator(int creator) {
            this.creator = creator;
        }
        public int getCreator() {
            return creator;
        }

        public void setCreatorName(String creatorName) {
            this.creatorName = creatorName;
        }
        public String getCreatorName() {
            return creatorName;
        }

        public void setModifier(String modifier) {
            this.modifier = modifier;
        }
        public String getModifier() {
            return modifier;
        }

        public void setModifierName(String modifierName) {
            this.modifierName = modifierName;
        }
        public String getModifierName() {
            return modifierName;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
        public String getCreateTime() {
            return createTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
        public String getUpdateTime() {
            return updateTime;
        }

        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

        public void setCustomerId(int customerId) {
            this.customerId = customerId;
        }
        public int getCustomerId() {
            return customerId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
        public int getUserId() {
            return userId;
        }

        public void setUserNick(String userNick) {
            this.userNick = userNick;
        }
        public String getUserNick() {
            return userNick;
        }

        public void setUserOpenId(String userOpenId) {
            this.userOpenId = userOpenId;
        }
        public String getUserOpenId() {
            return userOpenId;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
        public String getPhone() {
            return phone;
        }

        public void setMemberId(int memberId) {
            this.memberId = memberId;
        }
        public int getMemberId() {
            return memberId;
        }

        public void setMemberName(String memberName) {
            this.memberName = memberName;
        }
        public String getMemberName() {
            return memberName;
        }

        public void setStoreId(int storeId) {
            this.storeId = storeId;
        }
        public int getStoreId() {
            return storeId;
        }

        public void setStoreName(String storeName) {
            this.storeName = storeName;
        }
        public String getStoreName() {
            return storeName;
        }

        public void setPartsId(String partsId) {
            this.partsId = partsId;
        }
        public String getPartsId() {
            return partsId;
        }

        public void setPartsName(String partsName) {
            this.partsName = partsName;
        }
        public String getPartsName() {
            return partsName;
        }

        public void setBeautician(String beautician) {
            this.beautician = beautician;
        }
        public String getBeautician() {
            return beautician;
        }

        public void setModelId(int modelId) {
            this.modelId = modelId;
        }
        public int getModelId() {
            return modelId;
        }

        public void setDeviceCode(String deviceCode) {
            this.deviceCode = deviceCode;
        }
        public String getDeviceCode() {
            return deviceCode;
        }

        public void setScore(int score) {
            this.score = score;
        }
        public int getScore() {
            return score;
        }

        public void setStatus(int status) {
            this.status = status;
        }
        public int getStatus() {
            return status;
        }

        public void setSubscribeMsg(int subscribeMsg) {
            this.subscribeMsg = subscribeMsg;
        }
        public int getSubscribeMsg() {
            return subscribeMsg;
        }

        public void setAdvices(String advices) {
            this.advices = advices;
        }
        public String getAdvices() {
            return advices;
        }

        public void setKey(String key) {
            this.key = key;
        }
        public String getKey() {
            return key;
        }

        public void setSearchDate(String searchDate) {
            this.searchDate = searchDate;
        }
        public String getSearchDate() {
            return searchDate;
        }

        public void setRecommend(String recommend) {
            this.recommend = recommend;
        }
        public String getRecommend() {
            return recommend;
        }

        public void setAdviceList(List<String> adviceList) {
            this.adviceList = adviceList;
        }
        public List<String> getAdviceList() {
            return adviceList;
        }

        public void setRecommendData(List<RecommendData> recommendData) {
            this.recommendData = recommendData;
        }
        public List<RecommendData> getRecommendData() {
            return recommendData;
        }

        public void setReportGsList(List<ReportGsList> reportGsList) {
            this.reportGsList = reportGsList;
        }
        public List<ReportGsList> getReportGsList() {
            return reportGsList;
        }

        public void setDeviceCustomer(DeviceCustomer deviceCustomer) {
            this.deviceCustomer = deviceCustomer;
        }
        public DeviceCustomer getDeviceCustomer() {
            return deviceCustomer;
        }

    }
    public static class RecommendData {

        private int type;
        private int id;
        private String name;

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        private String img;
        public void setType(int type) {
            this.type = type;
        }
        public int getType() {
            return type;
        }

        public void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

    }
}
