package com.xinyu.roadconditionquery.gson.weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 名称: WeatherResult
 * 作者: WangXinYu
 * 时间: 2018/11/25
 * 描述:
 */
public class WeatherResult {

    @SerializedName("today")
    public TodayWeather todayWeather;

    @SerializedName("future")
    public List<FutureWeather> futureWeatherList;

    public class TodayWeather {
        public String temperature;

        @SerializedName("weather")
        public String weatherInfo;
    }

    public class FutureWeather {
        public String temperature;

        @SerializedName("weather")
        public String weatherInfo;
    }

}
