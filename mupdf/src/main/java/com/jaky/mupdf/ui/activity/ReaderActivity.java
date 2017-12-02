package com.jaky.mupdf.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jaky.mupdf.R;
import com.jaky.mupdf.binding.ActivityReaderModel;
import com.jaky.mupdf.binding.MainToolBarModel;
import com.jaky.mupdf.data.Annotation;
import com.jaky.mupdf.data.ReaderConstants;
import com.jaky.mupdf.data.Separation;
import com.jaky.mupdf.databinding.ActivityMupdfBinding;
import com.jaky.mupdf.task.AsyncTask;
import com.jaky.mupdf.data.FilePicker;
import com.jaky.mupdf.data.MuPDFAlert;
import com.jaky.mupdf.core.MuPDFCore;
import com.jaky.mupdf.data.OutlineActivityData;
import com.jaky.mupdf.data.OutlineItem;
import com.jaky.mupdf.data.SafeAnimatorInflater;
import com.jaky.mupdf.task.SearchTask;
import com.jaky.mupdf.task.SearchTaskResult;
import com.jaky.mupdf.async.ThreadPerTaskExecutor;
import com.jaky.mupdf.async.ViewMapper;
import com.jaky.mupdf.ui.adapter.MuPDFPageAdapter;
import com.jaky.mupdf.ui.adapter.MuPDFReflowAdapter;
import com.jaky.mupdf.ui.views.adapterview.MuPDFReaderView;
import com.jaky.mupdf.ui.views.adapterview.ReaderCallback;
import com.jaky.mupdf.ui.views.baseview.MuPDFView;
import com.jaky.mupdf.utils.Debug;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class ReaderActivity extends Activity implements FilePicker.FilePickerSupport {
    private static final String TAG = ReaderActivity.class.getSimpleName();
    private ActivityMupdfBinding readerBinding;

    enum TopBarMode {Main, Search, Annot, Delete, More, Accept}

    enum AcceptMode {Highlight, Underline, StrikeOut, Ink, CopyText}

    private final int OUTLINE_REQUEST = 0;
    private final int PRINT_REQUEST = 1;
    private final int FILEPICK_REQUEST = 2;
    private final int PROOF_REQUEST = 3;
    private MuPDFCore core;
    private String mFileName;
    private boolean mButtonsVisible;
    private EditText mPasswordView;
    private int mPageSliderRes;

    private TopBarMode mTopBarMode = TopBarMode.Main;
    private AcceptMode mAcceptMode;
    private SearchTask mSearchTask;
    private AlertDialog.Builder mAlertBuilder;
    private boolean mLinkHighlight = false;
    private final Handler mHandler = new Handler();
    private boolean mAlertsActive = false;
    private boolean mReflow = false;
    private AsyncTask<Void, Void, MuPDFAlert> mAlertTask;
    private AlertDialog mAlertDialog;
    private FilePicker mFilePicker;
    private String mProofFile;
    private boolean mSepEnabled[][];
    private ReaderCallback callback = new ReaderCallback() {
        @Override
        protected void onTapMainDocArea() {
            if (!mButtonsVisible) {
                showButtons();
            } else {
                if (mTopBarMode == TopBarMode.Main) {
                    hideButtons();
                }
            }
        }

        @Override
        protected void onDocMotion() {
            hideButtons();
        }

        @Override
        protected void onHit(String item) {
            switch (mTopBarMode) {
                case Annot:
                    if (ReaderConstants.ANNOTATION.equals(item)) {
                        showButtons();
                        mTopBarMode = TopBarMode.Delete;
                        readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
                    }
                    break;
                case Delete:
                    mTopBarMode = TopBarMode.Annot;
                    readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
                default:
                    MuPDFView pageView = (MuPDFView) readerBinding.readerPager.getDisplayedView();
                    if (pageView != null)
                        pageView.deselectAnnotation();
                    break;
            }
        }

        @Override
        protected void onMoveToChild(int i) {
            if (core == null) {
                return;
            }
            readerBinding.toolBar.bottomBar.tvPageNumber.setText(String.format("%d / %d", i + 1,
                    core.countPages()));
            readerBinding.toolBar.bottomBar.sbPageSlider.setMax((core.countPages() - 1) * mPageSliderRes);
            readerBinding.toolBar.bottomBar.sbPageSlider.setProgress(i * mPageSliderRes);
            super.onMoveToChild(i);

        }
    };

    private String[] pickFiles = new String[]{
            ".pdf", ".xps", ".cbz", ".epub", ".png", ".jpe", ".jpeg", ".jpg", ".jfif", ".jfif-tbnl", ".tif", ".tiff"};

    static private AlertDialog.Builder gAlertBuilder;

    static public AlertDialog.Builder getAlertBuilder() {
        return gAlertBuilder;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getCore(savedInstanceState)) return;
        initView(savedInstanceState);
    }

    private boolean getCore(Bundle savedInstanceState) {
        if (core == null) {
            restoreCore(savedInstanceState);
        }
        if (core == null) {
            Intent intent = getIntent();
            byte buffer[] = null;

            if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                Uri uri = intent.getData();
                Debug.i("URI to open is: " + uri);
                if (uri.toString().startsWith("content://")) {
                    String reason = null;
                    try {
                        InputStream is = getContentResolver().openInputStream(uri);
                        int len = is.available();
                        buffer = new byte[len];
                        is.read(buffer, 0, len);
                        is.close();
                    } catch (OutOfMemoryError e) {
                        Debug.i("Out of memory during buffer reading");
                        reason = e.toString();
                    } catch (Exception e) {
                        Debug.i("Exception reading from stream: " + e);
                        try {
                            Cursor cursor = getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
                            if (cursor.moveToFirst()) {
                                String str = cursor.getString(0);
                                if (str == null) {
                                    reason = "Couldn't parse data in intent";
                                } else {
                                    uri = Uri.parse(str);
                                }
                            }
                        } catch (Exception e2) {
                            Debug.i("Exception in Transformer Prime file manager code: " + e2);
                            reason = e2.toString();
                        }
                    }
                    if (reason != null) {
                        buffer = null;
                        Resources res = getResources();
                        AlertDialog alert = mAlertBuilder.create();
                        setTitle(String.format(res.getString(R.string.cannot_open_document_Reason), reason));
                        alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dismiss),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                        alert.show();
                        return true;
                    }
                }
                if (buffer != null) {
                    core = openBuffer(buffer, intent.getType());
                } else {
                    String path = Uri.decode(uri.getEncodedPath());
                    if (path == null) {
                        path = uri.toString();
                    }
                    core = openFile(path);
                }
                SearchTaskResult.set(null);
            }


            if (core != null && core.needsPassword()) {
                requestPassword(savedInstanceState);
                return true;
            }
            if (core != null && core.countPages() == 0) {
                core = null;
            }
        }

        mAlertBuilder = new AlertDialog.Builder(this);
        gAlertBuilder = mAlertBuilder;
        if (core == null) {
            AlertDialog alert = mAlertBuilder.create();
            alert.setTitle(R.string.cannot_open_document);
            alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.dismiss),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            alert.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
            });
            alert.show();
            return true;
        }
        return false;
    }

    private void restoreCore(Bundle savedInstanceState) {
        core = (MuPDFCore) getLastNonConfigurationInstance();
        if (savedInstanceState != null && savedInstanceState.containsKey("FileName")) {
            mFileName = savedInstanceState.getString("FileName");
        }
    }

    public void requestPassword(final Bundle savedInstanceState) {
        mPasswordView = new EditText(this);
        mPasswordView.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        mPasswordView.setTransformationMethod(new PasswordTransformationMethod());

        AlertDialog alert = mAlertBuilder.create();
        alert.setTitle(R.string.enter_password);
        alert.setView(mPasswordView);
        alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.okay),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (core.authenticatePassword(mPasswordView.getText().toString())) {
                            initView(savedInstanceState);
                        } else {
                            requestPassword(savedInstanceState);
                        }
                    }
                });
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        alert.show();
    }

    public void initView(Bundle savedInstanceState) {
        if (core == null){
            return;
        }
        readerBinding = DataBindingUtil.setContentView(this, R.layout.activity_mupdf);
        readerBinding.setReaderModel(new ActivityReaderModel(this));
        readerBinding.toolBar.setToolBarModel(new MainToolBarModel(this, core));
        readerBinding.readerPager.setCallback(callback);
        readerBinding.readerPager.setAdapter(new MuPDFPageAdapter(this, this, core));


        mSearchTask = new SearchTask(this, core) {
            @Override
            protected void onTextFound(SearchTaskResult result) {
                SearchTaskResult.set(result);
                readerBinding.readerPager.setDisplayedViewIndex(result.pageNumber);
                readerBinding.readerPager.resetupChildren();
            }
        };


        if (!core.gprfSupported()) {
            readerBinding.toolBar.proofButton.setVisibility(View.INVISIBLE);
        }
        readerBinding.toolBar.sepsButton.setVisibility(View.INVISIBLE);


        int smax = Math.max(core.countPages() - 1, 1);
        mPageSliderRes = ((10 + smax - 1) / smax) * 2;
        readerBinding.toolBar.tvDocName.setText(mFileName);
        readerBinding.toolBar.bottomBar.sbPageSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                readerBinding.readerPager.setDisplayedViewIndex((seekBar.getProgress() + mPageSliderRes / 2) / mPageSliderRes);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                updatePageNumView((progress + mPageSliderRes / 2) / mPageSliderRes);
            }
        });

        readerBinding.toolBar.searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                searchModeOn();
            }
        });

        readerBinding.toolBar.reflowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toggleReflow();
            }
        });

        if (core.fileFormat().startsWith("PDF") && core.isUnencryptedPDF() && !core.wasOpenedFromBuffer()) {
            readerBinding.toolBar.editAnnotButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mTopBarMode = TopBarMode.Annot;
                    readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
                }
            });
        } else {
            readerBinding.toolBar.editAnnotButton.setVisibility(View.GONE);
        }

        readerBinding.toolBar.btnSearchBack.setEnabled(false);
        readerBinding.toolBar.btnsSearchForward.setEnabled(false);
        readerBinding.toolBar.btnSearchBack.setColorFilter(Color.argb(255, 128, 128, 128));
        readerBinding.toolBar.btnsSearchForward.setColorFilter(Color.argb(255, 128, 128, 128));

        readerBinding.toolBar.etSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                boolean haveText = s.toString().length() > 0;
                setButtonEnabled(readerBinding.toolBar.btnSearchBack, haveText);
                setButtonEnabled(readerBinding.toolBar.btnsSearchForward, haveText);

                // Remove any previous search results
                if (SearchTaskResult.get() != null && !readerBinding.toolBar.etSearch.getText().toString().equals(SearchTaskResult.get().txt)) {
                    SearchTaskResult.set(null);
                    readerBinding.readerPager.resetupChildren();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });

        readerBinding.toolBar.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    search(1);
                return false;
            }
        });

        readerBinding.toolBar.etSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
                    search(1);
                return false;
            }
        });

        readerBinding.toolBar.btnSearchBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search(-1);
            }
        });
        readerBinding.toolBar.btnsSearchForward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search(1);
            }
        });

        readerBinding.toolBar.linkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setLinkHighlight(!mLinkHighlight);
            }
        });

        if (core.hasOutline()) {
            readerBinding.toolBar.outlineButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    OutlineItem outline[] = core.getOutline();
                    if (outline != null) {
                        OutlineActivityData.get().items = outline;
                        Intent intent = new Intent(ReaderActivity.this, OutlineActivity.class);
                        startActivityForResult(intent, OUTLINE_REQUEST);
                    }
                }
            });
        } else {
            readerBinding.toolBar.outlineButton.setVisibility(View.GONE);
        }

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        readerBinding.readerPager.setDisplayedViewIndex(prefs.getInt("page" + mFileName, 0));

        if (savedInstanceState == null || !savedInstanceState.getBoolean("ButtonsHidden", false)){
            showButtons();
        }

        if (savedInstanceState != null && savedInstanceState.getBoolean("SearchMode", false)){
            searchModeOn();
        }

        if (savedInstanceState != null && savedInstanceState.getBoolean("ReflowMode", false)){
            reflowModeSet(true);
        }

        if (readerBinding.toolBar.getToolBarModel().isProof()) {
            int currentPage = getIntent().getIntExtra("startingPage", 0);
            readerBinding.readerPager.setDisplayedViewIndex(currentPage);
        }
    }

    public Object onRetainNonConfigurationInstance() {
        MuPDFCore mycore = core;
        core = null;
        return mycore;
    }

    private void reflowModeSet(boolean reflow) {
        mReflow = reflow;
        readerBinding.readerPager.setAdapter(mReflow ? new MuPDFReflowAdapter(this, core) : new MuPDFPageAdapter(this, this, core));
        readerBinding.toolBar.reflowButton.setColorFilter(mReflow ? Color.argb(0xFF, 172, 114, 37) : Color.argb(0xFF, 255, 255, 255));
        setButtonEnabled(readerBinding.toolBar.editAnnotButton, !reflow);
        setButtonEnabled(readerBinding.toolBar.searchButton, !reflow);
        if (reflow) setLinkHighlight(false);
        setButtonEnabled(readerBinding.toolBar.linkButton, !reflow);
        setButtonEnabled(readerBinding.toolBar.moreButton, !reflow);
        readerBinding.readerPager.refresh(mReflow);
    }

    private void toggleReflow() {
        reflowModeSet(!mReflow);
        showInfo(mReflow ? getString(R.string.entering_reflow_mode) : getString(R.string.leaving_reflow_mode));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mFileName != null && readerBinding.readerPager != null) {
            outState.putString("FileName", mFileName);
            SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt("page" + mFileName, readerBinding.readerPager.getDisplayedViewIndex());
            edit.commit();
        }

        if (!mButtonsVisible)
            outState.putBoolean("ButtonsHidden", true);

        if (mTopBarMode == TopBarMode.Search)
            outState.putBoolean("SearchMode", true);

        if (mReflow)
            outState.putBoolean("ReflowMode", true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSearchTask != null)
            mSearchTask.stop();
        if (mFileName != null && readerBinding.readerPager != null) {
            SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt("page" + mFileName, readerBinding.readerPager.getDisplayedViewIndex());
            edit.commit();
        }
    }

    public void onDestroy() {
        if (readerBinding.readerPager != null) {
            readerBinding.readerPager.applyToChildren(new ViewMapper() {
                public void applyToView(View view) {
                    ((MuPDFView) view).releaseBitmaps();
                }
            });
        }
        if (core != null)
            core.onDestroy();
        if (mAlertTask != null) {
            mAlertTask.cancel(true);
            mAlertTask = null;
        }
        core = null;
        super.onDestroy();
    }

    private void setButtonEnabled(ImageButton button, boolean enabled) {
        button.setEnabled(enabled);
        button.setColorFilter(enabled ? Color.argb(255, 255, 255, 255) : Color.argb(255, 128, 128, 128));
    }

    private void setLinkHighlight(boolean highlight) {
        mLinkHighlight = highlight;
        readerBinding.toolBar.linkButton.setColorFilter(highlight ? Color.argb(0xFF, 172, 114, 37) : Color.argb(0xFF, 255, 255, 255));
        readerBinding.readerPager.setLinksEnabled(highlight);
    }

    private void showButtons() {
        if (core == null) {
            return;
        }
        if (!mButtonsVisible) {
            mButtonsVisible = true;
            int index = readerBinding.readerPager.getDisplayedViewIndex();
            updatePageNumView(index);
            readerBinding.toolBar.bottomBar.sbPageSlider.setMax((core.countPages() - 1) * mPageSliderRes);
            readerBinding.toolBar.bottomBar.sbPageSlider.setProgress(index * mPageSliderRes);
            if (mTopBarMode == TopBarMode.Search) {
                readerBinding.toolBar.etSearch.requestFocus();
                showKeyboard();
            }

            Animation anim = new TranslateAnimation(0, 0, -readerBinding.toolBar.switcher.getHeight(), 0);
            anim.setDuration(200);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    readerBinding.toolBar.switcher.setVisibility(View.VISIBLE);
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                }
            });
            readerBinding.toolBar.switcher.startAnimation(anim);

            anim = new TranslateAnimation(0, 0, readerBinding.toolBar.bottomBar.sbPageSlider.getHeight(), 0);
            anim.setDuration(200);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    readerBinding.toolBar.bottomBar.sbPageSlider.setVisibility(View.VISIBLE);
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    readerBinding.toolBar.bottomBar.tvPageNumber.setVisibility(View.VISIBLE);
                }
            });
            readerBinding.toolBar.bottomBar.sbPageSlider.startAnimation(anim);
        }
    }

    private void hideButtons() {
        if (mButtonsVisible) {
            mButtonsVisible = false;
            hideKeyboard();

            Animation anim = new TranslateAnimation(0, 0, 0, -readerBinding.toolBar.switcher.getHeight());
            anim.setDuration(200);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    readerBinding.toolBar.switcher.setVisibility(View.INVISIBLE);
                }
            });
            readerBinding.toolBar.switcher.startAnimation(anim);

            anim = new TranslateAnimation(0, 0, 0, readerBinding.toolBar.bottomBar.sbPageSlider.getHeight());
            anim.setDuration(200);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    readerBinding.toolBar.bottomBar.tvPageNumber.setVisibility(View.INVISIBLE);
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    readerBinding.toolBar.bottomBar.sbPageSlider.setVisibility(View.INVISIBLE);
                }
            });
            readerBinding.toolBar.bottomBar.sbPageSlider.startAnimation(anim);
        }
    }

    private void searchModeOn() {
        if (mTopBarMode != TopBarMode.Search) {
            mTopBarMode = TopBarMode.Search;
            readerBinding.toolBar.etSearch.requestFocus();
            showKeyboard();
            readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
        }
    }

    private void searchModeOff() {
        if (mTopBarMode == TopBarMode.Search) {
            mTopBarMode = TopBarMode.Main;
            hideKeyboard();
            readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
            SearchTaskResult.set(null);
            readerBinding.readerPager.resetupChildren();
        }
    }

    private void updatePageNumView(int index) {
        if (core == null)
            return;
        readerBinding.toolBar.bottomBar.tvPageNumber.setText(String.format("%d / %d", index + 1, core.countPages()));
    }

    private void printDoc() {
        if (!core.fileFormat().startsWith("PDF")) {
            showInfo(getString(R.string.format_currently_not_supported));
            return;
        }

        Intent myIntent = getIntent();
        Uri docUri = myIntent != null ? myIntent.getData() : null;

        if (docUri == null) {
            showInfo(getString(R.string.print_failed));
        }

        if (docUri.getScheme() == null)
            docUri = Uri.parse("file://" + docUri.toString());

        Intent printIntent = new Intent(this, PrintDialogActivity.class);
        printIntent.setDataAndType(docUri, "aplication/pdf");
        printIntent.putExtra("title", mFileName);
        startActivityForResult(printIntent, PRINT_REQUEST);
    }

    private void showInfo(String message) {
        readerBinding.toolBar.tvInfo.setText(message);

        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            SafeAnimatorInflater safe = new SafeAnimatorInflater((Activity) this, R.animator.info, (View) readerBinding.toolBar.tvInfo);
        } else {
            readerBinding.toolBar.tvInfo.setVisibility(View.VISIBLE);
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    readerBinding.toolBar.tvInfo.setVisibility(View.INVISIBLE);
                }
            }, 500);
        }
    }

    public void proofWithResolution(int resolution) {
        mProofFile = core.startProof(resolution);
        Uri uri = Uri.parse("file://" + mProofFile);
        Intent intent = new Intent(this, ReaderActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.putExtra("startingPage", readerBinding.readerPager.getDisplayedViewIndex());
        startActivityForResult(intent, PROOF_REQUEST);
    }


    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.showSoftInput(readerBinding.toolBar.etSearch, 0);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(readerBinding.toolBar.etSearch.getWindowToken(), 0);
    }

    private void search(int direction) {
        hideKeyboard();
        int displayPage = readerBinding.readerPager.getDisplayedViewIndex();
        SearchTaskResult r = SearchTaskResult.get();
        int searchPage = r != null ? r.pageNumber : -1;
        mSearchTask.go(readerBinding.toolBar.etSearch.getText().toString(), direction, displayPage, searchPage);
    }

    @Override
    public boolean onSearchRequested() {
        if (mButtonsVisible && mTopBarMode == TopBarMode.Search) {
            hideButtons();
        } else {
            showButtons();
            searchModeOn();
        }
        return super.onSearchRequested();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mButtonsVisible && mTopBarMode != TopBarMode.Search) {
            hideButtons();
        } else {
            showButtons();
            searchModeOff();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        if (core != null) {
            core.startAlerts();
            createAlertWaiter();
        }

        super.onStart();
    }

    @Override
    protected void onStop() {
        if (core != null) {
            destroyAlertWaiter();
            core.stopAlerts();
        }

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (core != null && core.hasChanges()) {
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == AlertDialog.BUTTON_POSITIVE)
                        core.save();

                    finish();
                }
            };
            AlertDialog alert = mAlertBuilder.create();
            alert.setTitle("MuPDF");
            alert.setMessage(getString(R.string.document_has_changes_save_them_));
            alert.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), listener);
            alert.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no), listener);
            alert.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void performPickFor(FilePicker picker) {
        mFilePicker = picker;
        new LFilePicker().withActivity(this)
                .withRequestCode(FILEPICK_REQUEST)
                .withTitle("选择文件")
                .withTitleColor("#FF000000")
                .withMutilyMode(false)
                .withFileFilter(pickFiles)
                .withBackIcon(Constant.BACKICON_STYLETHREE)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case OUTLINE_REQUEST:
                if (resultCode >= 0)
                    readerBinding.readerPager.setDisplayedViewIndex(resultCode);
                break;
            case PRINT_REQUEST:
                if (resultCode == RESULT_CANCELED)
                    showInfo(getString(R.string.print_failed));
                break;
            case FILEPICK_REQUEST:
                if (mFilePicker != null && resultCode == RESULT_OK) {
                    List<String> list = data.getStringArrayListExtra("paths");
                    for (String s : list) {
                        mFilePicker.onPick(Uri.fromFile(new File(s)));
                    }
                }
            case PROOF_REQUEST:
                if (mProofFile != null) {
                    core.endProof(mProofFile);
                    mProofFile = null;
                }

                mTopBarMode = TopBarMode.Main;
                readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void createAlertWaiter() {
        mAlertsActive = true;
        if (mAlertTask != null) {
            mAlertTask.cancel(true);
            mAlertTask = null;
        }
        if (mAlertDialog != null) {
            mAlertDialog.cancel();
            mAlertDialog = null;
        }
        mAlertTask = new AsyncTask<Void, Void, MuPDFAlert>() {

            @Override
            protected MuPDFAlert doInBackground(Void... arg0) {
                if (!mAlertsActive)
                    return null;

                return core.waitForAlert();
            }

            @Override
            protected void onPostExecute(final MuPDFAlert result) {
                if (result == null)
                    return;
                final MuPDFAlert.ButtonPressed pressed[] = new MuPDFAlert.ButtonPressed[3];
                for (int i = 0; i < 3; i++)
                    pressed[i] = MuPDFAlert.ButtonPressed.None;
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialog = null;
                        if (mAlertsActive) {
                            int index = 0;
                            switch (which) {
                                case AlertDialog.BUTTON1:
                                    index = 0;
                                    break;
                                case AlertDialog.BUTTON2:
                                    index = 1;
                                    break;
                                case AlertDialog.BUTTON3:
                                    index = 2;
                                    break;
                            }
                            result.buttonPressed = pressed[index];
                            core.replyToAlert(result);
                            createAlertWaiter();
                        }
                    }
                };
                mAlertDialog = mAlertBuilder.create();
                mAlertDialog.setTitle(result.title);
                mAlertDialog.setMessage(result.message);
                switch (result.iconType) {
                    case Error:
                        break;
                    case Warning:
                        break;
                    case Question:
                        break;
                    case Status:
                        break;
                }
                switch (result.buttonGroupType) {
                    case OkCancel:
                        mAlertDialog.setButton(AlertDialog.BUTTON2, getString(R.string.cancel), listener);
                        pressed[1] = MuPDFAlert.ButtonPressed.Cancel;
                    case Ok:
                        mAlertDialog.setButton(AlertDialog.BUTTON1, getString(R.string.okay), listener);
                        pressed[0] = MuPDFAlert.ButtonPressed.Ok;
                        break;
                    case YesNoCancel:
                        mAlertDialog.setButton(AlertDialog.BUTTON3, getString(R.string.cancel), listener);
                        pressed[2] = MuPDFAlert.ButtonPressed.Cancel;
                    case YesNo:
                        mAlertDialog.setButton(AlertDialog.BUTTON1, getString(R.string.yes), listener);
                        pressed[0] = MuPDFAlert.ButtonPressed.Yes;
                        mAlertDialog.setButton(AlertDialog.BUTTON2, getString(R.string.no), listener);
                        pressed[1] = MuPDFAlert.ButtonPressed.No;
                        break;
                }
                mAlertDialog.setOnCancelListener(new OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        mAlertDialog = null;
                        if (mAlertsActive) {
                            result.buttonPressed = MuPDFAlert.ButtonPressed.None;
                            core.replyToAlert(result);
                            createAlertWaiter();
                        }
                    }
                });

                mAlertDialog.show();
            }
        };

        mAlertTask.executeOnExecutor(new ThreadPerTaskExecutor());
    }

    public void destroyAlertWaiter() {
        mAlertsActive = false;
        if (mAlertDialog != null) {
            mAlertDialog.cancel();
            mAlertDialog = null;
        }
        if (mAlertTask != null) {
            mAlertTask.cancel(true);
            mAlertTask = null;
        }
    }

    private MuPDFCore openFile(String path) {
        int lastSlashPos = path.lastIndexOf('/');
        mFileName = new String(lastSlashPos == -1
                ? path
                : path.substring(lastSlashPos + 1));
        Log.d(TAG, "Trying to open " + path);
        try {
            core = new MuPDFCore(this, path);
            // New file: drop the old outline data
            OutlineActivityData.set(null);
        } catch (Exception e) {
            Debug.w(this.getClass(), e);
            return null;
        } catch (OutOfMemoryError e) {
            Debug.w(this.getClass(), e);
            return null;
        }
        return core;
    }

    private MuPDFCore openBuffer(byte buffer[], String magic) {
        Debug.i("Trying to open byte buffer");
        try {
            core = new MuPDFCore(this, buffer, magic);
            OutlineActivityData.set(null);
        } catch (Exception e) {
            Debug.w(this.getClass(), e);
            return null;
        }
        return core;
    }

    public void OnMoreButtonClick(View v) {
        mTopBarMode = ReaderActivity.TopBarMode.More;
        readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
    }

    public void OnCancelMoreButtonClick(View v) {
        mTopBarMode = ReaderActivity.TopBarMode.Main;
        readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
    }

    public void OnPrintButtonClick(View v) {
        printDoc();
    }

    public void OnSepsButtonClick(final View v) {
        if (isProofing()) {
            final int currentPage = readerBinding.readerPager.getDisplayedViewIndex();
            final PopupMenu menu = new PopupMenu(this, v);

            try {
                Field[] fields = menu.getClass().getDeclaredFields();
                for (Field field : fields) {
                    if ("mPopup".equals(field.getName())) {
                        field.setAccessible(true);
                        Object menuPopupHelper = field.get(menu);
                        Class<?> classPopupHelper = Class.forName(menuPopupHelper
                                .getClass().getName());
                        Method setForceIcons = classPopupHelper.getMethod(
                                "setForceShowIcon", boolean.class);
                        setForceIcons.invoke(menuPopupHelper, true);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            int maxSeps = 0;
            int numPages = core.countPages();
            for (int page = 0; page < numPages; page++) {
                int numSeps = core.getNumSepsOnPage(page);
                if (numSeps > maxSeps)
                    maxSeps = numSeps;
            }

            if (mSepEnabled == null) {
                mSepEnabled = new boolean[numPages][maxSeps];
                for (int page = 0; page < numPages; page++) {
                    for (int i = 0; i < maxSeps; i++)
                        mSepEnabled[page][i] = true;
                }
            }

            int numSeps = core.getNumSepsOnPage(currentPage);

            for (int i = 0; i < numSeps; i++) {
                Separation sep = core.getSep(currentPage, i);
                String name = sep.name;

                //  make a checkable menu item with that name
                //  and the separation index as the id
                MenuItem item = menu.getMenu().add(0, i, 0, name + "    ");
                item.setCheckable(true);

                //  set an icon that's the right color
                int iconSize = 48;
                int alpha = (sep.rgba >> 24) & 0xFF;
                int red = (sep.rgba >> 16) & 0xFF;
                int green = (sep.rgba >> 8) & 0xFF;
                int blue = (sep.rgba >> 0) & 0xFF;
                int color = (alpha << 24) | (red << 16) | (green << 8) | (blue << 0);

                ShapeDrawable swatch = new ShapeDrawable(new RectShape());
                swatch.setIntrinsicHeight(iconSize);
                swatch.setIntrinsicWidth(iconSize);
                swatch.setBounds(new Rect(0, 0, iconSize, iconSize));
                swatch.getPaint().setColor(color);
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                item.setIcon(swatch);
                item.setChecked(mSepEnabled[currentPage][i]);
                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int sep = item.getItemId();
                        mSepEnabled[currentPage][sep] = !mSepEnabled[currentPage][sep];
                        item.setChecked(mSepEnabled[currentPage][sep]);
                        core.controlSepOnPage(currentPage, sep, !mSepEnabled[currentPage][sep]);
                        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                        item.setActionView(new View(v.getContext()));
                        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                            @Override
                            public boolean onMenuItemActionExpand(MenuItem item) {
                                return false;
                            }

                            @Override
                            public boolean onMenuItemActionCollapse(MenuItem item) {
                                return false;
                            }
                        });
                        return false;
                    }
                });

                core.controlSepOnPage(currentPage, i, !mSepEnabled[currentPage][i]);
            }

            MenuItem itemDone = menu.getMenu().add(0, 0, 0, "Done");
            itemDone.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    readerBinding.readerPager.refresh(false);
                    return true;
                }
            });

            menu.show();
        }

    }


    public void OnCopyTextButtonClick(View v) {
        mTopBarMode = ReaderActivity.TopBarMode.Accept;
        readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
        mAcceptMode = ReaderActivity.AcceptMode.CopyText;
        readerBinding.readerPager.setMode(MuPDFReaderView.Mode.Selecting);
        readerBinding.toolBar.tvAnnotType.setText(getString(R.string.copy_text));
        showInfo(getString(R.string.select_text));
    }

    public void OnEditAnnotButtonClick(View v) {
        mTopBarMode = ReaderActivity.TopBarMode.Annot;
        readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
    }

    public void OnCancelAnnotButtonClick(View v) {
        mTopBarMode = ReaderActivity.TopBarMode.More;
        readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
    }

    public void OnHighlightButtonClick(View v) {
        mTopBarMode = ReaderActivity.TopBarMode.Accept;
        readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
        mAcceptMode = ReaderActivity.AcceptMode.Highlight;
        readerBinding.readerPager.setMode(MuPDFReaderView.Mode.Selecting);
        readerBinding.toolBar.tvAnnotType.setText(R.string.highlight);
        showInfo(getString(R.string.select_text));
    }

    public void OnUnderlineButtonClick(View v) {
        mTopBarMode = ReaderActivity.TopBarMode.Accept;
        readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
        mAcceptMode = ReaderActivity.AcceptMode.Underline;
        readerBinding.readerPager.setMode(MuPDFReaderView.Mode.Selecting);
        readerBinding.toolBar.tvAnnotType.setText(R.string.underline);
        showInfo(getString(R.string.select_text));
    }

    public void OnStrikeOutButtonClick(View v) {
        mTopBarMode = ReaderActivity.TopBarMode.Accept;
        readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
        mAcceptMode = ReaderActivity.AcceptMode.StrikeOut;
        readerBinding.readerPager.setMode(MuPDFReaderView.Mode.Selecting);
        readerBinding.toolBar.tvAnnotType.setText(R.string.strike_out);
        showInfo(getString(R.string.select_text));
    }

    public void OnInkButtonClick(View v) {
        mTopBarMode = ReaderActivity.TopBarMode.Accept;
        readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
        mAcceptMode = ReaderActivity.AcceptMode.Ink;
        readerBinding.readerPager.setMode(MuPDFReaderView.Mode.Drawing);
        readerBinding.toolBar.tvAnnotType.setText(R.string.ink);
        showInfo(getString(R.string.draw_annotation));
    }

    public void OnCancelAcceptButtonClick(View v) {
        MuPDFView pageView = (MuPDFView) readerBinding.readerPager.getDisplayedView();
        if (pageView != null) {
            pageView.deselectText();
            pageView.cancelDraw();
        }
        readerBinding.readerPager.setMode(MuPDFReaderView.Mode.Viewing);
        switch (mAcceptMode) {
            case CopyText:
                mTopBarMode = ReaderActivity.TopBarMode.More;
                break;
            default:
                mTopBarMode = ReaderActivity.TopBarMode.Annot;
                break;
        }
        readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
    }

    public void OnAcceptButtonClick(View v) {
        MuPDFView pageView = (MuPDFView) readerBinding.readerPager.getDisplayedView();
        boolean success = false;
        switch (mAcceptMode) {
            case CopyText:
                if (pageView != null)
                    success = pageView.copySelection();
                mTopBarMode = ReaderActivity.TopBarMode.More;
                showInfo(success ? getString(R.string.copied_to_clipboard) : getString(R.string.no_text_selected));
                break;

            case Highlight:
                if (pageView != null)
                    success = pageView.markupSelection(Annotation.HIGHLIGHT);
                mTopBarMode = ReaderActivity.TopBarMode.Annot;
                if (!success)
                    showInfo(getString(R.string.no_text_selected));
                break;

            case Underline:
                if (pageView != null)
                    success = pageView.markupSelection(Annotation.UNDERLINE);
                mTopBarMode = ReaderActivity.TopBarMode.Annot;
                if (!success)
                    showInfo(getString(R.string.no_text_selected));
                break;

            case StrikeOut:
                if (pageView != null)
                    success = pageView.markupSelection(Annotation.STRIKEOUT);
                mTopBarMode = ReaderActivity.TopBarMode.Annot;
                if (!success)
                    showInfo(getString(R.string.no_text_selected));
                break;

            case Ink:
                if (pageView != null)
                    success = pageView.saveDraw();
                mTopBarMode = ReaderActivity.TopBarMode.Annot;
                if (!success)
                    showInfo(getString(R.string.nothing_to_save));
                break;
        }
        readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
        readerBinding.readerPager.setMode(MuPDFReaderView.Mode.Viewing);
    }

    public void OnCancelSearchButtonClick(View v) {
        searchModeOff();
    }

    public void OnDeleteButtonClick(View v) {
        MuPDFView pageView = (MuPDFView) readerBinding.readerPager.getDisplayedView();
        if (pageView != null)
            pageView.deleteSelectedAnnotation();
        mTopBarMode = ReaderActivity.TopBarMode.Annot;
        readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
    }

    public void OnCancelDeleteButtonClick(View v) {
        MuPDFView pageView = (MuPDFView) readerBinding.readerPager.getDisplayedView();
        if (pageView != null)
            pageView.deselectAnnotation();
        mTopBarMode = ReaderActivity.TopBarMode.Annot;
        readerBinding.toolBar.switcher.setDisplayedChild(mTopBarMode.ordinal());
    }

    public void OnProofButtonClick(final View v) {
        //  set up the menu or resolutions.
        final PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add(0, 1, 0, "Select a resolution:");
        popup.getMenu().add(0, 72, 0, "72");
        popup.getMenu().add(0, 96, 0, "96");
        popup.getMenu().add(0, 150, 0, "150");
        popup.getMenu().add(0, 300, 0, "300");
        popup.getMenu().add(0, 600, 0, "600");
        popup.getMenu().add(0, 1200, 0, "1200");
        popup.getMenu().add(0, 2400, 0, "2400");

        MenuItem item = popup.getMenu().getItem(0);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        item.setActionView(new View(v.getContext()));
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return false;
            }
        });

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id != 1) {
                    proofWithResolution(id);
                    return true;
                }
                return false;
            }
        });
        popup.show();
    }

    public boolean isProofing() {
        String format = core.fileFormat();
        return (format.equals("GPROOF"));
    }


}
