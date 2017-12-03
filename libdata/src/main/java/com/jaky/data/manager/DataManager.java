package com.jaky.data.manager;

import android.content.Context;

/**
 * Created by Jack on 2017/12/3.
 */

public class DataManager implements IDataManager {
    private static DataManager dataManager;
    private JsonHelper jsonHelper;
    private PreferHelper preferHelper;
    private DatabaseHelper dbHelper;
    private RemoteHelper providerHelper;
    private CloudHelper cloudHelper;

    private DataManager(Context context) {
        jsonHelper = new JsonHelper(context);
        preferHelper = new PreferHelper(context);
        dbHelper = new DatabaseHelper(context);
        providerHelper = new RemoteHelper(context);
        cloudHelper = new CloudHelper(context);
    }

    @Override
    public IDataManager init(Context context) {
        if(dataManager ==null){
            dataManager = new DataManager(context);
        }
        return dataManager;
    }

    @Override
    public void loadData() {
        jsonHelper.loadBean();
    }

    @Override
    public void saveData() {

    }

    @Override
    public JsonHelper getJsonHelper() {
        return jsonHelper;
    }

    @Override
    public PreferHelper getPreferHelper() {
        return preferHelper;
    }

    @Override
    public DatabaseHelper getDatabaseHelper() {
        return dbHelper;
    }

    @Override
    public RemoteHelper getRemoteHelper() {
        return providerHelper;
    }

    @Override
    public CloudHelper getCloudHelper() {
        return cloudHelper;
    }
}
