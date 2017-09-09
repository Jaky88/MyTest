package com.onyx.test.styletest.utils;

import android.database.Cursor;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by jaky on 2017/9/8 0008.
 */

public class FileUtil {

    public static String readContentFromFile(String filePath) {
        BufferedReader breader = null;
        FileInputStream fis = null;

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            fis = new FileInputStream(filePath);
            breader = new BufferedReader(new InputStreamReader(fis));
            StringBuffer total = new StringBuffer();
            String line = null;
            while ((line = breader.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        } catch (Exception e) {
            return null;
        } finally {
            FileUtil.closeQuietly(breader);
            FileUtil.closeQuietly(fis);
        }
    }

    public static boolean saveContentToFile(String content, String filePath) {
        FileOutputStream fos = null;

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (!file.createNewFile()) {
                    return false;
                }
            }

            fos = new FileOutputStream(filePath);
            fos.write(content.getBytes());
            fos.flush();
            fos.close();

            return file.exists() && file.length() > 0;
        } catch (Exception e) {
            return false;
        } finally {
            FileUtil.closeQuietly(fos);
        }
    }

    public static void closeQuietly(Cursor cursor) {
        try {
            if (cursor != null)
                cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
