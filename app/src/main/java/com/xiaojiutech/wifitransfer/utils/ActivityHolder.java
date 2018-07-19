package com.xiaojiutech.wifitransfer.utils;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;

public class ActivityHolder {
    private static ActivityHolder sInstance;
    private static List<Activity> sList = new LinkedList<Activity>();
    private ActivityHolder(){

    }

    public static synchronized ActivityHolder getInstance(){
        if (sInstance==null){
            sInstance = new ActivityHolder();
        }
        return sInstance;
    }

    public void addActivity(Activity activity){
        if (activity!=null&&sList!=null){
            if (sList.contains(activity)){
                sList.remove(activity);
                sList.add(sList.size(),activity);
            }else {
                sList.add(activity);
            }
        }
    }

    public void removeActivity(Activity activity){
        sList.remove(activity);
    }

    public void finishAllActivity(){
        int size = sList.size();
        for (int i = size -1 ; i >=0 ; i--) {
            Activity activity = sList.get(i);
            if (activity!=null){
                activity.finish();
            }
            sList.remove(activity);
        }
    }
}
