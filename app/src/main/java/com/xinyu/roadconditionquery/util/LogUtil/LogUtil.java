package com.xinyu.roadconditionquery.util.LogUtil;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by WangXinYu on 2018/11/21.
 */
public class LogUtil {

    /**
     * 输出等级类型
     */
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WRAN = 4;
    public static final int ERROR = 5;
    private static final int TOAST = 6;
    public static final int NOTHING = 7;


    /**
     * 输出等级选择
     */
    private static int level;


    /**
     * 初始化操作.
     *
     * 必须很早初始化,可放在onCreate等中进行.
     *
     * @param levelValue: 取值为 输出等级类型.
     */
    public static void LogUtilInit(int levelValue) {
        level = levelValue;
    }

    public static void d(String TAG, Object msg) {
        if (level <= VERBOSE) {
            Log.d(TAG, "DEBUG: " + msg);
        }
    }

    public static void i(String TAG, Object msg) {
        if (level <= INFO) {
            Log.i(TAG, "INFO: " + msg);
        }
    }

    public static void w(String TAG, Object msg) {
        if (level <= WRAN) {
            Log.w(TAG, "WARN: " + msg);
        }
    }

    public static void e(String TAG, Object msg) {
        if (level <= ERROR) {
            Log.e(TAG, "ERROR: " + msg);
        }
    }

    public static void toast(Context context, String msg) {
        if (level <= TOAST) {
            Toast.makeText(context, "Log: " + msg, Toast.LENGTH_SHORT).show();
        }
    }

}
