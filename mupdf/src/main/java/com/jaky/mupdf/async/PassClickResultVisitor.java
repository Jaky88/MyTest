package com.jaky.mupdf.async;

import com.jaky.mupdf.async.PassClickResultChoice;
import com.jaky.mupdf.async.PassClickResultSignature;
import com.jaky.mupdf.async.PassClickResultText;

/**
 * Created by jaky on 2017/11/22 0022.
 */

public abstract class PassClickResultVisitor {
    public abstract void visitText(PassClickResultText result);
    public abstract void visitChoice(PassClickResultChoice result);
    public abstract void visitSignature(PassClickResultSignature result);
}
