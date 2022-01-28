package com.example.paul_weather_task.Data;

import com.google.gson.annotations.SerializedName;

public class Basic {
    public String cid;
    public String location;
    public String parent_city;
    public String admin_area;
    @SerializedName("cnty")
    public String country;
    public String lat;
    public String lon;
    @SerializedName("tz")
    public String timeZone;

    @Override
    public String toString() {
        return "Basic{" +
                "cid='" + cid + '\'' +
                ", location='" + location + '\'' +
                ", parent_city='" + parent_city + '\'' +
                ", admin_area='" + admin_area + '\'' +
                ", country='" + country + '\'' +
                ", lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                ", timeZone='" + timeZone + '\'' +
                '}';
    }
}
