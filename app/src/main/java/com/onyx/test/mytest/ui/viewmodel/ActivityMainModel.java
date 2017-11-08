package com.onyx.test.mytest.ui.viewmodel;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaky on 2017/11/8 0008.
 */

public class ActivityMainModel {
    public String title = "";
    public String versionName = "版本号：";
    public List tabTitleList = new ArrayList();
    public Activity activity;

    public ActivityMainModel(Activity activity) {
        this.activity =activity;
        versionName = getVersionName1();
    }

    public List getTabTitleList() {
        return tabTitleList;
    }

    public void setTabTitleList(List tabTitleList) {
        this.tabTitleList = tabTitleList;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersionName1() {
        PackageManager manager = activity.getPackageManager();
        try {
            PackageInfo packageInfo = manager.getPackageInfo(activity.getPackageName(), PackageManager.GET_ACTIVITIES
            );
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0";
    }
}
