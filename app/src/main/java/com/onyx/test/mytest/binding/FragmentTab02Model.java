package com.onyx.test.mytest.binding;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.onyx.test.mytest.model.manager.ConfigManager;
import com.onyx.test.mytest.model.bean.ReaderSlideshowBean;
import com.onyx.test.mytest.model.utils.ShellUtils;

/**
 * Created by jaky on 2017/11/8 0008.
 */

public class FragmentTab02Model extends BaseObservable {

    private Context context;
    private ReaderSlideshowBean config;
    private String info;

    @Bindable
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public FragmentTab02Model(Context context) {
        this.context = context;
        this.config = ConfigManager.getConfig(context).getReaderSlideshowBean();
    }


    public void onStartClick(View view) {
        ShellUtils.CommandResult ret = ShellUtils.execCommand("cat /proc/partitions", false, true);
        System.out.printf("===========：%s", ret.successMsg);
        setInfo(ret.successMsg);
    }
}
