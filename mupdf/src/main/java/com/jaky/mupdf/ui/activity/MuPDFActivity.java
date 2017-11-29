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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.jaky.mupdf.R;
import com.jaky.mupdf.binding.MainToolBarModel;
import com.jaky.mupdf.data.ReaderConstants;
//import com.jaky.mupdf.databinding.MainToolBarBinding;
import com.jaky.mupdf.databinding.MainToolBarBinding;
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
import com.jaky.mupdf.ui.views.baseview.MuPDFView;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class MuPDFActivity extends Activity implements FilePicker.FilePickerSupport {
    private static final String TAG = MuPDFActivity.class.getSimpleName();
    private MainToolBarBinding mToolBarbinding;

    /* The core rendering instance */
    enum TopBarMode {
        Main, Search, Annot, Delete, More, Accept
    }

    ;

    enum AcceptMode {Highlight, Underline, StrikeOut, Ink, CopyText}

    ;

    private final int OUTLINE_REQUEST = 0;
    private final int PRINT_REQUEST = 1;
    private final int FILEPICK_REQUEST = 2;
    private final int PROOF_REQUEST = 3;
    private MuPDFCore core;
    private String mFileName;
    private MuPDFReaderView mDocView;
    private View mButtonsView;
    private boolean mButtonsVisible;
    private EditText mPasswordView;
    private int mPageSliderRes;

    private TopBarMode mTopBarMode = TopBarMode.Main;
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

    private String[] pickFiles = new String[]{
            ".pdf", ".xps", ".cbz", ".epub", ".png", ".jpe", ".jpeg", ".jpg", ".jfif", ".jfif-tbnl", ".tif", ".tiff"};

    static private AlertDialog.Builder gAlertBuilder;

    static public AlertDialog.Builder getAlertBuilder() {
        return gAlertBuilder;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getCore(savedInstanceState)) return;
        createUI(savedInstanceState);
    }

    private boolean getCore(Bundle savedInstanceState) {
        if (core == null) {
            core = (MuPDFCore) getLastNonConfigurationInstance();

            if (savedInstanceState != null && savedInstanceState.containsKey("FileName")) {
                mFileName = savedInstanceState.getString("FileName");
            }
        }
        if (core == null) {
            Intent intent = getIntent();
            byte buffer[] = null;

            if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                Uri uri = intent.getData();
                System.out.println("URI to open is: " + uri);
                if (uri.toString().startsWith("content://")) {
                    String reason = null;
                    try {
                        InputStream is = getContentResolver().openInputStream(uri);
                        int len = is.available();
                        buffer = new byte[len];
                        is.read(buffer, 0, len);
                        is.close();
                    } catch (OutOfMemoryError e) {
                        System.out.println("Out of memory during buffer reading");
                        reason = e.toString();
                    } catch (Exception e) {
                        System.out.println("Exception reading from stream: " + e);
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
                            System.out.println("Exception in Transformer Prime file manager code: " + e2);
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
                            createUI(savedInstanceState);
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

    public void createUI(Bundle savedInstanceState) {
        if (core == null)
            return;
        // First create the document view
        mDocView = new MuPDFReaderView(this) {
            @Override
            protected void onMoveToChild(int i) {
                if (core == null)
                    return;

                mToolBarbinding.tvPageNumber.setText(String.format("%d / %d", i + 1,
                        core.countPages()));
                mToolBarbinding.sbPageSlider.setMax((core.countPages() - 1) * mPageSliderRes);
                mToolBarbinding.sbPageSlider.setProgress(i * mPageSliderRes);
                super.onMoveToChild(i);
            }

            @Override
            protected void onTapMainDocArea() {
                if (!mButtonsVisible) {
                    showButtons();
                } else {
                    if (mTopBarMode == TopBarMode.Main)
                        hideButtons();
                }
            }

            @Override
            protected void onDocMotion() {
                hideButtons();
            }

            @Override
            protected void onHit(@ReaderConstants.Hit String item) {
                switch (mTopBarMode) {
                    case Annot:
                        if (ReaderConstants.ANNOTATION.equals(item)) {
                            showButtons();
                            mTopBarMode = TopBarMode.Delete;
                            mToolBarbinding.switcher.setDisplayedChild(mTopBarMode.ordinal());
                        }
                        break;
                    case Delete:
                        mTopBarMode = TopBarMode.Annot;
                        mToolBarbinding.switcher.setDisplayedChild(mTopBarMode.ordinal());
                        // fall through
                    default:
                        // Not in annotation editing mode, but the pageview will
                        // still select and highlight hit annotations, so
                        // deselect just in case.
                        MuPDFView pageView = (MuPDFView) mDocView.getDisplayedView();
                        if (pageView != null)
                            pageView.deselectAnnotation();
                        break;
                }
            }
        };
        mDocView.setAdapter(new MuPDFPageAdapter(this, this, core));

        mSearchTask = new SearchTask(this, core) {
            @Override
            protected void onTextFound(SearchTaskResult result) {
                SearchTaskResult.set(result);
                // Ask the ReaderView to move to the resulting page
                mDocView.setDisplayedViewIndex(result.pageNumber);
                // Make the ReaderView act on the change to SearchTaskResult
                // via overridden onChildSetup method.
                mDocView.resetupChildren();
            }
        };
        mToolBarbinding = DataBindingUtil.inflate( getLayoutInflater(), R.layout.main_tool_bar, null, false);
        mToolBarbinding.setToolBarModel(new MainToolBarModel(this, core));
        if (!core.gprfSupported()) {
            mToolBarbinding.proofButton.setVisibility(View.INVISIBLE);
        }
        mToolBarbinding.sepsButton.setVisibility(View.INVISIBLE);


        int smax = Math.max(core.countPages() - 1, 1);
        mPageSliderRes = ((10 + smax - 1) / smax) * 2;
        mToolBarbinding.tvDocName.setText(mFileName);
        mToolBarbinding.sbPageSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar seekBar) {
                mDocView.setDisplayedViewIndex((seekBar.getProgress() + mPageSliderRes / 2) / mPageSliderRes);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                updatePageNumView((progress + mPageSliderRes / 2) / mPageSliderRes);
            }
        });

        // Activate the search-preparing button
        mToolBarbinding.searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                searchModeOn();
            }
        });

        // Activate the reflow button
        mToolBarbinding.reflowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toggleReflow();
            }
        });

        if (core.fileFormat().startsWith("PDF") && core.isUnencryptedPDF() && !core.wasOpenedFromBuffer()) {
            mToolBarbinding.editAnnotButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mTopBarMode = TopBarMode.Annot;
                    mToolBarbinding.switcher.setDisplayedChild(mTopBarMode.ordinal());
                }
            });
        } else {
            mToolBarbinding.editAnnotButton.setVisibility(View.GONE);
        }

        // Search invoking main_tool_bar are disabled while there is no text specified
        mToolBarbinding.btnSearchBack.setEnabled(false);
        mToolBarbinding.btnsSearchForward.setEnabled(false);
        mToolBarbinding.btnSearchBack.setColorFilter(Color.argb(255, 128, 128, 128));
        mToolBarbinding.btnsSearchForward.setColorFilter(Color.argb(255, 128, 128, 128));

        // React to interaction with the text widget
        mToolBarbinding.etSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                boolean haveText = s.toString().length() > 0;
                setButtonEnabled(mToolBarbinding.btnSearchBack, haveText);
                setButtonEnabled(mToolBarbinding.btnsSearchForward, haveText);

                // Remove any previous search results
                if (SearchTaskResult.get() != null && !mToolBarbinding.etSearch.getText().toString().equals(SearchTaskResult.get().txt)) {
                    SearchTaskResult.set(null);
                    mDocView.resetupChildren();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });

        //React to Done button on keyboard
        mToolBarbinding.etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    search(1);
                return false;
            }
        });

        mToolBarbinding.etSearch.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
                    search(1);
                return false;
            }
        });

        // Activate search invoking main_tool_bar
        mToolBarbinding.btnSearchBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search(-1);
            }
        });
        mToolBarbinding.btnsSearchForward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search(1);
            }
        });

        mToolBarbinding.linkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setLinkHighlight(!mLinkHighlight);
            }
        });

        if (core.hasOutline()) {
            mToolBarbinding.outlineButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    OutlineItem outline[] = core.getOutline();
                    if (outline != null) {
                        OutlineActivityData.get().items = outline;
                        Intent intent = new Intent(MuPDFActivity.this, OutlineActivity.class);
                        startActivityForResult(intent, OUTLINE_REQUEST);
                    }
                }
            });
        } else {
            mToolBarbinding.outlineButton.setVisibility(View.GONE);
        }

        // Reenstate last state if it was recorded
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        mDocView.setDisplayedViewIndex(prefs.getInt("page" + mFileName, 0));

        if (savedInstanceState == null || !savedInstanceState.getBoolean("ButtonsHidden", false))
            showButtons();

        if (savedInstanceState != null && savedInstanceState.getBoolean("SearchMode", false))
            searchModeOn();

        if (savedInstanceState != null && savedInstanceState.getBoolean("ReflowMode", false))
            reflowModeSet(true);

        // Stick the document view and the main_tool_bar overlay into a parent view
        RelativeLayout layout = new RelativeLayout(this);
        layout.addView(mDocView);
        layout.addView(mButtonsView);
        setContentView(layout);

        if (mToolBarbinding.getToolBarModel().isProof()) {
            int currentPage = getIntent().getIntExtra("startingPage", 0);
            mDocView.setDisplayedViewIndex(currentPage);
        }

    }

    public Object onRetainNonConfigurationInstance() {
        MuPDFCore mycore = core;
        core = null;
        return mycore;
    }

    private void reflowModeSet(boolean reflow) {
        mReflow = reflow;
        mDocView.setAdapter(mReflow ? new MuPDFReflowAdapter(this, core) : new MuPDFPageAdapter(this, this, core));
        mToolBarbinding.reflowButton.setColorFilter(mReflow ? Color.argb(0xFF, 172, 114, 37) : Color.argb(0xFF, 255, 255, 255));
        setButtonEnabled(mToolBarbinding.editAnnotButton, !reflow);
        setButtonEnabled(mToolBarbinding.searchButton, !reflow);
        if (reflow) setLinkHighlight(false);
        setButtonEnabled(mToolBarbinding.linkButton, !reflow);
        setButtonEnabled(mToolBarbinding.moreButton, !reflow);
        mDocView.refresh(mReflow);
    }

    private void toggleReflow() {
        reflowModeSet(!mReflow);
        showInfo(mReflow ? getString(R.string.entering_reflow_mode) : getString(R.string.leaving_reflow_mode));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mFileName != null && mDocView != null) {
            outState.putString("FileName", mFileName);

            // Store current page in the prefs against the file name,
            // so that we can pick it up each time the file is loaded
            // Other info is needed only for screen-orientation change,
            // so it can go in the bundle
            SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt("page" + mFileName, mDocView.getDisplayedViewIndex());
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

        if (mFileName != null && mDocView != null) {
            SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt("page" + mFileName, mDocView.getDisplayedViewIndex());
            edit.commit();
        }
    }

    public void onDestroy() {
        if (mDocView != null) {
            mDocView.applyToChildren(new ViewMapper() {
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
        // LINK_COLOR tint
        mToolBarbinding.linkButton.setColorFilter(highlight ? Color.argb(0xFF, 172, 114, 37) : Color.argb(0xFF, 255, 255, 255));
        // Inform pages of the change.
        mDocView.setLinksEnabled(highlight);
    }

    private void showButtons() {
        if (core == null)
            return;
        if (!mButtonsVisible) {
            mButtonsVisible = true;
            // Update page number text and slider
            int index = mDocView.getDisplayedViewIndex();
            updatePageNumView(index);
            mToolBarbinding.sbPageSlider.setMax((core.countPages() - 1) * mPageSliderRes);
            mToolBarbinding.sbPageSlider.setProgress(index * mPageSliderRes);
            if (mTopBarMode == TopBarMode.Search) {
                mToolBarbinding.etSearch.requestFocus();
                showKeyboard();
            }

            Animation anim = new TranslateAnimation(0, 0, -mToolBarbinding.switcher.getHeight(), 0);
            anim.setDuration(200);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    mToolBarbinding.switcher.setVisibility(View.VISIBLE);
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                }
            });
            mToolBarbinding.switcher.startAnimation(anim);

            anim = new TranslateAnimation(0, 0, mToolBarbinding.sbPageSlider.getHeight(), 0);
            anim.setDuration(200);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    mToolBarbinding.sbPageSlider.setVisibility(View.VISIBLE);
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    mToolBarbinding.tvPageNumber.setVisibility(View.VISIBLE);
                }
            });
            mToolBarbinding.sbPageSlider.startAnimation(anim);
        }
    }

    private void hideButtons() {
        if (mButtonsVisible) {
            mButtonsVisible = false;
            hideKeyboard();

            Animation anim = new TranslateAnimation(0, 0, 0, -mToolBarbinding.switcher.getHeight());
            anim.setDuration(200);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    mToolBarbinding.switcher.setVisibility(View.INVISIBLE);
                }
            });
            mToolBarbinding.switcher.startAnimation(anim);

            anim = new TranslateAnimation(0, 0, 0, mToolBarbinding.sbPageSlider.getHeight());
            anim.setDuration(200);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    mToolBarbinding.tvPageNumber.setVisibility(View.INVISIBLE);
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    mToolBarbinding.sbPageSlider.setVisibility(View.INVISIBLE);
                }
            });
            mToolBarbinding.sbPageSlider.startAnimation(anim);
        }
    }

    private void searchModeOn() {
        if (mTopBarMode != TopBarMode.Search) {
            mTopBarMode = TopBarMode.Search;
            //Focus on EditTextWidget
            mToolBarbinding.etSearch.requestFocus();
            showKeyboard();
            mToolBarbinding.switcher.setDisplayedChild(mTopBarMode.ordinal());
        }
    }

    private void searchModeOff() {
        if (mTopBarMode == TopBarMode.Search) {
            mTopBarMode = TopBarMode.Main;
            hideKeyboard();
            mToolBarbinding.switcher.setDisplayedChild(mTopBarMode.ordinal());
            SearchTaskResult.set(null);
            // Make the ReaderView act on the change to mSearchTaskResult
            // via overridden onChildSetup method.
            mDocView.resetupChildren();
        }
    }

    private void updatePageNumView(int index) {
        if (core == null)
            return;
        mToolBarbinding.tvPageNumber.setText(String.format("%d / %d", index + 1, core.countPages()));
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
        mToolBarbinding.tvInfo.setText(message);

        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            SafeAnimatorInflater safe = new SafeAnimatorInflater((Activity) this, R.animator.info, (View) mToolBarbinding.tvInfo);
        } else {
            mToolBarbinding.tvInfo.setVisibility(View.VISIBLE);
            mHandler.postDelayed(new Runnable() {
                public void run() {
                    mToolBarbinding.tvInfo.setVisibility(View.INVISIBLE);
                }
            }, 500);
        }
    }

    public void proofWithResolution(int resolution) {
        mProofFile = core.startProof(resolution);
        Uri uri = Uri.parse("file://" + mProofFile);
        Intent intent = new Intent(this, MuPDFActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(uri);
        // add the current page so it can be found when the activity is running
        intent.putExtra("startingPage", mDocView.getDisplayedViewIndex());
        startActivityForResult(intent, PROOF_REQUEST);
    }


    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.showSoftInput(mToolBarbinding.etSearch, 0);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.hideSoftInputFromWindow(mToolBarbinding.etSearch.getWindowToken(), 0);
    }

    private void search(int direction) {
        hideKeyboard();
        int displayPage = mDocView.getDisplayedViewIndex();
        SearchTaskResult r = SearchTaskResult.get();
        int searchPage = r != null ? r.pageNumber : -1;
        mSearchTask.go(mToolBarbinding.etSearch.getText().toString(), direction, displayPage, searchPage);
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
                    mDocView.setDisplayedViewIndex(resultCode);
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
                //  we're returning from a proofing activity

                if (mProofFile != null) {
                    core.endProof(mProofFile);
                    mProofFile = null;
                }

                //  return the top bar to default
                mTopBarMode = TopBarMode.Main;
                mToolBarbinding.switcher.setDisplayedChild(mTopBarMode.ordinal());
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
                // core.waitForAlert may return null when shutting down
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
                            // Send the user's response to the core, so that it can
                            // continue processing.
                            core.replyToAlert(result);
                            // Create another alert-waiter to pick up the next alert.
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
            System.out.println(e);
            return null;
        } catch (OutOfMemoryError e) {
            //  out of memory is not an Exception, so we catch it separately.
            System.out.println(e);
            return null;
        }
        return core;
    }

    private MuPDFCore openBuffer(byte buffer[], String magic) {
        System.out.println("Trying to open byte buffer");
        try {
            core = new MuPDFCore(this, buffer, magic);
            // New file: drop the old outline data
            OutlineActivityData.set(null);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
        return core;
    }

    //  determine whether the current activity is a proofing activity.


}
