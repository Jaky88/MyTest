package com.onyx.test.styletest.fragment;

import android.app.Instrumentation;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.onyx.test.styletest.R;
import com.onyx.test.styletest.utils.ActivityUtil;
import com.onyx.test.styletest.utils.FileUtil;
import com.onyx.test.styletest.utils.ReflectionUtil;

import java.io.File;
import java.lang.reflect.Method;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab4Fragment extends BaseFragment implements View.OnClickListener {

    private Context mContext = null;
    @Bind(R.id.btn_start_kreader)
    Button btnStartKreader;
    @Bind(R.id.edt_file_name)
    EditText edtFileName;
    @Bind(R.id.btn_select_file)
    Button btnSelectFile;
    @Bind(R.id.btn_volume_down)
    Button btnVolumeDown;
    @Bind(R.id.btn_volume_add)
    Button btnVolumeAdd;
    private AudioManager audiomanage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab04, null);
        mContext = getActivity();
        audiomanage = (AudioManager)(mContext.getSystemService(Context.AUDIO_SERVICE));
        ButterKnife.bind(this, view);
        initEvent();
        return view;
    }

    private void initEvent() {
        btnStartKreader.setOnClickListener(this);
        btnVolumeDown.setOnClickListener(this);
        btnVolumeAdd.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_kreader:
                startKreader();
                break;
            case R.id.btn_volume_down:
                Log.d("=======", "============btn_volume_down================");
                audiomanage.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                break;
            case R.id.btn_volume_add:
                Log.d("=======", "============btn_volume_add================");
                audiomanage.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                break;
            default:
                break;
        }
    }

    public void sendKeyEvent(int code, int event, boolean down) {
//        try {
//            KeyEvent ev = new KeyEvent(0, 0, down?KeyEvent.ACTION_DOWN:KeyEvent.ACTION_UP, code, 0, 0, 0, event, 8);
//            (IWindowManager.Stub
//                    .asInterface(ServiceManager.getService("window")))
//                    .injectKeyEvent_status_bar(ev, true);
//        } catch (RemoteException e) {
//        }
    }

    private void startKreader() {
        String fileName = "西游记.pdf";
        String filePath = FileUtil.getSDCardPath() + File.separator + "Books" + File.separator + fileName;
        ActivityUtil.startActivityWithData(getActivity(), "com.onyx.kreader", new File(filePath));
    }

    private void sendKeyCode(final int keyCode) {
//        try {
//            String keyCommand = "input keyevent " + keyCode;
//            Runtime.getRuntime().exec(keyCommand);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        new Thread(new Runnable() {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    Log.d("=======", "====================" + e.getMessage());
                }
            }
        }).start();
    }

}
