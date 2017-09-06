package com.onyx.test.styletest;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.tab_layout)
    android.support.design.widget.TabLayout tabLayout;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private List<View> viewList;
    private List<String> titleList;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();
        initData();
        initTabPage();
    }

    private void initData() {
        titleList = new ArrayList<>();
        titleList = Arrays.asList(getResources().getStringArray(R.array.tab_title_items));

        viewList = new ArrayList<>();
        TypedArray array = getResources().obtainTypedArray(R.array.tab_layout_items);
        for (int i = 0; i < array.length(); i++) {
            viewList.add(LayoutInflater.from(this).inflate(array.getResourceId(i, 0), null));
        }
    }

    private void initTabPage() {
        viewPagerAdapter = new ViewPagerAdapter(viewList, titleList);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initToolbar() {
        toolbar.inflateMenu(R.menu.home_option_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add:
                        break;
                    case R.id.menu_setting:
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
}
