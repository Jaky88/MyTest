package com.jaky.mupdf.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.jaky.mupdf.R;
import com.jaky.mupdf.data.Annotation;
import com.jaky.mupdf.data.LinkInfo;
import com.jaky.mupdf.data.MuPDFAlert;
import com.jaky.mupdf.data.MuPDFAlertInternal;
import com.jaky.mupdf.data.OutlineItem;
import com.jaky.mupdf.data.Separation;
import com.jaky.mupdf.data.TextChar;
import com.jaky.mupdf.data.TextWord;
import com.jaky.mupdf.data.WidgetType;
import com.jaky.mupdf.ui.PassClickResult;
import com.jaky.mupdf.ui.PassClickResultChoice;
import com.jaky.mupdf.ui.PassClickResultSignature;
import com.jaky.mupdf.ui.PassClickResultText;

import java.util.ArrayList;

public class MuPDFCore {
    private static boolean gs_so_available = false;

    static {
        System.loadLibrary("mupdf_jaky");
        Log.d("", "=====loadLibrary===============");
//        if (gprfSupportedInternal()) {
        if (false) {
            try {
                //提供交互式颜色校正工具的库
                System.loadLibrary("gs");
                gs_so_available = true;
            } catch (UnsatisfiedLinkError e) {
                gs_so_available = false;
            }
        }
    }

    /* Readable members */
    private int numPages = -1;
    private float pageWidth;
    private float pageHeight;
    private long globals;
    private byte fileBuffer[];
    private String file_format;
    private boolean isUnencryptedPDF;
    private final boolean wasOpenedFromBuffer;
    private int id;
    private static int sPluginId = -1;

    public class Cookie {
        private final long cookiePtr;

        public Cookie() {
            cookiePtr = createCookie(id);
            if (cookiePtr == 0)
                throw new OutOfMemoryError();
        }

        public void abort() {
            abortCookie(id, cookiePtr);
        }

        public void destroy() {
            // We could do this in finalize, but there's no guarantee that
            // a finalize will occur before the muPDF context occurs.
            destroyCookie(id, cookiePtr);
        }
    }

    public MuPDFCore(Context context, String filename) throws Exception {
        if (filename == null && filename.isEmpty()) {
            throw new Exception(String.format(context.getString(R.string.cannot_open_file_Path), filename));
        }
        id = nextId();
        Log.d("", "=========openFile========id==" + id + "====filename===" + filename);
        globals = openFile(id, filename);
        if (globals == 0) {
            throw new Exception(String.format(context.getString(R.string.cannot_open_file_Path), filename));
        }
        Log.d("", "=========globals====2======" + globals);
        file_format = fileFormatInternal(id);
        isUnencryptedPDF = isUnencryptedPDFInternal(id);
        wasOpenedFromBuffer = false;
    }

    private synchronized static int nextId() {
        sPluginId++;
        return sPluginId;
    }


    public MuPDFCore(Context context, byte buffer[], String magic) throws Exception {
        fileBuffer = buffer;
        globals = openBuffer(id, magic != null ? magic : "");
        if (globals == 0) {
            throw new Exception(context.getString(R.string.cannot_open_buffer));
        }
        file_format = fileFormatInternal(id);
        isUnencryptedPDF = isUnencryptedPDFInternal(id);
        wasOpenedFromBuffer = true;
    }


    public boolean javascriptSupported() {
        return javascriptSupported(id);
    }

    public int countPages() {
        if (numPages < 0)
            numPages = countPagesSynchronized();
        return numPages;
    }

    public String fileFormat() {
        return file_format;
    }

    public boolean isUnencryptedPDF() {
        return isUnencryptedPDF;
    }

    public boolean wasOpenedFromBuffer() {
        return wasOpenedFromBuffer;
    }

    private synchronized int countPagesSynchronized() {
        return countPagesInternal(id);
    }

    /* Shim function */
    private void gotoPage(int page) {
        if (page > numPages - 1)
            page = numPages - 1;
        else if (page < 0)
            page = 0;
        gotoPageInternal(id, page);
        this.pageWidth = getPageWidth(id);
        this.pageHeight = getPageHeight(id);
    }

    public synchronized PointF getPageSize(int page) {
        gotoPage(page);
        return new PointF(pageWidth, pageHeight);
    }

    public MuPDFAlert waitForAlert() {
        MuPDFAlertInternal alert = waitForAlertInternal(id);
        return alert != null ? alert.toAlert() : null;
    }

    public void replyToAlert(MuPDFAlert alert) {
        replyToAlertInternal(id, new MuPDFAlertInternal(alert));
    }

    public void stopAlerts() {
        stopAlertsInternal(id);
    }

    public void startAlerts() {
        startAlertsInternal(id);
    }

    public synchronized void onDestroy() {
        destroying(id);
        globals = 0;
    }

    public synchronized boolean drawPage(Bitmap bm, int page,
                                         int pageW, int pageH,
                                         int patchX, int patchY,
                                         int patchW, int patchH,
                                         Cookie cookie) {
        gotoPage(page);
        return drawPage(id, bm, pageW, pageH, patchX, patchY, patchW, patchH, cookie.cookiePtr);
    }

    public synchronized boolean updatePage(Bitmap bm, int page,
                                           int pageW, int pageH,
                                           int patchX, int patchY,
                                           int patchW, int patchH,
                                           Cookie cookie) {
        return updatePageInternal(id, bm, page, pageW, pageH, patchX, patchY, patchW, patchH, cookie.cookiePtr);
    }

    public synchronized PassClickResult passClickEvent(int page, float x, float y) {
        boolean changed = passClickEventInternal(id, page, x, y) != 0;

        switch (WidgetType.values()[getFocusedWidgetTypeInternal(id)]) {
            case TEXT:
                return new PassClickResultText(changed, getFocusedWidgetTextInternal(id));
            case LISTBOX:
            case COMBOBOX:
                return new PassClickResultChoice(changed, getFocusedWidgetChoiceOptions(id), getFocusedWidgetChoiceSelected(id));
            case SIGNATURE:
                return new PassClickResultSignature(changed, getFocusedWidgetSignatureState());
            default:
                return new PassClickResult(changed);
        }

    }

    public synchronized boolean setFocusedWidgetText(int page, String text) {
        boolean success;
        gotoPage(page);
        success = setFocusedWidgetTextInternal(id, text) != 0 ? true : false;

        return success;
    }

    public synchronized void setFocusedWidgetChoiceSelected(String[] selected) {
        setFocusedWidgetChoiceSelectedInternal(id, selected);
    }

    public synchronized String checkFocusedSignature() {
        return checkFocusedSignatureInternal(id);
    }

    public synchronized boolean signFocusedSignature(String keyFile, String password) {
        return signFocusedSignatureInternal(id, keyFile, password);
    }

    public synchronized LinkInfo[] getPageLinks(int page) {
        return getPageLinksInternal(id, page);
    }

    public synchronized RectF[] getWidgetAreas(int page) {
        return getWidgetAreasInternal(id, page);
    }

    public synchronized Annotation[] getAnnoations(int page) {
        return getAnnotationsInternal(id, page);
    }

    public synchronized RectF[] searchPage(int page, String text) {
        gotoPage(page);
        return searchPage(id, text);
    }

    public synchronized byte[] html(int page) {
        gotoPage(page);
        return textAsHtml(id);
    }

    public synchronized TextWord[][] textLines(int page) {
        gotoPage(page);
        TextChar[][][][] chars = text(id);

        // The text of the page held in a hierarchy (blocks, lines, spans).
        // Currently we don't need to distinguish the blocks level or
        // the spans, and we need to collect the text into words.
        ArrayList<TextWord[]> lns = new ArrayList<TextWord[]>();

        for (TextChar[][][] bl : chars) {
            if (bl == null)
                continue;
            for (TextChar[][] ln : bl) {
                ArrayList<TextWord> wds = new ArrayList<TextWord>();
                TextWord wd = new TextWord();

                for (TextChar[] sp : ln) {
                    for (TextChar tc : sp) {
                        if (tc.c != ' ') {
                            wd.Add(tc);
                        } else if (wd.w.length() > 0) {
                            wds.add(wd);
                            wd = new TextWord();
                        }
                    }
                }

                if (wd.w.length() > 0)
                    wds.add(wd);

                if (wds.size() > 0)
                    lns.add(wds.toArray(new TextWord[wds.size()]));
            }
        }

        return lns.toArray(new TextWord[lns.size()][]);
    }

    public synchronized void addMarkupAnnotation(int page, PointF[] quadPoints, Annotation.Type type) {
        gotoPage(page);
        addMarkupAnnotationInternal(id, quadPoints, type.ordinal());
    }

    public synchronized void addInkAnnotation(int page, PointF[][] arcs) {
        gotoPage(page);
        addInkAnnotationInternal(id, arcs);
    }

    public synchronized void deleteAnnotation(int page, int annot_index) {
        gotoPage(page);
        deleteAnnotationInternal(id, annot_index);
    }

    public synchronized boolean hasOutline() {
        return hasOutlineInternal(id);
    }

    public synchronized OutlineItem[] getOutline() {
        return getOutlineInternal(id);
    }

    public synchronized boolean needsPassword() {
        return needsPasswordInternal(id);
    }

    public synchronized boolean authenticatePassword(String password) {
        return authenticatePasswordInternal(id, password);
    }

    public synchronized boolean hasChanges() {
        return hasChangesInternal(id);
    }

    public synchronized void save() {
        saveInternal(id);
    }

    public synchronized String startProof(int resolution) {
        return startProofInternal(id, resolution);
    }

    public synchronized void endProof(String filename) {
        endProofInternal(id, filename);
    }

    public static boolean gprfSupported() {
        if (gs_so_available == false)
            return false;
        return gprfSupportedInternal();
    }

    public boolean canProof() {
        String format = fileFormat();
        if (format.contains("PDF"))
            return true;
        return false;
    }

    public synchronized int getNumSepsOnPage(int page) {
        return getNumSepsOnPageInternal(id, page);
    }

    public synchronized void controlSepOnPage(int page, int sep, boolean disable) {
        controlSepOnPageInternal(id, page, sep, disable);
    }

    public synchronized Separation getSep(int page, int sep) {
        return getSepInternal(id, page, sep);
    }


    /* The native functions */
    private static native boolean gprfSupportedInternal();

    private native long openFile(int id, String filename);

    private native long openBuffer(int id, String magic);

    private native String fileFormatInternal(int id);

    private native boolean isUnencryptedPDFInternal(int id);

    private native int countPagesInternal(int id);

    private native void gotoPageInternal(int id, int localActionPageNum);

    private native float getPageWidth(int id);

    private native float getPageHeight(int id);

    private native boolean drawPage(int id,
                                    Bitmap bitmap,
                                    int pageW, int pageH,
                                    int patchX, int patchY,
                                    int patchW, int patchH,
                                    long cookiePtr);

    private native boolean updatePageInternal(int id,
                                              Bitmap bitmap,
                                              int page,
                                              int pageW, int pageH,
                                              int patchX, int patchY,
                                              int patchW, int patchH,
                                              long cookiePtr);

    private native RectF[] searchPage(Object id, String text);

    private native TextChar[][][][] text(int id);

    private native byte[] textAsHtml(int id);

    private native void addMarkupAnnotationInternal(int id, PointF[] quadPoints, int type);

    private native void addInkAnnotationInternal(int id, PointF[][] arcs);

    private native void deleteAnnotationInternal(int id, int annot_index);

    private native int passClickEventInternal(int id, int page, float x, float y);

    private native void setFocusedWidgetChoiceSelectedInternal(int id, String[] selected);

    private native String[] getFocusedWidgetChoiceSelected(int id);

    private native String[] getFocusedWidgetChoiceOptions(int id);

    private native int getFocusedWidgetSignatureState();

    private native String checkFocusedSignatureInternal(int id);

    private native boolean signFocusedSignatureInternal(int id, String keyFile, String password);

    private native int setFocusedWidgetTextInternal(int id, String text);

    private native String getFocusedWidgetTextInternal(int id);

    private native int getFocusedWidgetTypeInternal(int id);

    private native LinkInfo[] getPageLinksInternal(int id, int page);

    private native RectF[] getWidgetAreasInternal(int id, int page);

    private native Annotation[] getAnnotationsInternal(int id, int page);

    private native OutlineItem[] getOutlineInternal(int id);

    private native boolean hasOutlineInternal(int id);

    private native boolean needsPasswordInternal(int id);

    private native boolean authenticatePasswordInternal(int id, String password);

    private native MuPDFAlertInternal waitForAlertInternal(int id);

    private native void replyToAlertInternal(int id, MuPDFAlertInternal alert);

    private native void startAlertsInternal(int id);

    private native void stopAlertsInternal(int id);

    private native void destroying(int id);

    private native boolean hasChangesInternal(int id);

    private native void saveInternal(int id);

    private native void dumpMemoryInternal(int id);

    private native long createCookie(int id);

    private native void destroyCookie(int id, long cookie);

    private native void abortCookie(int id, long cookie);

    private native String startProofInternal(int id, int resolution);

    private native void endProofInternal(int id, String filename);

    private native int getNumSepsOnPageInternal(int id, int page);

    private native void controlSepOnPageInternal(int id, int page, int sep, boolean disable);

    private native Separation getSepInternal(int id, int page, int sep);

    private native boolean javascriptSupported(int id);
}
