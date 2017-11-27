package com.jaky.mupdf.data;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by jaky on 2017/11/27 0027.
 */

public class ReaderConstants {

    //Hit
    public static final String NOTHING = "Nothing";
    public static final String WIDGET = "Widget";
    public static final String ANNOTATION = "Annotation";

    //Purpose
    public static final String PICKPDF = "PickPDF";
    public static final String PICKKEYFILE = "PickKeyFile";

    //SignatureState
    public static final String NOSUPPORT = "NoSupport";
    public static final String UNSIGNED = "Unsigned";
    public static final String SIGNED = "Signed";


    public static final String NONE = "NONE";
    public static final String TEXT = "TEXT";
    public static final String LISTBOX = "LISTBOX";
    public static final String COMBOBOX = "COMBOBOX";
    public static final String SIGNATURE = "SIGNATURE";


    @StringDef({NOTHING, WIDGET, ANNOTATION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Hit {
    }

    @StringDef({PICKPDF, PICKKEYFILE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Purpose {
    }

    @StringDef({NOSUPPORT, UNSIGNED, SIGNED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SignatureState {
        String[] values = {NOSUPPORT, UNSIGNED, SIGNED};
    }

    @StringDef({NONE, TEXT, LISTBOX, COMBOBOX, SIGNATURE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface WidgetType {
        String[] values = {NONE, TEXT, LISTBOX, COMBOBOX, SIGNATURE};
    }

}
