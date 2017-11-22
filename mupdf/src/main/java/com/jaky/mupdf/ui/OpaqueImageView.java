package com.jaky.mupdf.ui;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by jaky on 2017/11/22 0022.
 */

public class OpaqueImageView extends ImageView {

    public OpaqueImageView(Context context) {
        super(context);
    }

    @Override
    public boolean isOpaque() {
        return true;
    }
}
