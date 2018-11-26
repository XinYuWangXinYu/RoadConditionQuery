package com.xinyu.roadconditionquery;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xinyu.roadconditionquery.gson.Result;
import com.xinyu.roadconditionquery.gson.Traffic;
import com.xinyu.roadconditionquery.gson.weather.Weather;
import com.xinyu.roadconditionquery.gson.weather.WeatherResult;
import com.xinyu.roadconditionquery.util.LogUtil.LogUtil;
import com.xinyu.roadconditionquery.util.OkHttp3Util.HttpUtil;
import com.xinyu.roadconditionquery.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TrafficActivity extends AppCompatActivity {

    private String TAG = "TrafficActivity";
    private ImageView bingPicImg;
    private ScrollView trafficScrollviewLayout;

    private TextView titleText;
    private TextView titleDate;
    private TextView titleWeek;

    private TextView degreeText;
    private TextView weatherInfoText;

    private TextView weihaoText;

    private LinearLayout forbiddenInfoLayout;

    private TextView pubishPubish;
    private TextView pubishRemarks;

    private TextView holidayHoliday;

    public SwipeRefreshLayout swipeRefreshLayout;

    private Button navHomeButton;

    public DrawerLayout drawerLayout;

    private StringBuffer stringBuffer = new StringBuffer();
    private String mCityCode;
    private int cityDate;
    private String mCityName;
    private int keyCount = 0;
    private long firstPressedTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //全屏
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_traffic);

        //初始化各控件
        drawerLayout = (DrawerLayout) findViewById(R.id.draw_layout);
        navHomeButton = (Button) findViewById(R.id.navhome_button);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        trafficScrollviewLayout = (ScrollView) findViewById(R.id.traffic_scrollview_layout);
        titleText = (TextView) findViewById(R.id.title_text);
        titleDate = (TextView) findViewById(R.id.title_date);
        titleWeek = (TextView) findViewById(R.id.title_week);

        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);

        weihaoText = (TextView) findViewById(R.id.weihao_text);

        forbiddenInfoLayout = (LinearLayout) findViewById(R.id.forbiddeninfo_layout);

        pubishPubish = (TextView) findViewById(R.id.forbiddenpubish_pubish);
        pubishRemarks = (TextView) findViewById(R.id.forbiddenpubish_remarks);

        holidayHoliday = (TextView) findViewById(R.id.forbiddenholiday_holiday);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String trafficMessage = prefs.getString(Utility.trafficMessage, null);
        String weatherMessage = prefs.getString(Utility.weatherMessage, null);
        if (trafficMessage != null) {
            //有缓存时解析路况数据
            Traffic traffic = Utility.handleTrafficResponse(trafficMessage);
            mCityCode = traffic.result.cityCode;
            mCityName = traffic.result.cityName;
            cityDate = prefs.getInt(Utility.CITY_DATE, 1);
            LogUtil.e(TAG, "4. cityCode: " + mCityCode + " ; "+ "dateId: " + cityDate);
            //显示完整信息
            showTrafficMessage(traffic);
            LogUtil.e(TAG, "有缓存解析路况数据");
        } else {
            //无缓存时访问服务器查询路况信息
            LogUtil.e(TAG, "无缓存解析路况数据");
            mCityCode = getIntent().getStringExtra(Utility.CITY_CODE);
            mCityName = getIntent().getStringExtra(Utility.CITY_NAME);
            cityDate = ChooseDateFragment.dateId;
            LogUtil.e(TAG, "5. cityCode: " + mCityCode + " ; "+ "dateId: " + cityDate);
            trafficScrollviewLayout.setVisibility(View.INVISIBLE);
            queryTraffic(mCityCode, cityDate);
            queryWeather(mCityName, cityDate);
        }

        if (weatherMessage != null) {
            LogUtil.e(TAG, "CityName: " + mCityName);
            Weather weather = Utility.handleWeatherResponse(weatherMessage);
            showWeatherMessage(weather, cityDate);
        } else {
            queryWeather(mCityName, cityDate);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(TrafficActivity.this);
                cityDate = prefs.getInt(Utility.CITY_DATE, 1);
                LogUtil.e(TAG, "6. cityCode: " + mCityCode + " ; "+ "dateId: " + cityDate);
                queryTraffic(mCityCode, cityDate);
                queryWeather(mCityName, cityDate);
            }
        });

        String bingPic = prefs.getString(Utility.bingPic, null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }

        navHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        titleDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        titleWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - firstPressedTime >= 3000) {
                    keyCount = 0;
                } else {
                    keyCount++;
                    LogUtil.e(TAG, "计次: " + keyCount);
                    if (keyCount >= 5) {
                        Toast.makeText(TrafficActivity.this, "再点击" + (10-keyCount) + "次", Toast.LENGTH_SHORT).show();
                        if (keyCount >= 10) {
                            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(TrafficActivity.this);
                            AlertDialog.Builder builder = new AlertDialog.Builder(TrafficActivity.this);
                            builder.setIcon(R.drawable.ic_danger72px);
                            builder.setTitle("请输入新的AppKey");
                            builder.setCancelable(false);
                            View view = LayoutInflater.from(TrafficActivity.this).inflate(R.layout.layout_dialog, null);
                            builder.setView(view);
                            final EditText trafficAppKey = (EditText) view.findViewById(R.id.traffic_appkey_editview);
                            final EditText weatherAppKey = (EditText) view.findViewById(R.id.weather_appkey_editview);
                            trafficAppKey.setText(prefs.getString(Utility.TRAFFIC_KEY, ""));
                            weatherAppKey.setText(prefs.getString(Utility.WEATHER_KEY, ""));
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String trafficKeyData = trafficAppKey.getText().toString();
                                    String weatherKeyData = weatherAppKey.getText().toString();
                                    SharedPreferences.Editor editor = PreferenceManager.
                                            getDefaultSharedPreferences(TrafficActivity.this).edit();
                                    if (!("".equals(trafficKeyData))) {
                                        LogUtil.e(TAG, "输入值traffic: " + trafficKeyData);
                                        editor.putString(Utility.TRAFFIC_KEY, trafficKeyData);
                                    }

                                    if (!("".equals(weatherKeyData))) {
                                        LogUtil.e(TAG, "输入值weather: " + weatherKeyData);
                                        editor.putString(Utility.WEATHER_KEY, weatherKeyData);

                                    }
                                    editor.apply();
                                    LogUtil.e(TAG, "读取值traffic: " + prefs.getString(Utility.TRAFFIC_KEY, ""));
                                    LogUtil.e(TAG, "读取值weather: " + prefs.getString(Utility.WEATHER_KEY, ""));
                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            builder.show();
                            LogUtil.e(TAG, "点击完毕!!!!");
                            keyCount = 0;
                        }
                    }
                }
                firstPressedTime = System.currentTimeMillis();
            }
        });

    }

    /**
     * 查询路况信息.
     *
     * @param cityCode: 城市列表代码.
     */
    public void queryTraffic(final String cityCode, final int cityType) {
        LogUtil.e(TAG, "查询路况信息: " + cityCode);
        String address = addressTraffic(cityCode, cityType);
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Traffic traffic = Utility.handleTrafficResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (traffic != null && "查询成功".equals(traffic.reason)) {
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(TrafficActivity.this).edit();
                            editor.putString(Utility.trafficMessage, responseText);
                            editor.putInt(Utility.CITY_DATE, cityType);
                            editor.apply();
                            LogUtil.e(TAG, "路况信息查询成功!");
                            mCityCode = traffic.result.cityCode;
                            mCityName = traffic.result.cityName;
                            //显示完整信息
                            showTrafficMessage(traffic);
                        } else {
                            Toast.makeText(TrafficActivity.this, "获取路况信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TrafficActivity.this, "获取路况信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

        });
        loadBingPic();
    }

    /**
     * 查询天气信息
     * @param cityName
     */
    public void queryWeather(String cityName, final int day) {
        LogUtil.e(TAG, "查询天气信息: " + cityName);
        LogUtil.e(TAG, "查询天气信息: " + day);
        String addressWeather = addressWeather(cityName);
        HttpUtil.sendOkHttpRequest(addressWeather, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseTest = response.body().string();
                LogUtil.e(TAG, "天气情况: " + responseTest);
                final Weather weather = Utility.handleWeatherResponse(responseTest);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null ) {
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(TrafficActivity.this).edit();
                            editor.putString(Utility.weatherMessage, responseTest);
                            editor.apply();
                            LogUtil.e(TAG, "天气信息查询成功!");
                            showWeatherMessage(weather, day);
                        } else {
                            Toast.makeText(TrafficActivity.this, "天气加载失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TrafficActivity.this, "天气加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示完整信息
     */
    private void showTrafficMessage(Traffic traffic) {
        titleText.setText(traffic.result.cityName);
        titleDate.setText(traffic.result.date);
        titleWeek.setText(traffic.result.week);

        if (traffic.result.weihaoList != null) {
            int weihaoListLength = traffic.result.weihaoList.size();
            stringBuffer.setLength(0);
            for (int i = 0; i < traffic.result.weihaoList.size(); i++) {
                if (i == (weihaoListLength - 1)) {
                    stringBuffer.append(traffic.result.weihaoList.get(i));
                } else {
                    stringBuffer.append(traffic.result.weihaoList.get(i)).append("  ");
                }
            }
            weihaoText.setText(stringBuffer);
        } else {
            weihaoText.setText("不限号");
        }

        forbiddenInfoLayout.removeAllViews();
        for (Result.More forbiddenInfo : traffic.result.moreList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forbidden_info_item, forbiddenInfoLayout, false);
            TextView carText = (TextView) view.findViewById(R.id.forbiddeninfo_car_text);
            TextView infoId_1 = (TextView) view.findViewById(R.id.info_id_1_text);
            TextView areaText = (TextView) view.findViewById(R.id.forbiddeninfo_area_text);
            TextView infoId_2 = (TextView) view.findViewById(R.id.info_id_2_text);
            TextView infoText = (TextView) view.findViewById(R.id.forbiddeninfo_info_text);
            TextView infoId_3 = (TextView) view.findViewById(R.id.info_id_3_text);

            infoId_1.setText("限行车辆:");
            carText.setText(analyzeData(forbiddenInfo.timeInfo));

            infoId_2.setText("限行区域:");
            areaText.setText(analyzeData(forbiddenInfo.place));

            infoId_3.setText("其他说明:");
            infoText.setText(analyzeData(forbiddenInfo.infoMsg));

            forbiddenInfoLayout.addView(view);
        }

        pubishPubish.setText(traffic.result.punish);
        pubishRemarks.setText(traffic.result.remarks);

        holidayHoliday.setText(analyzeData(traffic.result.holiday));
        trafficScrollviewLayout.setVisibility(View.VISIBLE);
    }

    private void showWeatherMessage(Weather weather, int switchDay) {
        switchDay -= 1;
        degreeText.setText(weather.result.futureWeatherList.get(switchDay).temperature);
        weatherInfoText.setText(weather.result.futureWeatherList.get(switchDay).weatherInfo);
    }

    /**
     * 组装路况查询地址.
     *
     * @param cityCode: 城市列表代码.
     * @param cityType: 类型，1:今日 2:明天 3:后天 4:第4天 5:第5天 6:第6天  默认1
     * @return
     */
    private String addressTraffic(String cityCode, int cityType) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(TrafficActivity.this);
        String trafficAddress = prefs.getString(Utility.TRAFFIC_KEY, Utility.TrafficAppKey);
        LogUtil.e(TAG, "组装路况地址: " + trafficAddress);
        return Utility.ADDRESS_TRAFFIC + "key=" + trafficAddress + "&city=" + cityCode + "&type=" + cityType;
//        return "http://192.168.1.105/get_data_1.json";
    }

    /**
     * 组装天气查询地址.
     * @param cityName
     * @return
     */
    private String addressWeather(String cityName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(TrafficActivity.this);
        String weatherAddress = prefs.getString(Utility.WEATHER_KEY, Utility.WeatherAppKey);
        LogUtil.e(TAG, "组装天气地址: " + weatherAddress);
        return Utility.ADDRESS_WEATHER + cityName + "&key=" + weatherAddress;
//        return "http://192.168.1.105/get_data_2.json";
    }

    /**
     *
     * 替换空信息
     *
     * @param dataInfo
     * @return
     */
    private String analyzeData(String dataInfo) {
        return "".equals(dataInfo) ? "暂无信息." : dataInfo;
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        final String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(TrafficActivity.this).edit();
                editor.putString(Utility.bingPic, bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(TrafficActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TrafficActivity.this, "加载图片失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }

        });
    }

//////////////////////////////////////////////////////////////////////
}
