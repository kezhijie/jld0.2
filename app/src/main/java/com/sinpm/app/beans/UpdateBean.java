package com.sinpm.app.beans;

public class UpdateBean {

    private boolean isUpgraded;
    private UpgradeDTO upgradeDTO;

    public boolean isUpgraded() {
        return isUpgraded;
    }

    public void setUpgraded(boolean upgraded) {
        isUpgraded = upgraded;
    }

    public UpgradeDTO getUpgradeDTO() {
        return upgradeDTO;
    }

    public void setUpgradeDTO(UpgradeDTO upgradeDTO) {
        this.upgradeDTO = upgradeDTO;
    }

    public static class UpgradeDTO {

        private String creator;
        private String creatorName;
        private String modifier;
        private String modifierName;
        private String createTime;
        private String updateTime;
        private int id;
        private String appName;
        private String version;
        private int force;
        private int deviceModelId;
        private String deviceModelName;
        private int brandId;
        private String brandName;
        private int publish;
        private String downloadUrl;
        private String description;
        private int disable;
        public void setCreator(String creator) {
            this.creator = creator;
        }
        public String getCreator() {
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

        public void setAppName(String appName) {
            this.appName = appName;
        }
        public String getAppName() {
            return appName;
        }

        public void setVersion(String version) {
            this.version = version;
        }
        public String getVersion() {
            return version;
        }

        public void setForce(int force) {
            this.force = force;
        }
        public int getForce() {
            return force;
        }

        public void setDeviceModelId(int deviceModelId) {
            this.deviceModelId = deviceModelId;
        }
        public int getDeviceModelId() {
            return deviceModelId;
        }

        public void setDeviceModelName(String deviceModelName) {
            this.deviceModelName = deviceModelName;
        }
        public String getDeviceModelName() {
            return deviceModelName;
        }

        public void setBrandId(int brandId) {
            this.brandId = brandId;
        }
        public int getBrandId() {
            return brandId;
        }

        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }
        public String getBrandName() {
            return brandName;
        }

        public void setPublish(int publish) {
            this.publish = publish;
        }
        public int getPublish() {
            return publish;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }
        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDescription(String description) {
            this.description = description;
        }
        public String getDescription() {
            return description;
        }

        public void setDisable(int disable) {
            this.disable = disable;
        }
        public int getDisable() {
            return disable;
        }

    }

}
