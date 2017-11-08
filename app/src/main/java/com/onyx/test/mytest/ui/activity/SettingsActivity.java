package com.onyx.test.mytest.ui.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.onyx.test.mytest.R;

import butterknife.ButterKnife;

/**
 * Created by jaky on 2017/9/15 0015.
 */

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_headers);
        ButterKnife.bind(this);
        initToolbar();
    }

    private void initToolbar() {

    }
}
