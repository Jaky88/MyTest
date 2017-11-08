package com.onyx.test.mytest.ui.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.onyx.test.mytest.R;
import com.onyx.test.mytest.databinding.FragmentTab2Binding;
import com.onyx.test.mytest.databinding.FragmentTab3Binding;
import com.onyx.test.mytest.translator.TranslateManager;
import com.onyx.test.mytest.translator.config.Language;
import com.onyx.test.mytest.translator.config.TranslatePlatform;
import com.onyx.test.mytest.viewmodel.FragmentTab02Model;
import com.onyx.test.mytest.viewmodel.FragmentTab03Model;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab3Fragment extends BaseFragment<FragmentTab3Binding> {
    private FragmentTab03Model bean;

    @Override
    public int getLayout() {
        return R.layout.fragment_tab3;
    }

    @Override
    public void bindData() {
        bean = new FragmentTab03Model(Tab3Fragment.this, config);
        bindingView.setBean(bean);
    }
}
