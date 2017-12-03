package com.jaky.data.manager;

import android.content.Context;

/**
 * Created by Jack on 2017/12/3.
 */

public interface IDataManager {
    IDataManager init(Context context);
    void loadData();
    void saveData();

    //json file
    JsonHelper getJsonHelper();

    //xml file
    PreferHelper getPreferHelper();

    //db file
    DatabaseHelper getDatabaseHelper();


    RemoteHelper getRemoteHelper();

    //net file
    CloudHelper getCloudHelper();
}
