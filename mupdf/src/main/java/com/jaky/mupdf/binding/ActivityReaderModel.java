package com.jaky.mupdf.binding;

import com.jaky.mupdf.ui.activity.ReaderActivity;
import com.jaky.mupdf.ui.activity.ReaderActivity.TopBarMode;

/**
 * Created by jaky on 2017/12/1 0001.
 */

public class ActivityReaderModel {

    private ReaderActivity context;
    private boolean toolBarVisible;
    private boolean isReflow;
    private TopBarMode topBarMode;
    private String fileName;


    public ActivityReaderModel(ReaderActivity muPDFActivity) {
        this.context = muPDFActivity;
    }
}
