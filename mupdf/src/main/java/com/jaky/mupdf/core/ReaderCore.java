package com.jaky.mupdf.core;

/**
 * Created by Jack on 2017/11/28.
 */

public interface ReaderCore {
    boolean isSurportFormat();
    boolean openFile(String fileName);
    boolean closeFile();
    boolean openBuffer(String magic);
    boolean drawPage();
    boolean gotoPage();
    int getPageWidth();
    int getPageHeight();





}
