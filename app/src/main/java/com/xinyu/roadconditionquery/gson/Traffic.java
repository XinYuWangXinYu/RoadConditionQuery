package com.xinyu.roadconditionquery.gson;

import com.google.gson.annotations.SerializedName;

import okhttp3.Request;

/**
 * 名称: Traffic
 * 作者: WangXinYu
 * 时间: 2018/11/24
 * 描述:
 */
public class Traffic {

    public String reason;

    @SerializedName("result")
    public Result result;

    @SerializedName("error_code")
    public String errorCode;

}
