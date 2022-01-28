package com.example.paul_weather_task.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherForecast {
    public Basic basic;
    public Update update;
    public String status;
    @SerializedName("daily_forecast")
    public List<DailyForecast> dailyForecastList;

    @Override
    public String toString() {
        return "WeatherForecast{" +
                "basic=" + basic +
                ", update=" + update +
                ", status='" + status + '\'' +
                ", dailyForecastList=" + dailyForecastList +
                '}';
    }
}
