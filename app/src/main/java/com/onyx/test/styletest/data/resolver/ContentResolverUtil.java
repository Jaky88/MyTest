package com.onyx.test.styletest.data.resolver;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import com.onyx.test.styletest.data.resolver.data.BaseColumn;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: MyTest
 * @Author: Jack
 * @Date: 2017/10/21 0021,0:24
 * @Version: V1.0
 * @Description: TODO
 */

public class ContentResolverUtil {

    public static BaseColumn resolve(Context context, BaseColumn column) {
        Cursor cursor = null;
        try {
            Uri uri = Uri.parse(column.url);
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor == null) {
                return column;
            }

            while (cursor.moveToNext()) {
                column.read(cursor);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            return column;
        }
    }
}
