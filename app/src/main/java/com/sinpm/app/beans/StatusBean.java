package com.sinpm.app.beans;

import java.util.List;

public class StatusBean {
    private List<UnLockList> unLockList;
    private Integer needActive;
    /**
     *
     * 用户登录方式,0:无需登录,1:强制登录,2:支持游客登录
     */
    private Integer customerLogin;//用户登录方式,0:无需登录,1:强制登录,2:支持游客登录
    private Integer storeId;
    private Integer brandId;
    private Integer stages;
    private Boolean isActive;
    private Boolean isBind;
    private Integer useTime;
    private Integer remainingCount;
    private Boolean debug;


    private String  bindQrcode;


    public String getBindQrcode() {
        return bindQrcode;
    }

    public void setBindQrcode(String bindQrcode) {
        this.bindQrcode = bindQrcode;
    }
    public Integer getRemainingCount() {
        return remainingCount;
    }

    public void setRemainingCount(Integer remainingCount) {
        this.remainingCount = remainingCount;
    }
    public void setUnLockList(List<UnLockList> unLockList) {
        this.unLockList = unLockList;
    }
    public List<UnLockList> getUnLockList() {
        return unLockList;
    }

    public void setNeedActive(int needActive) {
        this.needActive = needActive;
    }
    public int getNeedActive() {
        return needActive;
    }

    public void setCustomerLogin(Integer customerLogin) {
        this.customerLogin = customerLogin;
    }
    public Integer getCustomerLogin() {
        return customerLogin;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }
    public int getStoreId() {
        return storeId;
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }
    public int getBrandId() {
        return brandId;
    }

    public void setStages(int stages) {
        this.stages = stages;
    }
    public int getStages() {
        return stages;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    public boolean getIsActive() {
        return isActive;
    }

    public void setIsBind(boolean isBind) {
        this.isBind = isBind;
    }
    public boolean getIsBind() {
        return isBind;
    }

    public void setUseTime(Integer useTime) {
        this.useTime = useTime;
    }
    public Integer getUseTime() {
        return useTime;
    }
    public class UnLockList {

        private int type;
        private String name;
        public void setType(int type) {
            this.type = type;
        }
        public int getType() {
            return type;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

    }
}
