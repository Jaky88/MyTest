package com.onyx.test.styletest.data.model;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.ModelAdapter;

import java.util.List;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: MyTest
 * @Author: Jack
 * @Date: 2017/10/21 0021,0:36
 * @Version: V1.0
 * @Description: TODO
 */

public class MetadataDao {

    private Metadata mData;
    private ModelAdapter<Metadata> mAdapter;

    public void init(){
        mData = new Metadata();
        mAdapter = FlowManager.getModelAdapter(Metadata.class);

        mData.id = 1;
        mData.name = "张三";
    }

    public void insert(Metadata data){
        mAdapter.insert(data);
//        data.insert();
    }

    public void delete(Metadata data){
        mAdapter.delete(data);
//        data.delete();
    }

    public void update(Metadata data){
//        mAdapter.update(data);
//        data.update();
        SQLite.update(Metadata.class).set(Metadata_Table.name.eq("888")).where(Metadata_Table.idString.eq("")).execute();
    }

    public void select(Metadata data){

        List<Metadata> list = SQLite.select().from(Metadata.class).queryList();
    }
}
