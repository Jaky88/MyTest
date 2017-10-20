package com.onyx.test.styletest.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.onyx.test.styletest.R;
import com.onyx.test.styletest.adapter.ViewPagerAdapter;
import com.onyx.test.styletest.config.Constant;
import com.onyx.test.styletest.fragment.FragmentFactory;
import com.onyx.test.styletest.utils.ActivityUtil;
import com.onyx.test.styletest.utils.FileUtil;

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

    private static final String TAG = "ChooseFile";
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case FILE_SELECT_CODE:
//                if (resultCode == RESULT_OK) {
//                    // Get the Uri of the selected file
//                    Uri uri = data.getData();
//                    Log.d(TAG, "File Uri: " + uri.toString());
//                    // Get the path
//                    String path = FileUtil.getPath(this, uri);
//                    Log.d(TAG, "File Path: " + path);
//                    // Get the file instance
//                    // File file = new File(path);
//                    // Initiate the upload
//                }
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.REQUESTCODE_FROM_ACTIVITY) {
                List<String> list = data.getStringArrayListExtra(Constant.RESULT_FILE_NAME);
                Toast.makeText(getApplicationContext(), "选中了" + list.size() + "个文件", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
