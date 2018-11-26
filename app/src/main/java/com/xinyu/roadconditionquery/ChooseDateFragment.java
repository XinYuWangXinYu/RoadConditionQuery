package com.xinyu.roadconditionquery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.xinyu.roadconditionquery.util.LogUtil.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 名称: ChooseDateFragment
 * 作者: WangXinYu
 * 时间: 2018/11/24
 * 描述:
 */
public class ChooseDateFragment extends Fragment {
    private String TAG = "ChooseDateFragment";
    private ListView dateListView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    public static int dateId = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_date, container, false);
        dateListView = (ListView) view.findViewById(R.id.date_list);
        initDataList();
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, dataList);
        dateListView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dateDate = dataList.get(position);
                dateId = position + 1;
                TrafficActivity activity = (TrafficActivity) getActivity();
                LogUtil.e(TAG, "3. cityCode: " + ChooseAreaFragment.cityCode + " ; "+ "dateId: " + dateId);
                activity.drawerLayout.closeDrawers();
                activity.swipeRefreshLayout.setRefreshing(true);
                activity.queryTraffic(ChooseAreaFragment.cityCode, dateId);
                activity.queryWeather(ChooseAreaFragment.cityName, dateId);
                dateId = 1;
            }
        });
    }

    private void initDataList() {
        dataList.add("今天");
        dataList.add("明天");
        dataList.add("后天");
        dataList.add("第4天");
        dataList.add("第5天");
        dataList.add("第6天");
    }

}
