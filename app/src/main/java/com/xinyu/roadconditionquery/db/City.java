package com.xinyu.roadconditionquery.db;

import org.litepal.crud.DataSupport;

/**
 * 名称: City
 * 作者: WangXinYu
 * 时间: 2018/11/23
 * 描述:
 */
public class City extends DataSupport {
    private  int id;
    private String cityName;
    private String cityCode;

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
