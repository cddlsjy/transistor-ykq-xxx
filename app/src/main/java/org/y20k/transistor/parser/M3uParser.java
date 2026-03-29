package org.y20k.transistor.parser;

import org.y20k.transistor.model.RadioStation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class M3uParser {
    // 解析本地M3U文件
    public static List<RadioStation> parseLocalM3u(String filePath) {
        File m3uFile = new File(filePath);
        if (!m3uFile.exists() || !m3uFile.isFile()) {
            return null;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(m3uFile))) {
            return parseM3uContent(br);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 解析M3U字符串（内置默认内容）
    public static List<RadioStation> parseM3uString(String m3uContent) {
        try (BufferedReader br = new BufferedReader(new StringReader(m3uContent))) {
            return parseM3uContent(br);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 核心解析逻辑（通用）
    private static List<RadioStation> parseM3uContent(BufferedReader br) throws Exception {
        List<RadioStation> stationList = new ArrayList<>();
        String line;
        String name = null;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("#EXTINF:")) {
                // 解析电台名称：截取#EXTINF:-1,后的内容
                name = line.substring(line.indexOf(",") + 1);
            } else if (!line.startsWith("#") && !line.isEmpty()) {
                // 解析电台链接：非注释、非空行即为播放地址
                if (name != null && !name.isEmpty()) {
                    stationList.add(new RadioStation(name, line));
                    name = null;
                }
            }
        }
        return stationList;
    }
}