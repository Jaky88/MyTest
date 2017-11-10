package com.onyx.test.mytest.binding;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.onyx.test.mytest.BR;
import com.onyx.test.mytest.R;
import com.onyx.test.mytest.model.utils.DeviceInfoHelper;

import java.util.ArrayList;
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
    public List<RecycleViewItemModel> itemViewModel = new ArrayList<>();
    public final ItemView itemView = ItemView.of(BR.itemModel, R.layout.item_recycleview);

    public FragmentTab05Model(Context context) {
        this.context = context;
        itemViewModel = DeviceInfoHelper.getInstance(context).getItemModel();
    }

    @Bindable
    public List<RecycleViewItemModel> getItemViewModel() {
        return itemViewModel;
    }

    public void setItemViewModel(List<RecycleViewItemModel> itemViewModel) {
        this.itemViewModel = itemViewModel;
    }
}
