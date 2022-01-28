package com.example.paul_weather_task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.paul_weather_task.Data.City;
import com.example.paul_weather_task.Data.CityAdapter;
import com.example.paul_weather_task.db.CityDatabase;

import java.util.ArrayList;

public class CityListFragment extends Fragment {

    ListView lv;
    ArrayList<City> cityArrayList = new ArrayList<>();
    private static final String KEY_CITY_SET="city_set";
    private static final String KEY_CITY_COUNT="city_count";
    private static final int REQUEST_CODE=1;
    Button footBt;
    CityAdapter adapter;
    private static final String TAG = "CityListFragment";

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        new MenuInflater(getContext()).inflate(R.menu.citylist_opt_menu,menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenu.ContextMenuInfo menuInfo = item.getMenuInfo();
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = adapterContextMenuInfo.position;
        switch(item.getItemId()){
            case R.id.opt_menu_delete:
                cityArrayList.remove(position);
                adapter.notifyDataSetChanged();
                break;

            default:break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: getting the city");
        if(requestCode==REQUEST_CODE){
            if(resultCode== Activity.RESULT_OK){
                City city = SelectCityActivity.getCityByIntent(data);
                if(city!=null&&!cityArrayList.contains(city)){
                    cityArrayList.add(city);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_city_list,null,false);
        View footView = inflater.inflate(R.layout.footview,null,false);
        footBt = footView.findViewById(R.id.foot_bt_add);
        footBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent (getContext(),SelectCityActivity.class);
                startActivityForResult(i,REQUEST_CODE);
            }
        });
        lv = v.findViewById(R.id.city_list_lv);
        lv.addFooterView(footView);
        adapter=new CityAdapter(getContext(),cityArrayList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                City city = cityArrayList.get(i);
                WeatherActivity activity = (WeatherActivity) getActivity();
                activity.drawerLayout.closeDrawers();
                activity.updateData(city);
            }
        });
        registerForContextMenu(lv);
        loadCityList();

        return v;
    }

    private void saveCityList(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_CITY_COUNT,cityArrayList.size());
        for (int i = 0; i < cityArrayList.size() ; i++) {
            String key_id = String.format("%s%d", KEY_CITY_SET,i);
            editor.putInt(key_id,cityArrayList.get(i).getId());

        }
        editor.apply();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveCityList();

    }

    private void loadCityList() {
        CityDatabase cityDatabase = new CityDatabase(getActivity());
        cityDatabase.open();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        int count = sharedPreferences.getInt(KEY_CITY_COUNT,0);
        cityArrayList.clear();
        for (int i = 0; i < count; i++) {
            String key_id = String.format("%s%d",KEY_CITY_SET,i);
            int id = sharedPreferences.getInt(key_id,0);
            City city = cityDatabase.queryCityById(id,2);
            if(city!=null){
                cityArrayList.add(city);
                adapter.notifyDataSetChanged();

            }
        }
        cityDatabase.close();
    }


}
