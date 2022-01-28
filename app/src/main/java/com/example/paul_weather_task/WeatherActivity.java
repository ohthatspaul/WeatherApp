package com.example.paul_weather_task;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.paul_weather_task.Data.AirQualityData;
import com.example.paul_weather_task.Data.City;
import com.example.paul_weather_task.Data.DailyForecast;
import com.example.paul_weather_task.Data.WeatherForecast;
import com.example.paul_weather_task.Data.WeatherNow;
import com.example.paul_weather_task.Utility.WeatherApiUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WeatherActivity extends AppCompatActivity {
    TextView tv_city,tv_update_time,tv_temp,tv_weather_info,tv_aqi,tv_pm25;
    ImageView iv_cond;
    String weather_id = "CN101210701";
    LinearLayout forecastLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    public static final int CITY_REQ_CODE=0;
    public static final String KEY_WEATHER_ID="weather_id";
    DrawerLayout drawerLayout;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CITY_REQ_CODE&&resultCode== Activity.RESULT_OK){
            weather_id=SelectCityActivity.getWeatherIdByIntent(data);
            updateData();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        drawerLayout=findViewById(R.id.drawer_layout);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        forecastLayout = findViewById(R.id.forecast_layout);
        tv_aqi = findViewById(R.id.aqi_text);
        tv_pm25 = findViewById(R.id.pm25_text);
        tv_city = findViewById(R.id.title_city_tv);
        tv_update_time = findViewById(R.id.title_pub_time_tv);
        tv_temp = findViewById(R.id.now_temp_tv);
        tv_weather_info = findViewById(R.id.now_cond_tv);
        iv_cond = findViewById(R.id.now_cond_iv);
        Button nav_bt = findViewById(R.id.nav_button);
        nav_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
            }
        });
        tv_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (WeatherActivity.this,SelectCityActivity.class);
                startActivityForResult(i,CITY_REQ_CODE);
            }
        });
        saveWeatherId();

        updateData();


    }

    private void saveWeatherId() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_WEATHER_ID,weather_id);
        editor.apply();
        //editor.commit();
    }

    AtomicInteger requestCount = new AtomicInteger(0);
    private void updateData() {
        swipeRefreshLayout.setRefreshing(true);
        requestCount.set(0);
        updateWeatherNow();
        updateWeatherForecast();

        updateWeatherAqi();
    }
    private void updateRefreshState(){
        if(requestCount.incrementAndGet()==3){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void updateWeatherAqi() {
        WeatherApiUtil.getAirQualityData(this, weather_id, new WeatherApiUtil.OnAirQualityFinished() {
            @Override
            public void onFinished(AirQualityData data) {
                updateRefreshState();
                if(data!=null&&data.status.equalsIgnoreCase("ok")){
                    tv_aqi.setText(data.airNowCity.aqi);
                    tv_pm25.setText(data.airNowCity.pm25);
                }else{
                    tv_aqi.setText("---");
                    tv_pm25.setText("---");
                }
            }
        });
    }

    private void updateWeatherForecast() {
        WeatherApiUtil.getWeatherForecast(this, weather_id, new WeatherApiUtil.OnWeatherForecastFinished() {
            @Override
            public void onFinished(WeatherForecast data) {
                updateRefreshState();
                if(data!=null&&data.status.equalsIgnoreCase("ok")){
                    forecastLayout.removeAllViews();
                    List<DailyForecast> forecastList = data.dailyForecastList;
                    for (int i = 0; i < forecastList.size() ; i++) {
                        DailyForecast f = forecastList.get(i);
                        View v = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item,null,false);
                        TextView item_date_text = v.findViewById(R.id.item_date_text);
                        TextView item_max_text = v.findViewById(R.id.item_max_text);
                        TextView item_min_text = v.findViewById(R.id.item_min_text);
                        ImageView item_iv_day_con = v.findViewById(R.id.item_iv_day_con);
                        ImageView item_iv_night_con = v.findViewById(R.id.item_iv_night_con);
                        item_date_text.setText(f.date);
                        item_max_text.setText(f.tmp_max+"°C");
                        item_min_text.setText(f.tmp_min+"°C");
                        updateWeatherIcon(f.cond_code_d,item_iv_day_con);
                        updateWeatherIcon(f.cond_code_n,item_iv_night_con);
                        forecastLayout.addView(v);

                    }
                }
            }
        });
    }

    private void updateWeatherNow() {
        WeatherApiUtil.getWeatherNow(WeatherActivity.this, weather_id, new WeatherApiUtil.OnWeatherNowFinished() {
            @Override
            public void onFinished(WeatherNow weatherNow) {
                updateRefreshState();
                if(weatherNow!=null&&weatherNow.status.equalsIgnoreCase("ok")){
                    Log.d("WeatherData", "onFinished: "+weatherNow.toString());
                    tv_city.setText(weatherNow.basic.location);
                    tv_update_time.setText(weatherNow.update.loc);
                    tv_temp.setText(weatherNow.now.tmp+"°C");
                    tv_weather_info.setText(weatherNow.now.cond_txt);
                    updateWeatherIcon(weatherNow.now.cond_code,iv_cond);

                }
            }
        });
    }

    private void updateWeatherIcon(String code, ImageView iv) {
        String url = String.format("https://cdn.heweather.com/cond_icon/%s.png",code);
        Glide.with(this).load(Uri.parse(url)).into(iv);
    }

    public void updateData(City city) {
        weather_id=city.getWeather_id();
        saveWeatherId();
        updateData();
    }


}
