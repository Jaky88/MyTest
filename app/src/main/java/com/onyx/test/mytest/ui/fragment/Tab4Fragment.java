package com.onyx.test.mytest.ui.fragment;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.CompoundButton;

import com.onyx.test.mytest.R;
import com.onyx.test.mytest.binding.FragmentTab04Model;
import com.onyx.test.mytest.databinding.FragmentTab4Binding;
import com.onyx.test.mytest.model.utils.WifiConnector;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab4Fragment extends BaseFragment<FragmentTab4Binding> {

    private FragmentTab04Model bean;
    private Context mContext = null;
    private WifiManager wifiManager;
    private WifiConnector wac;

    @Override
    public int getLayout() {
        return R.layout.fragment_tab4;
    }

    @Override
    public void bindData() {
        bean = new FragmentTab04Model(Tab4Fragment.this, config);
        bindingView.setBean(bean);
        initWifiConnect();
        bindingView.cbOpenWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (wac.openWifi()) {
                        buttonView.setText("关闭wifi");
                    }
                } else {
                    if (wac.closeWifi()) {
                        buttonView.setText("打开wifi");
                    }
                }
            }
        });
    }

    private void initWifiConnect() {
        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        wac = new WifiConnector(wifiManager);
    }


}
