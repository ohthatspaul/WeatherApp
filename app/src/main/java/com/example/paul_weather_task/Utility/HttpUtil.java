package com.example.paul_weather_task.Utility;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {
    public interface SimpleCallback{
        public void onFailure(IOException e);
        public void onFailure2(IllegalArgumentException e);
        public void onResponse(String response);
    }

    public static String sendOKHttpGetBlock(String url){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = client.newCall(request);
        try{
            Response response = call.execute();
            String s = response.body().string();
            return s;

        }catch (IOException e){
            e.printStackTrace();
            Log.d("Okhttp", "sendOKHttpGetBlock: "+e.toString());
        }
        return null;
    }
    public static void sendOKHttpGetAsync(final Activity activity, String url, final SimpleCallback simpleCallback){
        OkHttpClient client = new OkHttpClient();
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    Log.d("OkHttp", "onFailure: "+e.toString());
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            simpleCallback.onFailure(e);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try{
                        final String s = response.body().string();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                simpleCallback.onResponse(s);
                            }
                        });
                    }catch (final IOException e){
                        e.printStackTrace();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                simpleCallback.onFailure(e);
                            }
                        });
                    }

                }
            });
        }catch(final IllegalArgumentException e){
            e.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    simpleCallback.onFailure2(e);
                }
            });

        }



    }
}
