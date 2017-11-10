package com.onyx.test.mytest.binding;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * Created by jaky on 2017/11/10 0010.
 */

public class RecycleViewItemModel extends BaseObservable {

    private String key;
    private String value;

    public RecycleViewItemModel(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Bindable
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Bindable
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
