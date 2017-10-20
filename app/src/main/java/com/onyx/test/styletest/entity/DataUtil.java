package com.onyx.test.styletest.entity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.support.v4.util.Pair;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static com.onyx.test.styletest.BuildConfig.DEBUG;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: StyleTest
 * @Author: Jack
 * @Date: 2017/9/19 0019,0:57
 * @Version: V1.0
 * @Description: TODO
 */

public class DataUtil {
    private static final String TAG = DataUtil.class.getSimpleName();
    private List<Pair<String, String>> mDatas;
    private static DataUtil sInstance;
    private Context mContext;
    private String strLevel = "";


    private DataUtil(Context context) {
        mContext = context;
        context.registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public static DataUtil getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataUtil(context);
        }

        return sInstance;
    }

    public List<Pair<String, String>> getData() {
        if (mDatas == null) {
            mDatas = new ArrayList<>();
            mDatas.add(new Pair<>("编译信息：", ""));
            mDatas.add(new Pair<>("TYPE(类型)", Build.TYPE));
            mDatas.add(new Pair<>("SERIAL（序列号）", Build.SERIAL));
            mDatas.add(new Pair<>("HARDWARE", Build.HARDWARE));
            mDatas.add(new Pair<>("MODEL（型号）", Build.MODEL));
            mDatas.add(new Pair<>("BOARD（主板）", Build.BOARD));
            mDatas.add(new Pair<>("BRAND", Build.BRAND));
            mDatas.add(new Pair<>("DEVICE（设备）", Build.DEVICE));
            mDatas.add(new Pair<>("PRODUCT（产品）", Build.PRODUCT));
            mDatas.add(new Pair<>("VERSION_CODES.BASE（系统版本）", "" + Build.VERSION_CODES.BASE));
            mDatas.add(new Pair<>("VERSION.RELEASE（API等级）", "" + Build.VERSION.RELEASE));
            mDatas.add(new Pair<>("FINGERPRINT（系统指纹）", "" + Build.FINGERPRINT));
            mDatas.add(new Pair<>("SDK（sdk版本）", "" + Build.VERSION.SDK));

            mDatas.add(new Pair<>("ID(版本ID)", "" + Build.ID));
            mDatas.add(new Pair<>("DISPLAY（版本号）", "" + Build.DISPLAY));
            mDatas.add(new Pair<>("USER", "" + Build.USER));
            mDatas.add(new Pair<>("编译时间", "" + Build.TIME));

            mDatas.add(new Pair<>("kernel version", System.getProperty("os.version")));
            mDatas.add(new Pair<>("kernel name", System.getProperty("os.name")));
            mDatas.add(new Pair<>("kernel arch", System.getProperty("os.arch")));
            mDatas.add(new Pair<>("user.home", System.getProperty("user.home")));
            mDatas.add(new Pair<>("user.name", System.getProperty("user.name")));
            mDatas.add(new Pair<>("user.dir", System.getProperty("user.dir")));

            mDatas.add(new Pair<>("java.home ", System.getProperty("java.home ")));
            mDatas.add(new Pair<>("java.versio", System.getProperty("java.versio")));
            mDatas.add(new Pair<>("java.class.version", System.getProperty("java.class.version")));
            mDatas.add(new Pair<>("java.class.path", System.getProperty("java.class.path")));

            mDatas.add(new Pair<>("硬件信息：", ""));
            mDatas.add(new Pair<>("CPU概况：", CpuUtil.getCpuString()));
            mDatas.add(new Pair<>("CPU名字：", CpuUtil.getCpuName()));
            mDatas.add(new Pair<>("CPU类型：", CpuUtil.getCpuModel()));
            mDatas.add(new Pair<>("CPU ABI：", Build.CPU_ABI));
            mDatas.add(new Pair<>("CPU特性：", CpuUtil.getCpuFeature()));
            mDatas.add(new Pair<>("CPU最大频率：", "" + CpuUtil.getMaxCpuFreq()));
            mDatas.add(new Pair<>("CPU最小频率：", "" + CpuUtil.getMinCpuFreq()));
            mDatas.add(new Pair<>("CPU当前频率：", "" + CpuUtil.getCurCpuFreq()));
            mDatas.add(new Pair<>("移动信息：", CpuUtil.getMobileInfo()));
            mDatas.add(new Pair<>("内存大小：", "" + getTotalMemory()));
            mDatas.add(new Pair<>("Rom大小：", "" + getRomMemroy()[0]));
            mDatas.add(new Pair<>("内部存储大小：", "" + getTotalInternalMemorySize()));
            mDatas.add(new Pair<>("SD卡大小：", "" + getSDCardMemory()[0]));
            mDatas.add(new Pair<>("IP地址：", getIpAddress(mContext)));
            mDatas.add(new Pair<>("版本信息：", getVersion()[0]));
            mDatas.add(new Pair<>("当前电量：", strLevel));
            mDatas.add(new Pair<>("开机时间：", getTimes()));
            mDatas.add(new Pair<>("MAC地址：", getOtherInfo()[0]));
        }
        return mDatas;
    }

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            //  level加%就是当前电量了
            strLevel = level + "%";
        }
    };

    public String[] getOtherInfo() {
        String[] other = {"null", "null"};
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getMacAddress() != null) {
            other[0] = wifiInfo.getMacAddress();
        } else {
            other[0] = "Fail";
        }
        other[1] = getTimes();
        return other;
    }

    private String getTimes() {
        long ut = SystemClock.elapsedRealtime() / 1000;
        if (ut == 0) {
            ut = 1;
        }
        int m = (int) ((ut / 60) % 60);
        int h = (int) ((ut / 3600));
        return h + " " + "小时" + m + " "
                + "分钟";
    }


    public static long getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            if (str2 != null) {
                arrayOfString = str2.split("\\s+");
                initial_memory = Integer.valueOf(arrayOfString[1]).intValue() / 1024;
            }
            localBufferedReader.close();
            return initial_memory;
        } catch (IOException e) {
            return -1;
        }
    }

    public static String getIpAddress(Context mContext) {
        String ipAddress = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ipAddress = inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            return null;
        }
        if (DEBUG) {
            Log.d(TAG, "ip address:" + ipAddress);
        }
        return ipAddress;
    }

    public long[] getRomMemroy() {
        long[] romInfo = new long[2];
        //Total rom memory
        romInfo[0] = getTotalInternalMemorySize();

        //Available rom memory
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        romInfo[1] = blockSize * availableBlocks;
//        getVersion();
        return romInfo;
    }

    public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    public long[] getSDCardMemory() {
        long[] sdCardInfo = new long[2];
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long bSize = sf.getBlockSize();
            long bCount = sf.getBlockCount();
            long availBlocks = sf.getAvailableBlocks();

            sdCardInfo[0] = bSize * bCount;//总大小
            sdCardInfo[1] = bSize * availBlocks;//可用大小
        }
        return sdCardInfo;
    }


    public String[] getVersion() {
        String[] version = {"null", "null", "null", "null"};
        String str1 = "/proc/version";
        String str2;
        String[] arrayOfString;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            version[0] = arrayOfString[2];//KernelVersion
            localBufferedReader.close();
        } catch (IOException e) {
        }
        version[1] = Build.VERSION.RELEASE;// firmware version
        version[2] = Build.MODEL;//model
        version[3] = Build.DISPLAY;//system version
        return version;
    }


}
