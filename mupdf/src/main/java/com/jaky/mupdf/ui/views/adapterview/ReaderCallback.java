package com.jaky.mupdf.ui.views.adapterview;

import com.jaky.mupdf.data.ReaderConstants;
import com.jaky.mupdf.task.SearchTaskResult;

/**
 * Created by jaky on 2017/12/1 0001.
 */

public abstract class ReaderCallback {

    protected void onTapMainDocArea() {}
    protected void onDocMotion() {}


    @ReaderConstants.Hit
    protected void onHit(String item) {}

    protected void onMoveToChild(int i) {}
}
