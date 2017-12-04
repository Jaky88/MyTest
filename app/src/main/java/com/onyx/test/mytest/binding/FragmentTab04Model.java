package com.onyx.test.mytest.binding;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.jaky.utils.FileUtil;
import com.onyx.test.mytest.R;
import com.onyx.test.mytest.model.manager.ConfigManager;
import com.onyx.test.mytest.model.bean.ReaderSlideshowBean;
import com.jaky.utils.ActivityUtil;
import com.onyx.test.mytest.model.utils.WifiConnector;
import com.onyx.test.mytest.view.activity.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jaky on 2017/11/8 0008.
 */

public class FragmentTab04Model extends BaseObservable {
    
    private Context context;
    private ReaderSlideshowBean config;
    private String selectfilePath;
    private boolean wifiChecked = false;

    private AudioManager audiomanage;
    private NotificationManager notificationManager;
    private WifiManager wifiManager;
    private WifiConnector wac;
    private String mPwd = "OnyxWpa2017";
    private String mSSID = "onyx-office1";


    public FragmentTab04Model(Context context) {
        this.context = context;
        this.config = ConfigManager.getConfig(context).getReaderSlideshowBean();
        audiomanage = (AudioManager) (context.getSystemService(Context.AUDIO_SERVICE));
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        initWifiConnect();
    }

        private void initWifiConnect() {
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wac = new WifiConnector(wifiManager);
        ConnectivityManager manager = (ConnectivityManager)context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        setWifiChecked((wifi == NetworkInfo.State.CONNECTED||wifi== NetworkInfo.State.CONNECTING));
    }

    @Bindable
    public boolean isWifiChecked() {
        return wifiChecked;
    }

    public void setWifiChecked(boolean wifiChecked) {
        this.wifiChecked = wifiChecked;
    }

    @Bindable
    public String getSelectfilePath() {
        return selectfilePath;
    }

    public void setSelectfilePath(String selectfilePath) {
        this.selectfilePath = selectfilePath;
    }


    public void onEnableAdbClick(View view) {
        openADB();
    }

    public void onOpenPdfClick(View view) {
        openPDF();
    }

    public void onConnectWifiClick(View view) {
        connectWIFI();
    }

    public void onWifiCheckedChanged(final boolean isChecked) {
        if (isChecked) {
            setWifiChecked(wac.openWifi());
        } else {
            setWifiChecked(!wac.closeWifi());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onSendNotificationClick(View view) {
        sendNotification();
    }

    public void onFileTestClick(View view) {
        readWriteFileTest();
    }

    public void onVolumeDownClick(View view) {
        audiomanage.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
    }

    public void onVolumeAddClick(View view) {
        audiomanage.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }

    public void onStartReaderClick(View view) {
        startReader();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification() {
        Notification.Builder builder = new Notification.Builder(context);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("新通知");
        builder.setContentText("这个是通知的内容");
        builder.setWhen(System.currentTimeMillis());
        builder.setAutoCancel(true);
        builder.setTicker("new message");
        builder.setOngoing(true);
        builder.setNumber(20);
        builder.setPriority(Notification.PRIORITY_MIN);
//        builder.setCustomBigContentView()

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        setEventInfo(pendingIntent, notification);
        notificationManager.notify(0, notification);
        Toast.makeText(context, "发送成功！", Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setEventInfo(PendingIntent pendingIntent, Notification notification) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            notification.setLatestEventInfo(this, this.getString(R.string.app_name), "===", pendingIntent);
        }
    }

    private void readWriteFileTest() {
        if (FileUtil.saveContentToFile("test", "/mnt/hidden/test")) {
            Toast.makeText(context, "成功！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "失败！", Toast.LENGTH_SHORT).show();
        }
    }

    private void startReader() {
        if(selectfilePath ==null || selectfilePath.isEmpty()){
            selectfilePath = FileUtil.getSDCardPath() + File.separator + File.separator + "test.pdf";
        }
        ActivityUtil.startActivityWithData(context, "com.onyx.test.mytest", new File(selectfilePath));
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

        SimpleAdapter adapter = new SimpleAdapter(context, listItem, R.layout.simple_adapter,
                new String[]{"name", "image"}, new int[]{R.id.adapter_filename, R.id.adapter_image});

        final AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setAdapter(adapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if ((Boolean) listItem.get(arg1).get("isDire")) {
                    openCurDir((String) listItem.get(arg1).get("path"));
                } else {
                    setSelectfilePath((String) listItem.get(arg1).get("path"));
                }
            }
        });
        b.show();
    }

    private void openPDF() {
//        ActivityUtil.startActivity(mContext, MuPDFActivity.class);
    }

    private void openADB() {

    }

    private void connectWIFI() {
        try {
            wac.connect(mSSID, mPwd, mPwd.equals("") ? WifiConnector.WifiCipherType.WIFICIPHER_NOPASS : WifiConnector.WifiCipherType.WIFICIPHER_WPA);
        } catch (Exception e) {
            Log.d("===", "============" + e.getMessage());
        }
    }
}
