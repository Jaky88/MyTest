package com.onyx.test.styletest.data.model;

import com.onyx.test.styletest.data.database.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: MyTest
 * @Author: Jack
 * @Date: 2017/10/20 0020,23:55
 * @Version: V1.0
 * @Description: TODO
 */

@Table(database = AppDatabase.class)
public class ReaderData extends BaseModel {
    @PrimaryKey(autoincrement = true)//ID自增
    public long id;

    @Column
    public String application;

}
