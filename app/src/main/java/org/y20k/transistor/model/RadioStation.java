package org.y20k.transistor.model;

public class RadioStation {
    private String stationName; // 电台名称
    private String playUrl;     // 播放地址

    public RadioStation(String stationName, String playUrl) {
        this.stationName = stationName;
        this.playUrl = playUrl;
    }

    // getter/setter
    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }
}