package com.sinpm.app.base;

import java.util.ArrayList;
import java.util.List;

public class ProcessInfo {
    private Integer processIndex;
    private List<TextInfo> textInfoDefault;
    private List<TextInfo> textInfoTip;
    private List<TextInfo> textInfoError;

    public List<TextInfo> getTextInfoTip() {
        return textInfoTip;
    }

    public void setTextInfoTip(List<TextInfo> textInfoTip) {
        this.textInfoTip = textInfoTip;
    }

    public List<TextInfo> getTextInfoError() {
        return textInfoError;
    }

    public void setTextInfoError(List<TextInfo> textInfoError) {
        this.textInfoError = textInfoError;
    }

    public ProcessInfo() {
        this.textInfoTip = new ArrayList<>();
        this.textInfoError = new ArrayList<>();
        this.textInfoDefault = new ArrayList<>();
    }


    public List<TextInfo> getTextInfoDefault() {
        return textInfoDefault;
    }

    public void setTextInfoDefault(List<TextInfo> textInfoDefault) {
        this.textInfoDefault = textInfoDefault;
    }

    public Integer getProcessIndex() {
        return processIndex;
    }

    public void setProcessIndex(Integer processIndex) {
        this.processIndex = processIndex;
    }
}
