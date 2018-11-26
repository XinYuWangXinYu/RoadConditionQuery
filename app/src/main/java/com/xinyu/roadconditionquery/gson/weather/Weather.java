package com.xinyu.roadconditionquery.gson.weather;

import com.google.gson.annotations.SerializedName;

/**
 * 名称: Weather
 * 作者: WangXinYu
 * 时间: 2018/11/25
 * 描述:
 */
public class Weather {

    public String reason;

    @SerializedName("result")
    public WeatherResult result;

    @SerializedName("error_code")
    public String errorCode;
}
