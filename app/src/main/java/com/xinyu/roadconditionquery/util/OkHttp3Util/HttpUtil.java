package com.xinyu.roadconditionquery.util.OkHttp3Util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 名称: HttpUtil
 * 作者: WangXinYu
 * 时间: 2018/11/23
 * 描述:
 */
public class HttpUtil {

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }



}
