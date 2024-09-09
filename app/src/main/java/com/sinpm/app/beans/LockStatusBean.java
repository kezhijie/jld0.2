package com.sinpm.app.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LockStatusBean {

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
        @SerializedName("unLockList")
        private List<UnLockListDTO> unLockList;
        @SerializedName("needActive")
        private Integer needActive;
        @SerializedName("customerLogin")
        private Integer customerLogin;
        @SerializedName("storeId")
        private Integer storeId;
        @SerializedName("brandId")
        private Integer brandId;
        @SerializedName("stages")
        private Integer stages;
        @SerializedName("isActive")
        private Boolean isActive;
        @SerializedName("isBind")
        private Boolean isBind;
        @SerializedName("useTime")
        private Integer useTime;
        @SerializedName("remainingCount")
        private Integer remainingCount;
        @SerializedName("bindQrcode")
        private Object bindQrcode;
        @SerializedName("exMsg")
        private Object exMsg;
        @SerializedName("debug")
        private Boolean debug;

        public List<UnLockListDTO> getUnLockList() {
            return unLockList;
        }

        public void setUnLockList(List<UnLockListDTO> unLockList) {
            this.unLockList = unLockList;
        }

        public Integer getNeedActive() {
            return needActive;
        }

        public void setNeedActive(Integer needActive) {
            this.needActive = needActive;
        }

        public Integer getCustomerLogin() {
            return customerLogin;
        }

        public void setCustomerLogin(Integer customerLogin) {
            this.customerLogin = customerLogin;
        }

        public Integer getStoreId() {
            return storeId;
        }

        public void setStoreId(Integer storeId) {
            this.storeId = storeId;
        }

        public Integer getBrandId() {
            return brandId;
        }

        public void setBrandId(Integer brandId) {
            this.brandId = brandId;
        }

        public Integer getStages() {
            return stages;
        }

        public void setStages(Integer stages) {
            this.stages = stages;
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }

        public Boolean getIsBind() {
            return isBind;
        }

        public void setIsBind(Boolean isBind) {
            this.isBind = isBind;
        }

        public Integer getUseTime() {
            return useTime;
        }

        public void setUseTime(Integer useTime) {
            this.useTime = useTime;
        }

        public Integer getRemainingCount() {
            return remainingCount;
        }

        public void setRemainingCount(Integer remainingCount) {
            this.remainingCount = remainingCount;
        }

        public Object getBindQrcode() {
            return bindQrcode;
        }

        public void setBindQrcode(Object bindQrcode) {
            this.bindQrcode = bindQrcode;
        }

        public Object getExMsg() {
            return exMsg;
        }

        public void setExMsg(Object exMsg) {
            this.exMsg = exMsg;
        }

        public Boolean getDebug() {
            return debug;
        }

        public void setDebug(Boolean debug) {
            this.debug = debug;
        }

        public static class UnLockListDTO {
            @SerializedName("type")
            private Integer type;
            @SerializedName("name")
            private String name;

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
        }
    }
}
