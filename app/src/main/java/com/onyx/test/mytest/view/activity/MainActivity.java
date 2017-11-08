package com.onyx.test.mytest.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;

import com.onyx.test.mytest.R;
import com.onyx.test.mytest.binding.ActivityMainModel;
import com.onyx.test.mytest.databinding.ActivityMainBinding;
import com.onyx.test.mytest.model.utils.ActivityUtil;
import com.onyx.test.mytest.view.adapter.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

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
        initToolbar();
        initTabPage();
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

    private void initTabPage() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mainModel.getFragmentList(), mainModel.getTabTitleList());
        binding.viewPager.setAdapter(viewPagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }
}
