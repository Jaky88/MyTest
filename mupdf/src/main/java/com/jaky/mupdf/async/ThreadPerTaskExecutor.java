package com.jaky.mupdf.async;

import java.util.concurrent.Executor;

/**
 * Created by jaky on 2017/11/22 0022.
 */

public class ThreadPerTaskExecutor implements Executor {
    public void execute(Runnable r) {
        new Thread(r).start();
    }
}
