package com.sinpm.app.beans;

public class UpdateTimeEvent {
    public UpdateTimeEvent(Integer releaseTime) {
        this.releaseTime = releaseTime;
    }

    public UpdateTimeEvent() {
    }

    public Integer getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Integer releaseTime) {
        this.releaseTime = releaseTime;
    }

    private Integer releaseTime;
}
