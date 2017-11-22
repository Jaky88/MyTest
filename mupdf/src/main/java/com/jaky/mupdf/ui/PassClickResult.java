package com.jaky.mupdf.ui;

/**
 * Created by jaky on 2017/11/22 0022.
 */

public class PassClickResult {
    public final boolean changed;

    public PassClickResult(boolean _changed) {
        changed = _changed;
    }

    public void acceptVisitor(PassClickResultVisitor visitor) {
    }
}
