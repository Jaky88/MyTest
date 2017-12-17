package com.onyx.test.mytest.binding;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;

import com.jaky.utils.ShellUtils;
import com.onyx.test.mytest.BR;
import com.onyx.test.mytest.R;
import com.onyx.test.mytest.model.bean.ReaderSlideshowBean;
import com.onyx.test.mytest.model.manager.ConfigManager;

import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;

/**
 * Created by jaky on 2017/11/8 0008.
 */

public class FragmentTab02Model extends BaseObservable {

    private static final int DEFAULT_SCREEN_OFF_TIMEOUT = -1;
    private static final String  AUTO_POWEROFF_TIMEOUT = "auto_poweroff_timeout";
    private Context context;
    private ReaderSlideshowBean config;
    private String info = "status：";
    private String screenTimeout = "0";
    private String poweroffTimeout = "0";
    private String strCommand = "cat /proc/partitions";
    private SettingsObserver mSettingsObserver;
    private final Object mLock = new Object();
    private final ContentResolver resolver;

    public FragmentTab02Model(Context context) {
        this.context = context;
        this.config = ConfigManager.getConfig(context).getReaderSlideshowBean();
        resolver = context.getContentResolver();
        mSettingsObserver = new SettingsObserver(mHandler);
        resolver.registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_OFF_TIMEOUT), true, mSettingsObserver);
        resolver.registerContentObserver(Settings.System.getUriFor(AUTO_POWEROFF_TIMEOUT), true, mSettingsObserver);
        initData();
    }

    private void initData() {
        screenTimeout = String.valueOf(Settings.System.getInt(resolver, Settings.System.SCREEN_OFF_TIMEOUT, DEFAULT_SCREEN_OFF_TIMEOUT));
        poweroffTimeout = String.valueOf(Settings.System.getInt(resolver, AUTO_POWEROFF_TIMEOUT, DEFAULT_SCREEN_OFF_TIMEOUT));
//        setInfo("screenTimeout: "+screenTimeout +", poweroffTimeout: "+poweroffTimeout);
        int a = Integer.MAX_VALUE;
        setInfo("screenTimeout: "+screenTimeout +", poweroffTimeout: "+poweroffTimeout +" ,a: "+ a);
    }

    public String getPoweroffTimeout() {
        return poweroffTimeout;
    }

    public void setPoweroffTimeout(String poweroffTimeout) {
        this.poweroffTimeout = poweroffTimeout;
    }

    @Bindable
    public String getStrCommand() {
        return strCommand;
    }

    public void setStrCommand(String strCommand) {
        this.strCommand = strCommand;
    }

    @Bindable
    public String getScreenTimeout() {
        return screenTimeout;
    }

    public void setScreenTimeout(String screenTimeout) {
        this.screenTimeout = screenTimeout;
    }

    @Bindable
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
        notifyPropertyChanged(BR.info);
    }

    public void onStartClick(View view) {
        ShellUtils.CommandResult ret = ShellUtils.execCommand(strCommand, false, true);
        if (ret.successMsg != null) {
            setInfo(ret.successMsg);
        } else if (ret.errorMsg != null) {
            setInfo(ret.errorMsg);
        } else {
            setInfo(ret.toString());
        }

    }

    public void onSetScreenTimeout(View view) {
        if (screenTimeout == null || screenTimeout.isEmpty()) {
            return;
        }

        Settings.System.putLong(context.getContentResolver(), SCREEN_OFF_TIMEOUT, Long.parseLong(screenTimeout));
    }

    public void onSetPoweroffTimeout(View view) {
        if (poweroffTimeout == null || poweroffTimeout.isEmpty()) {
            return;
        }

        Settings.System.putLong(context.getContentResolver(), AUTO_POWEROFF_TIMEOUT, Long.parseLong(poweroffTimeout));
    }

    private final class SettingsObserver extends ContentObserver {
        private Handler handler;

        public SettingsObserver(Handler handler) {
            super(handler);
            this.handler = handler;
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            handleSettingsChanged();
        }
    }

    private void handleSettingsChanged() {
        updateSettingsInfo();
    }

    private void updateSettingsInfo() {
        int screenTimeout = Settings.System.getInt(resolver, Settings.System.SCREEN_OFF_TIMEOUT, DEFAULT_SCREEN_OFF_TIMEOUT);
        String poweroffTimeout = String.valueOf(Settings.System.getInt(resolver, AUTO_POWEROFF_TIMEOUT, DEFAULT_SCREEN_OFF_TIMEOUT));
//        setInfo("screenTimeout:" + matchValue(screenTimeout) + ", poweroffTimeout:" + matchValue(poweroffTimeout));
        setInfo("screenTimeout:" + matchValue(screenTimeout));
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    break;
                default:
                    break;
            }
        }
    };

    public void destroy() {
        resolver.unregisterContentObserver(mSettingsObserver);
    }

    private String matchValue(int target) {
//        int target = convertMaxIntegerValue(Long.parseLong(value));
        final CharSequence[] values = context.getResources().getStringArray(R.array.screen_timeout_values);
        return String.valueOf(values[getIndex(target, values)]);
    }

    private int getIndex(int target, CharSequence[] array) {
        int index = array.length - 1;

        if (0 <= target && target <= Integer.parseInt(array[0].toString())) {
            return 0;
        }

        for (int i = 0; i < array.length - 1; i++) {
            long num = Long.parseLong(array[i].toString());
            if (target > num) {
                index = i + 1;
            } else if (target == num) {
                index = i;
                break;
            }
        }
        return index;
    }

//    private int convertMaxIntegerValue(long value) {
//        if (value >= 0 && value <= Integer.MAX_VALUE) {
//            return (int) value;
//        } else {
//            return -1;
//        }
//    }
}
