package com.jaky.mupdf.async;

import com.jaky.mupdf.data.TextWord;

/**
 * Created by jaky on 2017/11/22 0022.
 */

public interface TextProcessor {
    void onStartLine();
    void onWord(TextWord word);
    void onEndLine();
}
