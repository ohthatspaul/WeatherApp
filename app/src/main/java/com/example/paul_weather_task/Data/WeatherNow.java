package com.example.paul_weather_task.Data;

public class WeatherNow {
    public Basic basic;
    public Update update;
    public String status;
    public Now now;

    @Override
    public String toString() {
        return "WeatherNow{" +
                "basic=" + basic +
                ", update=" + update +
                ", status='" + status + '\'' +
                ", now=" + now +
                '}';
    }
}
