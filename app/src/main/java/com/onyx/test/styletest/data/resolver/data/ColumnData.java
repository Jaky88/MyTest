package com.onyx.test.styletest.data.resolver.data;

import android.database.Cursor;

import com.onyx.android.sdk.data.compatability.CursorUtil;

import static android.provider.BaseColumns._ID;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: MyTest
 * @Author: Jack
 * @Date: 2017/10/21 0021,0:49
 * @Version: V1.0
 * @Description: TODO
 */

public class ColumnData extends BaseColumn {

    public static String SCHEME = "content://";
    public static String PROVIDER_AUTHORITY = "com.onyx.android.sdk.OnyxCmsProvider";
    public static String DB_TABLE_NAME = "library_scribble";

    public static String MD5 = "MD5";
    public static String PAGE = "Page";
    public static String COLOR = "Color";
    public static String THICKNESS = "Thickness";
    public static String POINTS = "Points";
    public static String UPDATE_TIME = "UpdateTime";
    public static String APPLICATION = "Application";
    public static String POSITION = "Position";
    public static String POINTS_BLOB = "PointsBlob";
    public static String UNIQUE_ID = "uniqueId";

    private Long id;
    private String md5;
    private Integer page;
    private Integer color;
    private double thickness;
    private String update_time;
    private String application;
    private String position;
    private byte[] pts;
    private String uniqueId;

    public ColumnData() {
        url = SCHEME + PROVIDER_AUTHORITY + "/" + DB_TABLE_NAME;
    }

    @Override
    public void read(Cursor c) {
        id = CursorUtil.getLong(c, c.getColumnIndex(_ID));
        md5 = CursorUtil.getString(c, c.getColumnIndex(MD5));
        page = CursorUtil.getInt(c, c.getColumnIndex(PAGE));
        color = CursorUtil.getInt(c, c.getColumnIndex(COLOR));
        thickness = c.getDouble(c.getColumnIndex(THICKNESS));
        update_time = CursorUtil.getString(c, c.getColumnIndex(UPDATE_TIME));
        application = CursorUtil.getString(c, c.getColumnIndex(APPLICATION));
        position = CursorUtil.getString(c, c.getColumnIndex(POSITION));
        pts = CursorUtil.getBlob(c, c.getColumnIndex(POINTS_BLOB));
        uniqueId = CursorUtil.getString(c, c.getColumnIndex(UNIQUE_ID));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public double getThickness() {
        return thickness;
    }

    public void setThickness(double thickness) {
        this.thickness = thickness;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public byte[] getPts() {
        return pts;
    }

    public void setPts(byte[] pts) {
        this.pts = pts;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
