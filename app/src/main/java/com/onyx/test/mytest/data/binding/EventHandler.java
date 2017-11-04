package com.onyx.test.mytest.data.binding;

import android.view.View;
import android.widget.Toast;

/**
 * Created by jaky on 2017/11/4 0004.
 */

public class EventHandler {

    public void settingsOnClick(View view) {
        Toast.makeText(view.getContext(),    " is Clicked", Toast.LENGTH_SHORT).show();
    }

    public void selectFileOnClick(View view) {
        Toast.makeText(view.getContext(),    " is Clicked", Toast.LENGTH_SHORT).show();
    }
}
