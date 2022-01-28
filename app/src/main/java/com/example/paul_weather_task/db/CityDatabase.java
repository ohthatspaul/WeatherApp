package com.example.paul_weather_task.db;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.paul_weather_task.Data.City;

import java.util.ArrayList;
import java.util.List;

public class CityDatabase {
    public static final String KEY_ID = "mid";
    public static final String KEY_PID = "pid";
    public static final String KEY_NAME = "name";
    public static final String KEY_WEATHER_ID = "weather_id";
    public static final String KEY_EN_NAME = "en_name";
    public static final String KEY_INI_NAME = "ini_name";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_LOOK_UP = "key_look_up";
    public static final String DB_NAME = "citydb.db";
    public static final String CITY_TABLE = "city";
    private Activity context;
    private int version = 1;
    private SQLiteDatabase db;
    DatabaseHelper databaseHelper;

    public interface OnQueryFinished{
        public void onFinished(List<City> list);
    }
    interface LoaderWork{
        List<City> queryWork();
    }

    private void asyncLoader(final OnQueryFinished listener,final LoaderWork work){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<City> list = work.queryWork();
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFinished(list);
                    }
                });
            }
        }).start();
    }

    public void queryAllProvincesAsync(final OnQueryFinished listener){
        asyncLoader(listener, new LoaderWork() {
            @Override
            public List<City> queryWork() {
                return queryAllProvinces();
            }
        });
    }
    public void queryCityListByParentIdAsync(final int parentId, final int level,final OnQueryFinished listener){
        asyncLoader(listener, new LoaderWork() {
            @Override
            public List<City> queryWork() {
                return queryCityListByParentID(parentId,level);
            }
        });
    }

    public void fuzzyQueryCityListAsync(final String match,final OnQueryFinished onQueryFinished){
        asyncLoader(onQueryFinished, new LoaderWork() {
            @Override
            public List<City> queryWork() {
                if(TextUtils.isEmpty(match)){
                    return queryAllProvinces();
                }else {
                    String sql = String.format("select * from %s where %s like ?",CITY_TABLE,KEY_LOOK_UP);
                    Log.i("Select query", "queryWork: "+sql);
                    String[] args = new String[]{"%"+match+"%"};
                    Cursor c = db.rawQuery(sql,args);
                    return getCityListFromCursor(c);
                }
            }
        });
    }

    public CityDatabase(Activity context) {
        this.context = context;
    }
    public void open(){
        if (db==null){
            databaseHelper =new DatabaseHelper();
            db = databaseHelper.getWritableDatabase();
        }
    }
    public void close(){
        if(db!=null && db.isOpen()){
            db.close();
        }
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper() {
            super(context, DB_NAME, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = String.format("CREATE Table if not exists %s (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "%s int, %s int, %s text, %s text, %s text,%s text, %s int, %s text)", CITY_TABLE, KEY_ID, KEY_PID, KEY_NAME, KEY_WEATHER_ID, KEY_EN_NAME, KEY_INI_NAME, KEY_LEVEL, KEY_LOOK_UP);
            db.execSQL(sql);


        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            resetData(db);

        }

        private void resetData(SQLiteDatabase db) {
            db.execSQL(String.format("drop table if exists %s", CITY_TABLE));
            onCreate(db);
        }
    }

    private ContentValues enCodeContentValues(City city) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_ID, city.getId());
        cv.put(KEY_PID, city.getParentId());
        cv.put(KEY_NAME, city.getName());
        cv.put(KEY_WEATHER_ID,city.getWeather_id());
        cv.put(KEY_EN_NAME,city.getEnName());
        cv.put(KEY_INI_NAME,city.getInitialName());
        cv.put(KEY_LEVEL,city.getLevel());
        String lookup = generateLookup(city);
        cv.put(KEY_LOOK_UP,lookup);

        return cv;
    }

    private String generateLookup(City city) {
        String name = city.getName();
        String enName = city.getEnName();
        String iniName = city.getInitialName();
        String [] enNameArray = enName.split("\\s");
        StringBuilder sb = new StringBuilder();
        sb.append(name+" ");
        sb.append(enName+" ");
        sb.append(iniName+" ");
        sb.append(enName.replaceAll("\\s","")+" ");

        return sb.toString();
    }
    @NonNull
    private City getCityFromCursor(Cursor c){
        City city;
        int id = c.getInt(c.getColumnIndex(KEY_ID));
        int pid = c.getInt(c.getColumnIndex(KEY_PID));
        String weather_id = c.getString(c.getColumnIndex(KEY_WEATHER_ID));
        String name = c.getString(c.getColumnIndex(KEY_NAME));
        String enName = c.getString(c.getColumnIndex(KEY_EN_NAME));
        String iniName = c.getString(c.getColumnIndex(KEY_INI_NAME));
        int level = c.getInt(c.getColumnIndex(KEY_LEVEL));
        city = new City(id,pid,name);
        city.setLevel(level);
        city.setWeather_id(weather_id);
        city.setEnName(enName);
        city.setInitialName(iniName);
        return city;
    }

    public long insertData(City city){
        ContentValues cv = enCodeContentValues(city);
        return db.insert(CITY_TABLE,null,cv);
    }

    public int insertList(List<City> list){
        int count = 0;
        for (int i = 0; i < list.size() ; i++) {
            if(insertData(list.get(i))>0){
                count++;
            }

        }
        return count;
    }

    public void clearDatabase(){
        databaseHelper.resetData(db);
    }

    public City queryCityById(int id, int level){
        City city = null;
        String sql = String.format("select * from %s where %s=%d and %s=%d",CITY_TABLE,KEY_ID,id,KEY_LEVEL,level);
        Cursor c = db.rawQuery(sql,null);
        if(c.getCount()>0){
            c.moveToFirst();
            city = getCityFromCursor(c);
        }
        c.close();
        return city;
    }

    public List<City> queryAllProvinces(){
        String sql = String.format("select * from %s where %s=0",CITY_TABLE,KEY_LEVEL);
        Cursor c = db.rawQuery(sql,null);
        return getCityListFromCursor(c);
    }

    private List<City> getCityListFromCursor(Cursor c) {
        List<City> list = new ArrayList<>();
        for (int i = 0; i <c.getCount() ; i++) {
            c.moveToPosition(i);
            City city = getCityFromCursor(c);
            list.add(city);

        }
        c.close();
        return list;
    }

    public List<City> queryCityListByParentID(int parentID,int level){
        if(level==0){
            return queryAllProvinces();

        }else{
            String sql = String.format("select * from %s where %s=%d and %s=%d",CITY_TABLE,KEY_PID,parentID,KEY_LEVEL,level);
            Cursor c = db.rawQuery(sql,null);
            return getCityListFromCursor(c);
        }
    }
}
