package com.xaqianbai.QBHotelSecurutyGovernor.Utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/4/12.
 */
public class ActivityController {
    /**
     * 放activity的集合
     */
    public static List<Activity> activities = new ArrayList<>();

    /**
     * 添加activity到集合的方法
     */
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * 从集合中移除activity的方法
     */
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    /**
     * 移除所有activity的方法
     */
    public static void finishAll() {
        for (Activity a : activities) {
            if (!a.isFinishing()) {
                a.finish();
            }
        }
    }


}
