package com.jaky.mupdf.data;

import android.graphics.RectF;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Annotation extends RectF {
    public static final int TEXT = 0;
    public static final int LINK = 1;
    public static final int FREETEXT = 2;
    public static final int LINE = 3;
    public static final int SQUARE = 4;
    public static final int CIRCLE = 5;
    public static final int POLYGON = 6;
    public static final int POLYLINE = 7;
    public static final int HIGHLIGHT = 8;
    public static final int UNDERLINE = 9;
    public static final int SQUIGGLY = 10;
    public static final int STRIKEOUT = 11;
    public static final int STAMP = 12;
    public static final int CARET = 13;
    public static final int INK = 14;
    public static final int POPUP = 15;
    public static final int FILEATTACHMENT = 16;
    public static final int SOUND = 17;
    public static final int MOVIE = 18;
    public static final int WIDGET = 19;
    public static final int SCREEN = 20;
    public static final int PRINTERMARK = 21;
    public static final int TRAPNET = 22;
    public static final int WATERMARK = 23;
    public static final int A3D = 24;
    public static final int UNKNOWN = 25;

    @IntDef({TEXT, LINK, FREETEXT, LINE, SQUARE, CIRCLE, POLYGON, POLYLINE, HIGHLIGHT,
            UNDERLINE, SQUIGGLY, STRIKEOUT, STAMP, CARET, INK, POPUP, FILEATTACHMENT,
            SOUND, MOVIE, WIDGET, SCREEN, PRINTERMARK, TRAPNET, WATERMARK, A3D, UNKNOWN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int[] values = {TEXT, LINK, FREETEXT, LINE, SQUARE, CIRCLE, POLYGON, POLYLINE, HIGHLIGHT,
                UNDERLINE, SQUIGGLY, STRIKEOUT, STAMP, CARET, INK, POPUP, FILEATTACHMENT,
                SOUND, MOVIE, WIDGET, SCREEN, PRINTERMARK, TRAPNET, WATERMARK, A3D, UNKNOWN};
    }

    @Type
    public final int type;

    public Annotation(float x0, float y0, float x1, float y1, int _type) {
        super(x0, y0, x1, y1);
        type = _type == -1 ? UNKNOWN : Type.values[_type];
    }
}
