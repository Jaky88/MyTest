package com.jaky.mupdf.ui.views.adapterview;

import android.view.View;

/**
 * Created by Jack on 2017/12/6.
 */

public interface ReaderViewItemAction {
    void onChildSetup(int i, View v);

    void onMoveToChild(int i);

    void onMoveOffChild(int i);

    void onSettle(View v);

    void onUnsettle(View v);

    void onNotInUse(View v);

    void onScaleChild(View v, Float scale);
}
