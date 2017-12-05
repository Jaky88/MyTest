package com.jaky.data;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.jaky.data.bean.TestBean;
import com.jaky.data.manager.JsonHelper;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by jaky on 2017/12/5 0005.
 */

@RunWith(AndroidJUnit4.class)
public class JsonHelperTest {

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.jaky.data.test", appContext.getPackageName());
    }

    @Test
    public void loadBean() throws Exception {
        JsonHelper helper = new JsonHelper(InstrumentationRegistry.getTargetContext());
        helper.loadBean();
        TestBean bean = helper.loadBean(TestBean.class.getSimpleName(), new TestBean());
        bean.setName("fffss");
        helper.saveBean(bean, TestBean.class.getSimpleName());
        bean = helper.loadBean(TestBean.class.getSimpleName(), new TestBean());
    }

    @Test
    public void saveBean() throws Exception {
        JsonHelper helper = new JsonHelper(InstrumentationRegistry.getTargetContext());
        helper.saveBean(helper.getAppConfig());
    }
}
