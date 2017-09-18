package com.onyx.test.styletest.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.onyx.test.styletest.R;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: StyleTest
 * @Author: Jack
 * @Date: 2017/9/16 0016,1:12
 * @Version: V1.0
 * @Description: TODO
 */

public class PrefsFragement extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_info);
    }
}
