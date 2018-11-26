package com.xinyu.roadconditionquery.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 名称: WeatherResult
 * 作者: WangXinYu
 * 时间: 2018/11/24
 * 描述:
 */
public class Result {

    public String date;

    public String week;

    @SerializedName("city")
    public String cityCode;

    @SerializedName("cityname")
    public String cityName;

    @SerializedName("des")
    public List<More> moreList;

    @SerializedName("fine")
    public String punish;

    public String remarks;

    @SerializedName("xxweihao")
    public List<Number> weihaoList;

    public String holiday;


    public class More {

        @SerializedName("time")
        public String timeInfo;

        public String place;

        @SerializedName("info")
        public String infoMsg;
    }
}
