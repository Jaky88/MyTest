package com.jaky.data.db;


import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by jaky on 2017/12/5 0005.
 */

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {

    public static final String NAME = "AppDatabase";
    public static final int VERSION = 1;

}
