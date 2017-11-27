package com.jaky.mupdf.data;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ChoosePDFItem {

    public static final int PARENT = 0;
    public static final int DIR = 1;
    public static final int DOC = 2;

    @IntDef({PARENT, DIR, DOC})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    @Type
    final public int type;
    final public String name;


    public ChoosePDFItem(@Type int t, String n) {
        type = t;
        name = n;
    }
}
