package com.xinyu.roadconditionquery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xinyu.roadconditionquery.util.LogUtil.LogUtil;
import com.xinyu.roadconditionquery.util.Utility;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.LogUtilInit(LogUtil.NOTHING);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if ( !(Boolean) prefs.getBoolean("firstLaunch", false)) {
            LogUtil.e(TAG, "第一次启动!!!");
            editor.putBoolean("firstLaunch", true);
            editor.putString(Utility.TRAFFIC_KEY, Utility.TrafficAppKey);
            editor.putString(Utility.WEATHER_KEY, Utility.WeatherAppKey);
            editor.apply();
        }

        LogUtil.e(TAG, "读取TrafficAppKey: " + prefs.getString(Utility.TRAFFIC_KEY, Utility.TrafficAppKey));
        LogUtil.e(TAG, "读取WeatherAppKey: " + prefs.getString(Utility.WEATHER_KEY, Utility.WeatherAppKey));

        String trafficMessage = prefs.getString(Utility.trafficMessage, null);
        if (trafficMessage != null) {
            Intent intent = new Intent(this, TrafficActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
