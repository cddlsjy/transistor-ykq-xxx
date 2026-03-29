package org.y20k.transistor.helpers;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.y20k.transistor.core.Station;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonHelper {

    private static final String TAG = "JsonHelper";

    /**
     * 解析本地 JSON 文件
     * @param filePath 文件路径
     * @return  Station 列表，失败返回 null
     */
    public static List<Station> parseLocalJson(String filePath) {
        File jsonFile = new File(filePath);
        if (!jsonFile.exists() || !jsonFile.isFile()) {
            return null;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(jsonFile))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return parseJsonString(sb.toString());
        } catch (IOException e) {
            Log.e(TAG, "读取本地 JSON 文件失败", e);
            return null;
        }
    }

    /**
     * 解析 JSON 字符串
     * @param jsonString 符合 collection.json 格式的字符串
     * @return Station 列表
     */
    public static List<Station> parseJsonString(String jsonString) {
        List<Station> stationList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String name = obj.optString("name", "未知电台");
                JSONArray urisArray = obj.optJSONArray("streamUris");
                if (urisArray != null && urisArray.length() > 0) {
                    String url = urisArray.getString(0);
                    // 直接构造 Station 对象
                    Station tempStation = new Station(name, url);
                    stationList.add(tempStation);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "解析 JSON 字符串失败", e);
        }
        return stationList;
    }
}