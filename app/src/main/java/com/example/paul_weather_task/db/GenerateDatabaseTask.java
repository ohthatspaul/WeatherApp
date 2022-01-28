package com.example.paul_weather_task.db;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.paul_weather_task.Data.City;
import com.example.paul_weather_task.Utility.HttpUtil;
import com.example.paul_weather_task.Utility.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class GenerateDatabaseTask extends AsyncTask<Void,Integer,Integer> {
    private Activity context;
    private ProgressDialog progressDialog;
    private String baseUrl="http://guolin.tech/api/china";
    private  CityDatabase cityDatabase;
    private List<City> provinceList = new ArrayList<>();
    private OnTaskFinishedListener listener;
    public interface OnTaskFinishedListener{
        public void onFinished(List<City> list0);
    }

    public GenerateDatabaseTask(Activity activity, CityDatabase cityDatabase, OnTaskFinishedListener listener) {
        this.context = activity;
        this.cityDatabase = cityDatabase;
        this.listener = listener;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressDialog.setProgress(values[0]);
        progressDialog.setMessage(String.format("Inserted data:%d",values[1]));
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        progressDialog.dismiss();
        showToast(String.format("Insert %d data to database",integer));
        listener.onFinished(provinceList);
    }

    private void showToast(String format) {
        Toast.makeText(context,format,Toast.LENGTH_LONG);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int count = 0;
        String ack = HttpUtil.sendOKHttpGetBlock(baseUrl);
        Log.d("Okhttp Response", "doInBackground: "+ack);
        provinceList = JsonUtil.getCityListFromJson(ack, -1, 0);
        if (provinceList != null && provinceList.size() > 0) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    iniProgressDialog();
                }
            });
            cityDatabase.clearDatabase();
            count = count + cityDatabase.insertList(provinceList);
            for (int i = 0; i < provinceList.size(); i++) {
                City province = provinceList.get(i);
                int provinceId = province.getId();
                final String urlProvince = String.format("%s/%d", baseUrl, provinceId);
                String s = HttpUtil.sendOKHttpGetBlock(urlProvince);
                if (!TextUtils.isEmpty(s)) {
                    List<City> cityList = JsonUtil.getCityListFromJson(s, provinceId, 1);
                    count = count + cityDatabase.insertList(cityList);
                    for (int j = 0; j < cityList.size(); j++) {
                        City city = cityList.get(j);
                        int cityId = city.getId();
                        String urlCity = String.format("%s/%d", urlProvince, cityId);
                        String s1 = HttpUtil.sendOKHttpGetBlock(urlCity);
                        if (!TextUtils.isEmpty(s1)) {
                            List<City> countyList = JsonUtil.getCountyListFromJson(s1, cityId, 2);
                            count += cityDatabase.insertList(countyList);
                            publishProgress(i, count);

                        }
                    }
                }
                publishProgress(i + 1, count);

            }
        }
        return count;
    }

    private void iniProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Generating Database");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Inserted Data:");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(provinceList.size());
        progressDialog.setProgress(0);
        progressDialog.show();
    }
}
