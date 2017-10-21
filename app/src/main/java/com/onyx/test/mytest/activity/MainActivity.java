package com.onyx.test.mytest.activity;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.onyx.test.mytest.R;
import com.onyx.test.mytest.adapter.ViewPagerAdapter;
import com.onyx.test.mytest.fragment.FragmentFactory;
import com.onyx.test.mytest.utils.ActivityUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_version_name)
    TextView tvVersionName;
    private List<Fragment> fragments;
    private List<String> titleList;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        initToolbar();
        initTabPage();
    }

    private void initData() {
        titleList = new ArrayList<>();
        titleList = Arrays.asList(getResources().getStringArray(R.array.tab_title_items));

        fragments = new ArrayList<>();
        for (int i = 0; i < titleList.size(); i++) {
            fragments.add(FragmentFactory.createFragment(i));
        }
    }

    private void initTabPage() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments, titleList);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initToolbar() {
        tvVersionName.setText("版本号：" + getVersionName());
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add:
                        break;
                    case R.id.menu_setting:
                        ActivityUtil.startActivity(MainActivity.this, SettingsActivity.class);
                        break;
                    case R.id.menu_clear_recent_reading:
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    public String getVersionName() {
        PackageManager manager = getPackageManager();
        try {
            PackageInfo packageInfo = manager.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES
            );
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "1.0";
    }

}
