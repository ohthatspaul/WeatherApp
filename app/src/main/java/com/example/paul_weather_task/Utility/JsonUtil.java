package com.example.paul_weather_task.Utility;

import androidx.annotation.NonNull;

import com.example.paul_weather_task.Data.City;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
    public static final String CITY_ID="id";
    public static final String CITY_NAME="name";
    public static final String CITY_WEATHER_ID="weather_id";


    public static List<City> getCityListFromJson(String s, int parentId, int level) {
        List<City> list = new ArrayList<City>();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(s);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                City city = getCityFromJson(parentId, jsonObject,level);
                list.add(city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    public static List<City> getCountyListFromJson(String s, int parentId, int level) {
        List<City> list = new ArrayList<City>();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(s);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                City city = getCityFromJson(parentId, jsonObject,level);
                String weatherId = jsonObject.getString(CITY_WEATHER_ID);
                city.setWeather_id(weatherId);
                list.add(city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }
    @NonNull
    private static City getCityFromJson(int parentId, JSONObject jsonObject, int level) throws JSONException {
        int id = jsonObject.getInt(CITY_ID);
        String name = jsonObject.getString(CITY_NAME);
        City city = new City(id, name);
        String enName = PinyinUtil.ToPinyin(name);
        String initialName = PinyinUtil.ToPinyinFirstLetter(name);
        city.setEnName(enName);
        city.setInitialName(initialName);
        city.setParentId(parentId);
        city.setLevel(level);
        return city;
    }
}

