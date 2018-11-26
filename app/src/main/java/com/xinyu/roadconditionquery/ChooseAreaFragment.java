package com.xinyu.roadconditionquery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.xinyu.roadconditionquery.db.City;
import com.xinyu.roadconditionquery.util.LogUtil.LogUtil;
import com.xinyu.roadconditionquery.util.OkHttp3Util.HttpUtil;
import com.xinyu.roadconditionquery.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 名称: ChooseAreaFragment
 * 作者: WangXinYu
 * 时间: 2018/11/24
 * 描述:
 */
public class ChooseAreaFragment extends Fragment {
    private String TAG = "ChooseAreaFragment";
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<City> cityList;
    private City selectedCity;
    private ProgressDialog progressDialog;
    public static String cityCode;
    public static String cityName;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        listView = (ListView) view.findViewById(R.id.area_list);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.e(TAG, "onActivityCreated ");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCity = cityList.get(position);
                cityCode = selectedCity.getCityCode();
                cityName = selectedCity.getCityName();
                if (getActivity() instanceof MainActivity) {
                    LogUtil.e(TAG, "1. cityCode: " + cityCode + " ; "+ "dateId: " + ChooseDateFragment.dateId);
                    Intent intent = new Intent(getActivity(), TrafficActivity.class);
                    intent.putExtra(Utility.CITY_CODE, cityCode);
                    intent.putExtra(Utility.CITY_NAME, cityName);
                    startActivity(intent);
                    getActivity().finish();
                } else if (getActivity() instanceof TrafficActivity) {
                    TrafficActivity activity = (TrafficActivity) getActivity();
                    LogUtil.e(TAG, "2. cityCode: " + cityCode + " ; "+ "dateId: " + ChooseDateFragment.dateId);
                    activity.drawerLayout.closeDrawers();
                    activity.swipeRefreshLayout.setRefreshing(true);
                    activity.queryTraffic(cityCode, ChooseDateFragment.dateId);
                    activity.queryWeather(cityName, ChooseDateFragment.dateId);
                }
            }
        });
        queryCity();
    }

    /**
     * 查询城市列表,优先从数据库查询.
     */
    private void queryCity() {
        cityList = DataSupport.findAll(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
        } else {
            queryFromServer();
            LogUtil.e(TAG, "从服务器读取城市列表.");
        }
    }

    /**
     * 从服务器查询城市列表.
     */
    private void queryFromServer() {
        showProgressDialog();
        String addressCity = addressCity();
        HttpUtil.sendOkHttpRequest(addressCity, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = Utility.handleCityResponse(responseText);
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            queryCity();
                        }
                    });
                } else {
                    closeProgressDialog();
                    Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }


    /**
     * 显示进度对话框.
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框.
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    private String addressCity() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String cityAddress = prefs.getString(Utility.TRAFFIC_KEY, Utility.TrafficAppKey);
        LogUtil.e(TAG, cityAddress);
        return Utility.ADDRESS_CITY + "key=" + cityAddress;
//        return "http://192.168.1.105/get_data.json";
    }
////////////////////////////////////////////////////////////////
}
