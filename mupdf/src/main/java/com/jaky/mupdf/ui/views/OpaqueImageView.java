package com.jaky.mupdf.ui.views;

import android.content.Context;

/**
 * Created by jaky on 2017/11/22 0022.
 */

public class OpaqueImageView extends android.support.v7.widget.AppCompatImageView {

    public OpaqueImageView(Context context) {
        super(context);
    }

    @Override
    public boolean isOpaque() {
        return true;
    }
}
