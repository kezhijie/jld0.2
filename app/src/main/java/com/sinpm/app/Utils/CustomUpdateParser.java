package com.sinpm.app.Utils;

import com.alibaba.fastjson.JSON;
import com.sinpm.app.beans.UpdateBean;
import com.xuexiang.xupdate.entity.UpdateEntity;
import com.xuexiang.xupdate.listener.IUpdateParseCallback;
import com.xuexiang.xupdate.proxy.IUpdateParser;

public class CustomUpdateParser implements IUpdateParser {
    @Override
    public UpdateEntity parseJson(String json) throws Exception {

        UpdateBean updateBean = JSON.parseObject(json).getObject("data", UpdateBean.class);

        if (updateBean.getUpgradeDTO() != null) {
            return new UpdateEntity()
                    .setHasUpdate(true)
                    .setForce(updateBean.getUpgradeDTO().getForce() == 1)
                    .setIsIgnorable(updateBean.getUpgradeDTO().getForce() == 0)
                    .setVersionName(updateBean.getUpgradeDTO().getVersion())
                    .setUpdateContent(updateBean.getUpgradeDTO().getDescription())
                    .setDownloadUrl(updateBean.getUpgradeDTO().getDownloadUrl());
        } else {
            return null;
        }


    }

    @Override
    public void parseJson(String json, IUpdateParseCallback callback) {

    }

    @Override
    public boolean isAsyncParser() {
        return false;
    }
}
