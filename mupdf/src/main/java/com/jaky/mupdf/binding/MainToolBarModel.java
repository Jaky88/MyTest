package com.jaky.mupdf.binding;

import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.jaky.mupdf.R;
import com.jaky.mupdf.core.MuPDFCore;
import com.jaky.mupdf.data.Annotation;
import com.jaky.mupdf.data.Separation;
import com.jaky.mupdf.ui.activity.MuPDFActivity;
import com.jaky.mupdf.ui.views.adapterview.MuPDFReaderView;
import com.jaky.mupdf.ui.views.baseview.MuPDFView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Jack on 2017/11/30.
 */

public class MainToolBarModel {
    private MuPDFActivity context;
    private MuPDFCore core;
    private boolean canProof;
    private boolean proof;
    private boolean gprfSurpport;

    public boolean isCanProof() {
        return canProof;
    }

    public void setCanProof(boolean canProof) {
        this.canProof = canProof;
    }

    public boolean isProof() {
        return proof;
    }

    public void setProof(boolean proof) {
        this.proof = proof;
    }

    public boolean isGprfSurpport() {
        return gprfSurpport;
    }

    public void setGprfSurpport(boolean gprfSurpport) {
        this.gprfSurpport = gprfSurpport;
    }

    public boolean isProofing() {
        String format = core.fileFormat();
        return (format.equals("GPROOF"));
    }

    public MainToolBarModel(MuPDFActivity context, MuPDFCore core) {
        this.context = context;
        this.core = core;
        canProof = core.canProof();
        proof = isProofing();
        gprfSurpport = core.gprfSupported();
    }




}
