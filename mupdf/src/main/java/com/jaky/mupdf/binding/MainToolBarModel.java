package com.jaky.mupdf.binding;

import com.jaky.mupdf.core.MuPDFCore;
import com.jaky.mupdf.ui.activity.ReaderActivity;

/**
 * Created by Jack on 2017/11/30.
 */

public class MainToolBarModel {
    private ReaderActivity context;
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

    public MainToolBarModel(ReaderActivity context, MuPDFCore core) {
        this.context = context;
        this.core = core;
        canProof = core.canProof();
        proof = isProofing();
        gprfSurpport = core.gprfSupported();
    }




}
