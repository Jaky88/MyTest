package com.onyx.test.styletest.entity;

import android.os.Build;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: StyleTest
 * @Author: Jack
 * @Date: 2017/9/19 0019,1:19
 * @Version: V1.0
 * @Description: TODO
 */

public class CpuUtil {

    private final static String kCpuInfoMaxFreqFilePath = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";

    public static CPUInfo getCPUInfo() {
        String strInfo = null;
        try {
            byte[] bs = new byte[1024];
            RandomAccessFile reader = new RandomAccessFile("/proc/cpuinfo", "r");
            reader.read(bs);
            String ret = new String(bs);
            int index = ret.indexOf(0);
            if (index != -1) {
                strInfo = ret.substring(0, index);
            } else {
                strInfo = ret;
            }
        } catch (IOException ex) {
            strInfo = "";
            ex.printStackTrace();
        }

        CPUInfo info = parseCPUInfo(strInfo);
        info.mCPUMaxFreq = getMaxCpuFreq();

        return info;
    }

    public static String getMobileInfo() {
        //StringBuffer sb = new StringBuffer();
        JSONObject mbInfo = new JSONObject();
        try {

            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                //sb.append(name + "=" + value);
                //sb.append("\n");
                mbInfo.put(name, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //return sb.toString();
        return mbInfo.toString();
    }


    static public String getCpuString(){
        if(Build.CPU_ABI.equalsIgnoreCase("x86")){
            return "Intel";
        }

        String strInfo = "";
        try
        {
            byte[] bs = new byte[1024];
            RandomAccessFile reader = new RandomAccessFile("/proc/cpuinfo", "r");
            reader.read(bs);
            String ret = new String(bs);
            int index = ret.indexOf(0);
            if(index != -1) {
                strInfo = ret.substring(0, index);
            } else {
                strInfo = ret;
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }

        return strInfo;
    }

    static public String getCpuType(){
        String strInfo = getCpuString();
        String strType = null;

        if (strInfo.contains("ARMv5")) {
            strType = "armv5";
        } else if (strInfo.contains("ARMv6")) {
            strType = "armv6";
        } else if (strInfo.contains("ARMv7")) {
            strType = "armv7";
        }else if (strInfo.contains("AArch64")) {
            strType = "AArch64";
        } else if (strInfo.contains("Intel")){
            strType = "x86";
        }else{
            strType = "unknown";
            return strType;
        }

        if (strInfo.contains("neon")) {
            strType += "_neon";
        }else if (strInfo.contains("vfpv3")) {
            strType += "_vfpv3";
        }else if (strInfo.contains(" vfp")) {
            strType += "_vfp";
        }else{
            strType += "_none";
        }

        return strType;
    }

    public static int getMaxCpuFreq() {
        int result = 0;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(kCpuInfoMaxFreqFilePath);
            br = new BufferedReader(fr);
            String text = br.readLine();
            if (text != null) {
                result = Integer.parseInt(text.trim());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null)
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return result;
    }

    public static String getMinCpuFreq() {

        String result = "";

        ProcessBuilder cmd;

        try {

            String[] args = { "/system/bin/cat",

                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq" };

            cmd = new ProcessBuilder(args);

            Process process = cmd.start();

            InputStream in = process.getInputStream();

            byte[] re = new byte[24];

            while (in.read(re) != -1) {

                result = result + new String(re);

            }

            in.close();

        } catch (IOException ex) {

            ex.printStackTrace();

            result = "N/A";

        }

        return result.trim();

    }

    public static String getCurCpuFreq() {

        String result = "N/A";

        try {

            FileReader fr = new FileReader(

                    "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");

            BufferedReader br = new BufferedReader(fr);

            String text = br.readLine();

            result = text.trim();

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return result;

    }

    public static String getCpuName() {

        try {

            FileReader fr = new FileReader("/proc/cpuinfo");

            BufferedReader br = new BufferedReader(fr);

            String text = br.readLine();

            String[] array = text.split(":\\s+", 2);

            for (int i = 0; i < array.length; i++) {

            }

            return array[1];

        } catch (FileNotFoundException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return null;

    }

    private static CPUInfo parseCPUInfo(String cpuInfo) {
        if (cpuInfo == null || "".equals(cpuInfo)) {
            return null;
        }

        CPUInfo ci = new CPUInfo();
        ci.mCPUType = CPUInfo.CPU_TYPE_UNKNOWN;
        ci.mCPUFeature = CPUInfo.CPU_FEATURE_UNKNOWS;
        ci.mCPUCount = 1;
        ci.mBogoMips = 0;

        if (cpuInfo.contains("ARMv5")) {
            ci.mCPUType = CPUInfo.CPU_TYPE_ARMV5TE;
        } else if (cpuInfo.contains("ARMv6")) {
            ci.mCPUType = CPUInfo.CPU_TYPE_ARMV6;
        } else if (cpuInfo.contains("ARMv7")) {
            ci.mCPUType = CPUInfo.CPU_TYPE_ARMV7;
        }

        if (cpuInfo.contains("neon")) {
            ci.mCPUFeature |= CPUInfo.CPU_FEATURE_NEON;
        }

        if (cpuInfo.contains("vfpv3")) {
            ci.mCPUFeature |= CPUInfo.CPU_FEATURE_VFPV3;
        }

        if (cpuInfo.contains(" vfp")) {
            ci.mCPUFeature |= CPUInfo.CPU_FEATURE_VFP;
        }

        String[] items = cpuInfo.split("\n");

        for (String item : items) {
            if (item.contains("CPU variant")) {
                int index = item.indexOf(": ");
                if (index >= 0) {
                    String value = item.substring(index + 2);
                    try {
                        ci.mCPUCount = Integer.decode(value);
                        ci.mCPUCount = ci.mCPUCount == 0 ? 1 : ci.mCPUCount;
                    } catch (NumberFormatException e) {
                        ci.mCPUCount = 1;
                    }
                }
            } else if (item.contains("BogoMIPS")) {
                int index = item.indexOf(": ");
                if (index >= 0) {
                    String value = item.substring(index + 2);
                }
            }
        }

        return ci;
    }

    public static String getCpuModel() {
        String cpu_model = "";

        CPUInfo in = getCPUInfo();

        if ((in.mCPUType & CPUInfo.CPU_TYPE_ARMV5TE) == CPUInfo.CPU_TYPE_ARMV5TE)
            cpu_model = "armv5";
        else if ((in.mCPUType & CPUInfo.CPU_TYPE_ARMV6) == CPUInfo.CPU_TYPE_ARMV6)
            cpu_model = "armv6";
        else if ((in.mCPUType & CPUInfo.CPU_TYPE_ARMV7) == CPUInfo.CPU_TYPE_ARMV7)
            cpu_model = "armv7";
        else
            cpu_model = "unknown";
        return cpu_model;
    }

    public static String getCpuFeature() {
        String cpu_feature = "";

        CPUInfo in = getCPUInfo();

        if ((in.mCPUFeature & CPUInfo.CPU_FEATURE_NEON) == CPUInfo.CPU_FEATURE_NEON)
            cpu_feature = "neon";
        else if ((in.mCPUFeature & CPUInfo.CPU_FEATURE_VFP) == CPUInfo.CPU_FEATURE_VFP)
            cpu_feature = "vfp";
        else if ((in.mCPUFeature & CPUInfo.CPU_FEATURE_VFPV3) == CPUInfo.CPU_FEATURE_VFPV3)
            cpu_feature = "vfpv3";
        else
            cpu_feature = "unknown";
        return cpu_feature;
    }
}
