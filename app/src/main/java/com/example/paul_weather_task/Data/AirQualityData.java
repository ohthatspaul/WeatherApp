package com.example.paul_weather_task.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AirQualityData {
    public Basic basic;
    public Update update;
    public String status;
    @SerializedName("air_now_city")
    public AirNowCity airNowCity;
    @SerializedName("air_now_station")
    List<AirNowStation> airNowStationList;

    @Override
    public String toString() {
        return "AirQualityData{" +
                "basic=" + basic +
                ", update=" + update +
                ", status='" + status + '\'' +
                ", airNowCity=" + airNowCity +
                ", airNowStationList=" + airNowStationList +
                '}';
    }
}
