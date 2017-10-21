package com.onyx.test.mytest.entity;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: StyleTest
 * @Author: Jack
 * @Date: 2017/9/19 0019,1:12
 * @Version: V1.0
 * @Description: TODO
 */

public class CPUInfo{
    public CPUInfo(){

    }

    public static final int CPU_TYPE_UNKNOWN            =   0x00000000;
    public static final int CPU_TYPE_ARMV5TE            =   0x00000001;
    public static final int CPU_TYPE_ARMV6              =   0x00000010;
    public static final int CPU_TYPE_ARMV7              =   0x00000100;

    public static final int CPU_FEATURE_UNKNOWS         =   0x00000000;
    public static final int CPU_FEATURE_VFP             =   0x00000001;
    public static final int CPU_FEATURE_VFPV3           =   0x00000010;
    public static final int CPU_FEATURE_NEON            =   0x00000100;

    public int mCPUType;
    public int mCPUCount;
    public int mCPUFeature;
    public double mBogoMips;
    public long mCPUMaxFreq;
}
