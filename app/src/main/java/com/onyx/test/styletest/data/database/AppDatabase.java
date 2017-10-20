package com.onyx.test.styletest.data.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: MyTest
 * @Author: Jack
 * @Date: 2017/10/20 0020,23:53
 * @Version: V1.0
 * @Description: TODO
 */

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {
    public static final String NAME = "AppDatabase";
    public static final int VERSION = 1;
}
