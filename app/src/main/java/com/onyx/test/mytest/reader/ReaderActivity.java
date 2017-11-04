package com.onyx.test.mytest.reader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.cache.ReaderBitmapImpl;
import com.onyx.android.sdk.reader.common.ReaderDrawContext;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.host.impl.ReaderDocumentOptionsImpl;
import com.onyx.android.sdk.reader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.host.wrapper.ReaderManager;
import com.onyx.android.sdk.reader.utils.TreeObserverUtils;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.test.mytest.R;
import com.onyx.test.mytest.data.DataManager;

import butterknife.ButterKnife;

/**
 * Created by jaky on 2017/10/21 0021.
 */

public class ReaderActivity extends AppCompatActivity {
    private SurfaceHolder.Callback surfaceHolderCallback;
    private SurfaceHolder holder;
    private SurfaceView surfaceView;
    private RelativeLayout mainView;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleDetector;
    private Reader reader;
    private ReaderBitmapImpl renderBitmap;
    private ReaderViewInfo readerViewInfo;
    private DataManager dadaManager;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        ButterKnife.bind(this);
        dadaManager =  DataManager.getInstance();
        initWindow();
        initComponents();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initWindow() {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
//        layoutParams.height = getIntent().getIntExtra(ReaderBroadcastReceiver.TAG_WINDOW_HEIGHT, WindowManager.LayoutParams.MATCH_PARENT);
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        getWindow().setAttributes(layoutParams);

        Debug.d(getClass(), "target window height:" + layoutParams.height);
    }

    private void initComponents() {
        initSurfaceView();
    }

    private void initSurfaceView() {
        mainView = (RelativeLayout) findViewById(R.id.main_view);
        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        surfaceHolderCallback = new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Debug.d(getClass(), "surfaceCreated");
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Debug.d(getClass(), "surfaceChanged: " + format + ", " + width + ", " + height);
                clearCanvas(holder);

                dadaManager.setDisplaySize(surfaceView.getWidth(), surfaceView.getHeight());
                reader.getViewOptions().setSize(dadaManager.getDisplayWidth(), dadaManager.getDisplayHeight());
                try {
                    drawVisiblePages(reader);
                } catch (ReaderException e) {
                    e.printStackTrace();
                }

                if (surfaceView.getWidth() == dadaManager.getDisplayWidth() &&
                        surfaceView.getHeight() == dadaManager.getDisplayHeight()) {
                    try {
                        drawVisiblePages(reader);
                    } catch (ReaderException e) {
                        e.printStackTrace();
                    }
                } else {
                    onSurfaceViewSizeChanged();
                }
            }

            private void redrawPage() {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Debug.d(getClass(), "surfaceDestroyed");
            }
        };

        surfaceView.getHolder().addCallback(surfaceHolderCallback);
        holder = surfaceView.getHolder();
//        gestureDetector = new GestureDetector(this, new MyOnGestureListener(getReaderDataHolder()));
//        scaleDetector = new ScaleGestureDetector(this, new MyScaleGestureListener(getReaderDataHolder()));
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
//                getHandlerManager().setTouchStartEvent(event);
//                scaleDetector.onTouchEvent(event);
//                gestureDetector.onTouchEvent(event);
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    getHandlerManager().onActionUp(getReaderDataHolder(), event);
//                    getHandlerManager().resetTouchStartPosition();
//                }
//                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
//                    getHandlerManager().onActionCancel(getReaderDataHolder(), event);
//                    getHandlerManager().resetTouchStartPosition();
//                }

//                getHandlerManager().onTouchEvent(getReaderDataHolder(), event);
                return true;
            }
        });

        surfaceView.setFocusable(true);
        surfaceView.setFocusableInTouchMode(true);
        surfaceView.requestFocusFromTouch();

        // make sure we openFileFromIntent the doc after surface view is layouted correctly.
        final ViewTreeObserver observer = surfaceView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                TreeObserverUtils.removeGlobalOnLayoutListener(surfaceView.getViewTreeObserver(), this);
                handleActivityIntent();
            }
        });
    }

    private void onSurfaceViewSizeChanged() {
        try {
            reader.getReaderHelper().updateViewportSize(758, 1024);
            drawVisiblePages(reader);
        } catch (ReaderException e) {
            e.printStackTrace();
        }
    }

    public void drawVisiblePages(final Reader reader) throws ReaderException {
        ReaderDrawContext context = ReaderDrawContext.create(false);
        drawVisiblePages(reader, context);
    }

    public void drawVisiblePages(final Reader reader, ReaderDrawContext context) throws ReaderException {
        if (reader.getReaderLayoutManager().drawVisiblePages(reader, context, createReaderViewInfo())) {
            renderBitmap = context.renderingBitmap;
        }
    }

    public ReaderViewInfo createReaderViewInfo() {
        readerViewInfo = new ReaderViewInfo();
        return readerViewInfo;
    }

    private void clearCanvas(SurfaceHolder holder) {
        if (holder == null) {
            return;
        }
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private boolean handleActivityIntent() {
        try {
            String action = getIntent().getAction();
            if (action.equals(Intent.ACTION_VIEW)) {
                handleViewActionIntent();
            }
            return true;
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void handleViewActionIntent() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            openFileFromIntent();
        }
    }

    private void openFileFromIntent() {
        Uri uri = getIntent().getData();
        if (uri == null) {
            return;
        }

        openFileFromIntentImpl();
    }

    private void openFileFromIntentImpl() {
        final String path = FileUtils.getRealFilePathFromUri(ReaderActivity.this, getIntent().getData());
        initReaderFromPath(path);
        try {
            BaseOptions srcOptions = new BaseOptions();
            final ReaderDocumentOptionsImpl documentOptions = srcOptions.documentOptions();
            ReaderPluginOptions pluginOptions = ReaderPluginOptionsImpl.create(this);
            reader.getReaderHelper().selectPlugin(this, path, pluginOptions);
            ReaderDocument document = reader.getPlugin().open(path, documentOptions, pluginOptions);
            reader.getReaderHelper().onDocumentOpened(this, path, document, srcOptions, pluginOptions);
            reader.getReaderHelper().initData(this);
        } catch (Exception e) {
        }
    }

    public void initReaderFromPath(final String path) {
        if (reader == null) {
            reader = new Reader();
        }
        reader = ReaderManager.getReader(path);
    }

}
