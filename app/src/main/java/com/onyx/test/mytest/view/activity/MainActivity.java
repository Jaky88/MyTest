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
import com.jaky.utils.ActivityUtil;
import com.onyx.test.mytest.view.adapter.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private ViewPagerAdapter viewPagerAdapter;
    private ActivityMainBinding bindingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_main);
        bindingView.setMainModel(new ActivityMainModel(MainActivity.this));
        initToolbar();
        initTabPage();
    }

    private void initToolbar() {
        bindingView.toolbar.inflateMenu(R.menu.menu_main);
        bindingView.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
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
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                bindingView.getMainModel().getFragmentList(),
                bindingView.getMainModel().getTabTitleList());
        bindingView.viewPager.setAdapter(viewPagerAdapter);
        bindingView.tabLayout.setupWithViewPager(bindingView.viewPager);
    }
}
