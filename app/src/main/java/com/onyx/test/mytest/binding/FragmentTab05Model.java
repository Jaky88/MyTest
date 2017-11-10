package com.onyx.test.mytest.binding;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.v4.util.Pair;

import com.onyx.test.mytest.BR;
import com.onyx.test.mytest.R;
import com.onyx.test.mytest.model.bean.ReaderSlideshowBean;
import com.onyx.test.mytest.model.entity.DataUtil;
import com.onyx.test.mytest.model.manager.ConfigManager;

import java.util.List;

import me.tatarka.bindingcollectionadapter.ItemView;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: MyTest
 * @Author: Jack
 * @Date: 2017/11/8 0008,21:59
 * @Version: V1.0
 * @Description: TODO
 */

public class FragmentTab05Model extends BaseObservable {

    private Context context;
    private ReaderSlideshowBean config;
    private List<Pair<String, String>> mDatas;
    public final ObservableList<Pair<String, String>> itemViewModel = new ObservableArrayList<>();
    public final ItemView itemView = ItemView.of(BR.itemModel, R.layout.item_recycleview);


    public FragmentTab05Model(Context context) {
        this.context = context;
        this.config = ConfigManager.getConfig(context).getReaderSlideshowBean();
        initData();
    }

    private void initData() {
        mDatas = DataUtil.getInstance(context).getData();
        itemViewModel.addAll(mDatas);
    }

    public ReaderSlideshowBean getConfig() {
        return config;
    }

    public void setConfig(ReaderSlideshowBean config) {
        this.config = config;
    }

    @Bindable
    public List<Pair<String, String>> getDatas() {
        return mDatas;
    }

    public void setDatas(List<Pair<String, String>> datas) {
        mDatas = datas;
    }


}
