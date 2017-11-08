package com.onyx.test.mytest.ui.activity;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;

import com.onyx.test.mytest.R;
import com.onyx.test.mytest.databinding.ActivityMainBinding;
import com.onyx.test.mytest.ui.adapter.ViewPagerAdapter;
import com.onyx.test.mytest.ui.fragment.FragmentFactory;
import com.onyx.test.mytest.ui.viewmodel.ActivityMainModel;
import com.onyx.test.mytest.utils.ActivityUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Fragment> fragments;
    private ViewPagerAdapter viewPagerAdapter;
    private ActivityMainBinding binding;
    private ActivityMainModel mainModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainModel = new ActivityMainModel(MainActivity.this);
        binding.setMainModel(mainModel);
        initData();
        initToolbar();
        initTabPage();
    }

    private void initData() {
        mainModel.setTabTitleList(Arrays.asList(getResources().getStringArray(R.array.tab_title_items)));
        fragments = new ArrayList<>();
        for (int i = 0; i < mainModel.getTabTitleList().size(); i++) {
            fragments.add(FragmentFactory.createFragment(i));
        }
    }

    private void initTabPage() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments, mainModel.getTabTitleList());
        binding.viewPager.setAdapter(viewPagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }

    private void initToolbar() {
        binding.toolbar.inflateMenu(R.menu.menu_main);
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
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



}
