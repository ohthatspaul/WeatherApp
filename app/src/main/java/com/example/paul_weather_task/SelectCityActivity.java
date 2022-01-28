package com.example.paul_weather_task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.paul_weather_task.Data.City;
import com.example.paul_weather_task.Data.CityAdapter;
import com.example.paul_weather_task.db.CityDatabase;
import com.example.paul_weather_task.db.GenerateDatabaseTask;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SelectCityActivity extends AppCompatActivity {

    ListView lv;
    List<City> mainList = new ArrayList<City>();
    CityAdapter adapter;
    int level=0;
    int parentId=-1;
    CityDatabase cityDatabase;
    SearchView searchView;
    Toolbar toolbar;
    int currentId=1;
    public static final String KEY_WEATHER_ID="weather_id";




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        MenuItem item = menu.findItem(R.id.toolbar_search);
        searchView= (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cityDatabase.fuzzyQueryCityListAsync(newText, new CityDatabase.OnQueryFinished() {
                    @Override
                    public void onFinished(List<City> list) {
                        updateListView(list);
                    }
                });
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        lv =  findViewById(R.id.listView);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Select");
        adapter = new CityAdapter(SelectCityActivity.this,mainList);
        lv.setAdapter(adapter);
        cityDatabase=new CityDatabase(SelectCityActivity.this);
        cityDatabase.open();
        getCityListFromDb(parentId,level);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = adapter.getItem(position);
                level=city.getLevel();
                toolbar.setTitle(city.toString());
                parentId=city.getParentId();
                currentId=city.getId();
                if(level<=1){
                    level++;
                    parentId=city.getParentId();
                    getCityListFromDb(currentId,level);
                }else{
                    Intent i = getIntent();
                    i.putExtra(KEY_WEATHER_ID,city.getWeather_id());

                    i.putExtra("CITY", (Parcelable) city);

                    Log.d("SelectCityActivity", "Weather ID: "+city.getWeather_id());
                    setResult(Activity.RESULT_OK,i);
                    finish();
                }

            }
        });


    }

    public static City getCityByIntent(Intent intent) {
        return intent.getParcelableExtra("CITY");
    }

    public static String getWeatherIdByIntent(Intent intent){
        return intent.getStringExtra(KEY_WEATHER_ID);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbar_back:
                backDeal();

                break;
            case R.id.toolbar_download:
                generateDatabase();
            case R.id.toolbar_close:
                Intent i = new Intent (SelectCityActivity.this,WeatherActivity.class);
                startActivity(i);

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void backDeal() {
        try {
            searchView.setQuery("",false);
            Method method = searchView.getClass().getDeclaredMethod("onCloseClicked");
            method.setAccessible(true);
            method.invoke(searchView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(level>0){
            level--;
        }if(level==0){
            parentId=-1;
            toolbar.setTitle("China");
        }else{
            City city=cityDatabase.queryCityById(parentId,level);
            parentId=city.getParentId();
            currentId=city.getId();
            City province = cityDatabase.queryCityById(parentId, level - 1);
            toolbar.setTitle(province.toString());
        }
        getCityListFromDb(parentId,level);
    }

    private void getCityListFromDb(int parentId, int level) {
        Log.d("test", "parentID: " + parentId + " ;level: " + level);

        if(level==0){
            cityDatabase.queryAllProvincesAsync(new CityDatabase.OnQueryFinished() {
                @Override
                public void onFinished(List<City> list) {
                    updateListView(list);
                }
            });
        }else {
            cityDatabase.queryCityListByParentIdAsync(parentId,level, new CityDatabase.OnQueryFinished() {
                @Override
                public void onFinished(List<City> list) {
                    updateListView(list);
                }
            });
        }
    }

    private void updateListView(List<City> list0){
        mainList.clear();
        if(list0!=null){
            mainList.addAll(list0);
        }
        adapter.notifyDataSetChanged();
        lv.setSelection(0);
    }
    public void showToast(String s){
        Toast.makeText(SelectCityActivity.this,s,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cityDatabase.close();
    }
    private void generateDatabase(){
        new GenerateDatabaseTask(this, cityDatabase, new GenerateDatabaseTask.OnTaskFinishedListener() {
            @Override
            public void onFinished(List<City> list0) {
                updateListView(list0);
            }
        }).execute();
    }
}
