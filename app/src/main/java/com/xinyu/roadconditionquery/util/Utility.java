package com.xinyu.roadconditionquery.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xinyu.roadconditionquery.db.City;
import com.xinyu.roadconditionquery.gson.Traffic;
import com.xinyu.roadconditionquery.gson.weather.Weather;
import com.xinyu.roadconditionquery.gson.weather.WeatherResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 名称: Utility
 * 作者: WangXinYu
 * 时间: 2018/11/23
 * 描述:
 */
public class Utility {

    private String TAG = "Utility";

    public static final String ADDRESS_TRAFFIC = "http://v.juhe.cn/xianxing/index?";
    public static final String ADDRESS_CITY = "http://v.juhe.cn/xianxing/citys?";
    public static final String ADDRESS_WEATHER = "http://v.juhe.cn/weather/index?format=2&cityname=";
    public static String TrafficAppKey = "5ff35fe6f8424aca89f4f9cf44e8cfac";    //初始key
    public static String WeatherAppKey = "193e62d1281939c8e95c04df3e3a1ee7";    //初始key


    public static final String trafficMessage = "TRAFFIC_MESSAGE";
    public static final String weatherMessage = "WEATHER_MESSAGE";
    public static final String bingPic = "bing_pic";
    public static final String CITY_CODE = "cityCode";
    public static final String CITY_NAME = "cityName";
    public static final String CITY_DATE = "city_date";
    public static final String TRAFFIC_KEY = "TRAFFIC_KEY";
    public static final String WEATHER_KEY = "WEATHER_KEY";

    /**
     * 解析城市数据
     */
    public static boolean handleCityResponse(String resopnse) {
        if (!TextUtils.isEmpty(resopnse)) {
            try {
                JSONObject allCities = new JSONObject(resopnse);
                String reason = allCities.getString("reason");
                String errorCode = allCities.getString("error_code");
                JSONArray allCityData = allCities.getJSONArray("result");
                for (int i = 0; i < allCityData.length(); i++) {
                    JSONObject cityObject = allCityData.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(cityObject.getString("city"));
                    city.setCityName(cityObject.getString("cityname"));
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     *解析天气信息
     *
     * @param response
     * @return
     */
    public static Weather handleWeatherResponse(String response) {
        return new Gson().fromJson(response, Weather.class);
    }

    /**
     * 解析路况信息
     */
    public static Traffic handleTrafficResponse(String response) {
        return new Gson().fromJson(response, Traffic.class);
    }

}
