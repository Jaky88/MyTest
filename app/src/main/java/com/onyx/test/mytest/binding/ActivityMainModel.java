package com.onyx.test.mytest.binding;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v4.app.Fragment;

import com.onyx.test.mytest.R;
import com.onyx.test.mytest.view.fragment.FragmentFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jaky on 2017/11/8 0008.
 */

public class ActivityMainModel extends BaseObservable{
    public String title ="";
    public String versionName ="版本号：";
    public Activity activity;
    public List<String> tabTitleList = new ArrayList();
    public List<Fragment> fragmentList = new ArrayList();

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public List getFragmentList() {
        return fragmentList;
    }

    public void setFragmentList(List fragmentList) {
        this.fragmentList = fragmentList;
    }

    public ActivityMainModel(Activity activity){
        this.activity = activity;
        this.versionName = getVersionNameImpl();
        initData();

    }

    private void initData() {
        setTabTitleList(Arrays.asList(activity.getResources().getStringArray(R.array.tab_title_items)));
        for (int i = 0; i < tabTitleList.size(); i++) {
            fragmentList.add(FragmentFactory.createFragment(i));
        }
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
