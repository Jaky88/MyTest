package com.onyx.test.styletest.utils;

/**
 * Created by jaky on 2017/9/15 0015.
 */

public class RefValue<T>
{
    private T mValue = null;

    public RefValue()
    {
    }

    public RefValue(T v)
    {
        mValue = v;
    }

    public T getValue()
    {
        return mValue;
    }
    public void setValue(T v)
    {
        mValue = v;
    }
}
