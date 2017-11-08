package com.onyx.test.mytest.binding;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.List;

/**
 * Created by jaky on 2017/11/8 0008.
 */

public class ActivityMainModel extends BaseObservable{
    public String title ="";
    public String versionName ="版本号：";
    public Context context;
    public List tabTitleList;

    public ActivityMainModel(Context context){
        this.context = context;
        this.versionName = getVersionNameImpl();
    }

    @Bindable
    public List getTabTitleList() {
        return tabTitleList;
    }

    public void setTabTitleList(List tabTitleList) {
        this.tabTitleList = tabTitleList;
    }

    @Bindable
    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersionNameImpl() {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES
            );
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0";
    }
}
