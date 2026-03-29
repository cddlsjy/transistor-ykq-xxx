package org.y20k.transistor.constant;

public class RadioConstant {
    // 本地M3U文件路径
    public static final String LOCAL_M3U_PATH = "/sdcard/station/station.m3u";
    // 内置默认M3U内容（兼容标准M3U格式）
    public static final String DEFAULT_M3U_CONTENT = "#EXTM3U\n" +
            "#EXTINF:-1,AsiaFM高清音乐台\n" +
            "http://asiafm.hk:8000/asiahd\n" +
            "\n" +
            "#EXTINF:-1,AsiaFM亚洲经典台\n" +
            "http://goldfm.cn:8000/goldfm\n" +
            "\n" +
            "#EXTINF:-1,AsiaFM 亚洲热歌台【2023.10.17】\n" +
            "http://hot.asiafm.net:8000/asiafm\n" +
            "\n" +
            "#EXTINF:-1,AsiaFM 亚洲音乐台【2023.10.18】\n" +
            "http://asiafm.hk:8000/asiafm";
}