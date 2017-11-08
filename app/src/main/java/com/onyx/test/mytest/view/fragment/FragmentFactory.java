package com.onyx.test.mytest.view.fragment;

import android.support.v4.app.Fragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public class FragmentFactory {
    private static Map<Integer, Fragment> mFragments = new HashMap<Integer, Fragment>();

    public static Fragment createFragment(int position)
    {
        Fragment fragment = null;
        fragment = mFragments.get(position);
        if(fragment == null)
        {
            if(position == 0)
            {
                fragment = new Tab1Fragment();
            }
            else if(position == 1)
            {
                fragment = new Tab2Fragment();
            }
            else if(position == 2)
            {
                fragment = new Tab3Fragment();
            }
            else if(position == 3)
            {
                fragment = new Tab4Fragment();
            }
            else if(position == 4)
            {
                fragment = new Tab5Fragment();
            }
//            else if(position == 5)
//            {
//                fragment = new StyleFragment();
//            }
            if(fragment != null)
            {
                mFragments.put(position, fragment);
            }
        }
        return fragment;

    }
}
