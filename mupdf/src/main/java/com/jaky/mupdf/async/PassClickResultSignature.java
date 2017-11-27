package com.jaky.mupdf.async;

import com.jaky.mupdf.data.ReaderConstants;

import static com.jaky.mupdf.data.ReaderConstants.SignatureState;

/**
 * Created by jaky on 2017/11/22 0022.
 */

public class PassClickResultSignature extends PassClickResult {
    @SignatureState
    public final String state;

    public PassClickResultSignature(boolean _changed, int _state) {
        super(_changed);
        state = SignatureState.values[_state];
    }

    public void acceptVisitor(PassClickResultVisitor visitor) {
        visitor.visitSignature(this);
    }
}
