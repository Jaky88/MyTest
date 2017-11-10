package com.onyx.test.mytest.binding;

import android.app.Activity;
import android.content.Context;
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

public class ActivityMainModel extends BaseObservable {
    private String title;
    private String versionName;
    private Context context;
    private List<String> tabTitleList = new ArrayList();
    private List<Fragment> fragmentList = new ArrayList();

    public ActivityMainModel(Context context) {
        this.context = context;
        initData();
    }

    private void initData() {
        setVersionName(getVersionNameImpl());
        setTabTitleList(Arrays.asList(context.getResources().getStringArray(R.array.tab_title_items)));
        for (int i = 0; i < tabTitleList.size(); i++) {
            fragmentList.add(FragmentFactory.createFragment(i));
        }
    }

    public List getFragmentList() {
        return fragmentList;
    }

    public void setFragmentList(List fragmentList) {
        this.fragmentList = fragmentList;
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
