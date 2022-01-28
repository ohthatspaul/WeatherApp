package com.example.paul_weather_task.Utility;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.paul_weather_task.Data.AirQualityData;
import com.example.paul_weather_task.Data.WeatherForecast;
import com.example.paul_weather_task.Data.WeatherNow;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class WeatherApiUtil {
    public static final String API_KEY="1430c759f1094b74a78a88026f1e0833";

    public interface OnAirQualityFinished{
        public void onFinished(AirQualityData data);
    }


    public interface OnWeatherForecastFinished{
        public void onFinished(WeatherForecast data);
    }

    public interface OnWeatherNowFinished{
        public void onFinished(WeatherNow weatherNow);
    }

    public static void getAirQualityData(final Activity activity, String weather_id,final OnAirQualityFinished listener){
        String url = String.format("https://free-api.heweather.net/s6/air/now?location=%s&key=%s&lang=en",weather_id,API_KEY);
        HttpUtil.sendOKHttpGetAsync(activity, url, new HttpUtil.SimpleCallback() {
            @Override
            public void onFailure(IOException e) {
                showToast(activity,e.toString());
                listener.onFinished(null);
            }

            @Override
            public void onFailure2(IllegalArgumentException e) {
                showToast(activity,"Invalid Url");
                listener.onFinished(null);

            }

            @Override
            public void onResponse(String response) {
                if(!TextUtils.isEmpty(response)){
                    try{
                        JSONArray heWeather6 = new JSONObject(response).getJSONArray("HeWeather6");
                        String s =heWeather6.get(0).toString();
                        AirQualityData data = new Gson().fromJson(s,AirQualityData.class);
                        listener.onFinished(data);
                    }catch (JSONException e){
                        e.printStackTrace();
                        listener.onFinished(null);
                    }
                }else {
                    listener.onFinished(null);
                }

            }
        });

    }

    public static void getWeatherForecast(final Activity activity, String weather_id,final OnWeatherForecastFinished listener) {
        String url = String.format("https://free-api.heweather.net/s6/weather/forecast?location=%s&key=%s&lang=en",weather_id,API_KEY);
        HttpUtil.sendOKHttpGetAsync(activity, url, new HttpUtil.SimpleCallback() {
            @Override
            public void onFailure(IOException e) {
                showToast(activity,e.toString());
                listener.onFinished(null);
            }

            @Override
            public void onFailure2(IllegalArgumentException e) {
                showToast(activity,"Invalid Url");
                listener.onFinished(null);

            }

            @Override
            public void onResponse(String response) {
                if(!TextUtils.isEmpty(response)){
                    try{
                        JSONArray heWeather6 = new JSONObject(response).getJSONArray("HeWeather6");
                        String s =heWeather6.get(0).toString();
                        WeatherForecast data = new Gson().fromJson(s,WeatherForecast.class);
                        listener.onFinished(data);
                    }catch (JSONException e){
                        e.printStackTrace();
                        listener.onFinished(null);
                    }
                }else {
                    listener.onFinished(null);
                }

            }
        });


    }

    public static void getWeatherNow(final Activity activity, String weather_id, final OnWeatherNowFinished listener){
        String url = String.format("https://free-api.heweather.net/s6/weather/now?location=%s&key=%s&lang=en",weather_id,API_KEY);
        Log.d("Get Request", "getWeatherNow: "+url);
        HttpUtil.sendOKHttpGetAsync(activity, url, new HttpUtil.SimpleCallback() {
            @Override
            public void onFailure(IOException e) {
                showToast(activity,e.toString());
                listener.onFinished(null);
            }

            @Override
            public void onFailure2(IllegalArgumentException e) {
                showToast(activity,"Invalid Url");

            }

            @Override
            public void onResponse(String response) {
                if(!TextUtils.isEmpty(response)){
                    try {
                        JSONArray heWeather6  = new JSONObject(response).getJSONArray("HeWeather6");
                        String s = heWeather6.get(0).toString();
                        WeatherNow data = new Gson().fromJson(s,WeatherNow.class);
                        listener.onFinished(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFinished(null);
                    }

                }else{
                    listener.onFinished(null);
                }

            }
        });
    }

    private static void showToast(Activity activity, String string) {
        Toast.makeText(activity,string,Toast.LENGTH_LONG).show();
    }
}
