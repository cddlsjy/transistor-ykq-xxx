package org.y20k.transistor.constant;

public class JsonConstants {
    // 本地 JSON 文件路径
    public static final String LOCAL_JSON_PATH = "/sdcard/station/station.json";

    // 内置默认电台的 JSON 字符串（兼容 Transistor 的 collection.json 格式）
    public static final String DEFAULT_JSON_CONTENT = "[\n" +
            "  {\n" +
            "    \"name\": \"BBC World Service\",\n" +
            "    \"streamUris\": [\"http://stream.live.vc.bbcmedia.co.uk/bbc_world_service\"],\n" +
            "    \"image\": \"\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"Classic FM (UK)\",\n" +
            "    \"streamUris\": [\"http://media-ice.musicradio.com/ClassicFM\"],\n" +
            "    \"image\": \"\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"Radio Paradise (Main Mix)\",\n" +
            "    \"streamUris\": [\"https://stream.radioparadise.com/mp3-128\"],\n" +
            "    \"image\": \"\"\n" +
            "  }\n" +
            "]";
}