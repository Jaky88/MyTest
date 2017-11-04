package com.onyx.test.mytest.data;

/**
 * Created by jaky on 2017/10/27 0027.
 */

public class DataManager {

    private static DataManager sInstance;
    private int displayWidth;
    private int displayHeight;

    private void DataManager() {

    }

    public static DataManager getInstance() {
        if (sInstance == null) {
            sInstance = new DataManager();
        }
        return sInstance;
    }


    public void setDisplaySize(int width, int height) {
        displayWidth = width;
        displayHeight = height;
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public int getDisplayHeight() {
        return displayHeight;
    }
}
