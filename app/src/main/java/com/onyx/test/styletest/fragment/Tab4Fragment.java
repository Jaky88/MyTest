package com.onyx.test.styletest.fragment;

import android.app.AlertDialog;
import android.app.Instrumentation;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.onyx.test.styletest.R;
import com.onyx.test.styletest.activity.MainActivity;
import com.onyx.test.styletest.utils.ActivityUtil;
import com.onyx.test.styletest.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab4Fragment extends BaseFragment implements View.OnClickListener {

    private static final int FILE_SELECT_CODE = 1;
    @Bind(R.id.btn_file_test)
    Button btnFileTest;
    @Bind(R.id.btn_send_notification)
    Button btnSendNotification;
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
    private NotificationManager notificationManager;
    private Context mContext = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab04, null);
        mContext = getActivity();
        audiomanage = (AudioManager) (mContext.getSystemService(Context.AUDIO_SERVICE));
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        ButterKnife.bind(this, view);
        initEvent();
        Log.d("", "");

        return view;
    }

    private void initEvent() {
        btnStartKreader.setOnClickListener(this);
        btnVolumeDown.setOnClickListener(this);
        btnVolumeAdd.setOnClickListener(this);
        btnFileTest.setOnClickListener(this);
        btnSendNotification.setOnClickListener(this);
        btnSelectFile.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_kreader:
                startKreader();
                break;
            case R.id.btn_volume_down:
                audiomanage.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                break;
            case R.id.btn_volume_add:
                audiomanage.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                break;
            case R.id.btn_file_test:
                readWriteFileTest();
                break;
            case R.id.btn_send_notification:
                sendNotification();
                break;
            case R.id.btn_select_file:
                openCurDir("mnt/sdcard");
                break;
            default:
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification() {
        Notification.Builder builder = new Notification.Builder(mContext);
        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("新通知");
        builder.setContentText("这个是通知的内容");
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        builder.setTicker("new message");
        builder.setOngoing(true);
        builder.setNumber(20);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        notificationManager.notify(0, notification);
        Toast.makeText(mContext, "发送成功！", Toast.LENGTH_SHORT).show();
    }

    private void readWriteFileTest() {
        if (FileUtil.saveContentToFile("test", "/mnt/hidden/test")) {
            Toast.makeText(mContext, "成功！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "失败！", Toast.LENGTH_SHORT).show();
        }
    }

    private void startKreader() {
        String fileName = "西游记.pdf";
        String filePath = FileUtil.getSDCardPath() + File.separator + "Books" + File.separator + fileName;
        ActivityUtil.startActivityWithData(getActivity(), "com.onyx.kreader", new File(filePath));
    }

    private void openCurDir(String curPath) {
        File f = new File(curPath);
        File[] file = f.listFiles();
        final List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();

        if (!curPath.equals("/")) {
            Map<String, Object> map1 = new HashMap<String, Object>();
            map1.put("name", "返回上一级目录");
            map1.put("image", R.mipmap.directory);
            map1.put("path", f.getParent());
            map1.put("isDire", true);
            listItem.add(map1);
        }

        if (file != null) {
            for (int i = 0; i < file.length; i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("name", file[i].getName());
                map.put("image", (file[i].isDirectory()) ? R.mipmap.directory : R.mipmap.file);
                map.put("path", file[i].getPath());
                map.put("isDire", file[i].isDirectory());
                listItem.add(map);
            }
        }

        SimpleAdapter adapter = new SimpleAdapter(mContext, listItem, R.layout.simple_adapter,
                new String[]{"name", "image"}, new int[]{R.id.adapter_filename, R.id.adapter_image});

        final AlertDialog.Builder b = new AlertDialog.Builder(mContext);
        b.setAdapter(adapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if ((Boolean) listItem.get(arg1).get("isDire")) {
                    openCurDir((String) listItem.get(arg1).get("path"));
                } else {
                    edtFileName.setText((String) listItem.get(arg1).get("path"));
                }
            }
        });
        b.show();
    }
}
