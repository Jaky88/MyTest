package com.onyx.test.mytest.binding;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.BaseObservable;
import android.util.Log;

import java.util.List;
import android.os.PowerManager.WakeLock;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;


/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: MyTest
 * @Author: Jack
 * @Date: 2017/11/8 0008,21:59
 * @Version: V1.0
 * @Description: TODO
 */

public class FragmentTab06Model extends BaseObservable {

    private Context context;
    private String appsList;
    private String currentList = "";
    private XSharedPreferences prefs;
    final String lock = "SampleWakeLock";
    final String app = "samplewakelock";

    public FragmentTab06Model(Context context) {
        this.context = context;
        prefs = new XSharedPreferences("PackageName", "WLTSettings");
        getInstalledAppPermissions();
        getCurrentWakeLocks();

    }

    public String getAppsList() {
        return appsList;
    }

    public void setAppsList(String appsList) {
        this.appsList = appsList;
    }

    public String getCurrentList() {
        return currentList;
    }

    public void setCurrentList(String currentList) {
        this.currentList = currentList;
    }

    public void getInstalledAppPermissions() {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            Log.d("WakeLockDetector", "App: " + applicationInfo.name + " Package: " + applicationInfo.packageName);

            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);

                //Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        //Log.d("WakeLockDetector", requestedPermissions[i]);
                        if (requestedPermissions[i].equals("android.permission.WAKE_LOCK")) {
                            appsList += applicationInfo.packageName + "\n";
                        }

                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void getCurrentWakeLocks() {

        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (int i = 0; i < recentTasks.size(); i++) {
            String appShortName = recentTasks.get(i).baseActivity.toShortString();
            String pkgName = recentTasks.get(i).baseActivity.getPackageName();
            String lockAppName = getLockFromApps(appShortName);
            currentList += (lockAppName != null)
                    ? "进程ID: " + recentTasks.get(i).id + ", 包名：" + pkgName + ", 锁名: " + lockAppName + "\n"
                    : "进程ID: " + recentTasks.get(i).id + "\n";
            Log.d("Executed app", "Application executed : " + "\t\t ID: " + recentTasks.get(i).id + "");
        }
    }

    public String getLockFromApp(String appName) {
        return (appName.contains(app)) ? lock : null;
    }

    public String getLockFromApps(final String appName) {
        Log.d("=====", "============getLockFromApps=========");
        if (prefs.getBoolean((new StringBuilder(String.valueOf(appName))).append("/preventWakeLock").toString(), false)) {

            XposedHelpers.findAndHookMethod(WakeLock.class, "acquire", new Object[]{
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            if (prefs.getBoolean((new StringBuilder(String.valueOf(appName))).append("/preventWakeLock").toString(), false)) {

                                String s = (String) XposedHelpers.getObjectField((android.os.PowerManager.WakeLock) param.thisObject, "mTag");
                                String s1 = prefs.getString((new StringBuilder(String.valueOf(appName))).append("/filterWakeLockTags").toString(), "");
                                if (s1.equals("")) {
                                    s1 = "-1";
                                }
                                if (s1.equals("-1") || s1.contains(s)) {
                                    param.setResult(null);
                                    Log.d("===================","===beforeHookedMethod=====acquire()============");
                                }
                            }
                        }
                    }
            });

            //================================release===========================
            Object aobj1[] = new Object[1];
            aobj1[0] = new XC_MethodHook() {

                //final MainXposedHook this$0;
                private final LoadPackageParam val$lpparam = null;

                protected void beforeHookedMethod(MethodHookParam methodhookparam)
                        throws Throwable {
                    if (prefs.getBoolean((new StringBuilder(String.valueOf(appName))).append("/preventWakeLock").toString(), false)) {
                        String s = (String) XposedHelpers.getObjectField((android.os.PowerManager.WakeLock) methodhookparam.thisObject, "mTag");
                        String s1 = prefs.getString((new StringBuilder(String.valueOf(appName))).append("/filterWakeLockTags").toString(), "-1");
                        if (s1.equals("")) {
                            s1 = "-1";
                        }
                        if (s1.equals("-1") || s1.contains(s)) {
                            methodhookparam.setResult(null);
                            Log.d("===================","===beforeHookedMethod=====release()============");
                        }
                    }
                }

            };
            XposedHelpers.findAndHookMethod(WakeLock.class, "release", aobj1);


        }
        return lock;
    }
}