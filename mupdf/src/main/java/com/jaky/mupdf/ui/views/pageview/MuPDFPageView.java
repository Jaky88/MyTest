package com.jaky.mupdf.ui.views.pageview;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.jaky.mupdf.R;
import com.jaky.mupdf.async.PassClickResult;
import com.jaky.mupdf.async.PassClickResultChoice;
import com.jaky.mupdf.async.PassClickResultSignature;
import com.jaky.mupdf.async.PassClickResultText;
import com.jaky.mupdf.async.PassClickResultVisitor;
import com.jaky.mupdf.async.TextProcessor;
import com.jaky.mupdf.core.MuPDFCore;
import com.jaky.mupdf.data.Annotation;
import com.jaky.mupdf.data.FilePicker;
import com.jaky.mupdf.data.LinkInfo;
import com.jaky.mupdf.data.ReaderConstants;
import com.jaky.mupdf.data.TextWord;
import com.jaky.mupdf.task.AsyncTask;
import com.jaky.mupdf.task.AsyncTaskImpl;
import com.jaky.mupdf.task.MuPDFCancellableTaskDefinition;
import com.jaky.mupdf.ui.views.baseview.MuPDFView;

import java.util.ArrayList;

public class MuPDFPageView extends PageView implements MuPDFView {

    private int mSelectedAnnotationIndex = -1;

    private RectF mWidgetAreas[];
    private Annotation mAnnotations[];
    final private FilePicker.FilePickerSupport mFilePickerSupport;
    private final MuPDFCore mCore;
    private Runnable changeReporter;

    private AlertDialog mTextEntryDialog;
    private AlertDialog mPasswordEntryDialog;
    private EditText mEtPassword;
    private EditText mEtText;

    private AsyncTask<String, Void, Boolean> mSetWidgetText;
    private AsyncTask<String, Void, Void> mSetWidgetChoice;
    private AsyncTask<PointF[], Void, Void> mAddStrikeOut;
    private AsyncTask<PointF[][], Void, Void> mAddInk;
    private AsyncTask<Integer, Void, Void> mDeleteAnnotation;
    private AsyncTask<Void, Void, String> mCheckSignature;
    private AsyncTask<Void, Void, RectF[]> mLoadWidgetAreas;
    private AsyncTask<Void, Void, Annotation[]> mLoadAnnotations;
    private AsyncTask<Void, Void, Boolean> mSign;
    private AsyncTask<Void, Void, PassClickResult> mPassClick;

    public MuPDFPageView(Context c, FilePicker.FilePickerSupport filePickerSupport, MuPDFCore core,
                         Point parentSize, Bitmap emptyHqBmp) {
        super(c, parentSize, emptyHqBmp);
        mFilePickerSupport = filePickerSupport;
        mCore = core;
    }

    //==========================Dialog=================================
    private void createPwdEntryDialog(Context c) {
        mEtPassword = new EditText(c);
        mEtPassword.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        mEtPassword.setTransformationMethod(new PasswordTransformationMethod());

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(R.string.enter_password);
        builder.setView(mEtPassword);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mPasswordEntryDialog = builder.create();
    }

    private void createTextEntryDialog(Context c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(getContext().getString(R.string.fill_out_text_field));

        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mEtText = (EditText) inflater.inflate(R.layout.textentry, null);
        builder.setView(mEtText);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mSetWidgetText = new AsyncTask<String, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(String... arg0) {
                        return mCore.setFocusedWidgetText(mPageNumber, arg0[0]);
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        changeReporter.run();
                        if (!result)
                            showTextDialog(mEtText.getText().toString());
                    }
                };

                mSetWidgetText.execute(mEtText.getText().toString());
            }
        });
        mTextEntryDialog = builder.create();
    }


    private void showTextDialog(String text) {
        mEtText.setText(text);
        if (mTextEntryDialog == null) {
            createTextEntryDialog(getContext());
        }
        mTextEntryDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mTextEntryDialog.show();
    }

    private void showChoiceDialog(final String[] options) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getString(R.string.choose_value));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mSetWidgetChoice = new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... params) {
                        String[] sel = {params[0]};
                        mCore.setFocusedWidgetChoiceSelected(sel);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        changeReporter.run();
                    }
                };

                mSetWidgetChoice.execute(options[which]);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showSignatureCheckingDialog() {
        mCheckSignature = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return mCore.checkFocusedSignature();
            }

            @Override
            protected void onPostExecute(String result) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Signature checked");
                builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog report = builder.create();
                report.setMessage(result);
                report.show();
            }
        };

        mCheckSignature.execute();
    }

    private void showSigningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select certificate and sign?");
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FilePicker picker = new FilePicker(mFilePickerSupport) {
                    @Override
                    public void onPick(Uri uri) {
                        signWithKeyFile(uri);
                    }
                };

                picker.pick();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void signWithKeyFile(final Uri uri) {
        if (mPasswordEntryDialog == null) {
            createPwdEntryDialog(getContext());
        }
        mPasswordEntryDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mPasswordEntryDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sign", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                signWithKeyFileAndPassword(uri, mEtPassword.getText().toString());
            }
        });

        mPasswordEntryDialog.show();
    }

    private void signWithKeyFileAndPassword(final Uri uri, final String password) {
        mSign = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return mCore.signFocusedSignature(Uri.decode(uri.getEncodedPath()), password);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    changeReporter.run();
                } else {
                    mEtPassword.setText("");
                    signWithKeyFile(uri);
                }
            }

        };

        mSign.execute();
    }



    private void showNoSignatureSupportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Signature checked");
        builder.setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setTitle("App built with no signature support");
        dialog.show();
    }

    //====================================================================


    public LinkInfo hitLink(float x, float y) {
        float scale = mSrcScale * (float) getWidth() / (float) mPageSize.x;
        float docRelX = (x - getLeft()) / scale;
        float docRelY = (y - getTop()) / scale;

        for (LinkInfo l : mLinks)
            if (l.rect.contains(docRelX, docRelY))
                return l;

        return null;
    }

    public void setChangeReporter(Runnable reporter) {
        changeReporter = reporter;
    }

    @ReaderConstants.Hit
    public String passClickEvent(float x, float y) {
        float scale = mSrcScale * (float) getWidth() / (float) mPageSize.x;
        final float docRelX = (x - getLeft()) / scale;
        final float docRelY = (y - getTop()) / scale;
        boolean hit = false;
        int i;

        if (mAnnotations != null) {
            for (i = 0; i < mAnnotations.length; i++)
                if (mAnnotations[i].contains(docRelX, docRelY)) {
                    hit = true;
                    break;
                }

            if (hit) {
                switch (mAnnotations[i].type) {
                    case Annotation.HIGHLIGHT:
                    case Annotation.UNDERLINE:
                    case Annotation.SQUIGGLY:
                    case Annotation.STRIKEOUT:
                    case Annotation.INK:
                        mSelectedAnnotationIndex = i;
                        setItemSelectBox(mAnnotations[i]);
                        return ReaderConstants.ANNOTATION;
                }
            }
        }

        mSelectedAnnotationIndex = -1;
        setItemSelectBox(null);

        if (!mCore.javascriptSupported())
            return ReaderConstants.NOTHING;

        if (mWidgetAreas != null) {
            for (i = 0; i < mWidgetAreas.length && !hit; i++)
                if (mWidgetAreas[i].contains(docRelX, docRelY))
                    hit = true;
        }

        if (hit) {
            mPassClick = new AsyncTask<Void, Void, PassClickResult>() {
                @Override
                protected PassClickResult doInBackground(Void... arg0) {
                    return mCore.passClickEvent(mPageNumber, docRelX, docRelY);
                }

                @Override
                protected void onPostExecute(PassClickResult result) {
                    if (result.changed) {
                        changeReporter.run();
                    }

                    result.acceptVisitor(new PassClickResultVisitor() {
                        @Override
                        public void visitText(PassClickResultText result) {
                            showTextDialog(result.text);
                        }

                        @Override
                        public void visitChoice(PassClickResultChoice result) {
                            showChoiceDialog(result.options);
                        }

                        @Override
                        public void visitSignature(PassClickResultSignature result) {
                            switch (result.state) {
                                case ReaderConstants.NOSUPPORT:
                                    showNoSignatureSupportDialog();
                                    break;
                                case ReaderConstants.UNSIGNED:
                                    showSigningDialog();
                                    break;
                                case ReaderConstants.SIGNED:
                                    showSignatureCheckingDialog();
                                    break;
                            }
                        }
                    });
                }
            };

            mPassClick.execute();
            return ReaderConstants.WIDGET;
        }

        return ReaderConstants.NOTHING;
    }

    @TargetApi(11)
    public boolean copySelection() {
        final StringBuilder text = new StringBuilder();

        processSelectedText(new TextProcessor() {
            StringBuilder line;

            public void onStartLine() {
                line = new StringBuilder();
            }

            public void onWord(TextWord word) {
                if (line.length() > 0)
                    line.append(' ');
                line.append(word.w);
            }

            public void onEndLine() {
                if (text.length() > 0)
                    text.append('\n');
                text.append(line);
            }
        });

        if (text.length() == 0)
            return false;

        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.HONEYCOMB) {
            android.content.ClipboardManager cm = (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);

            cm.setPrimaryClip(ClipData.newPlainText("MuPDF", text));
        } else {
            android.text.ClipboardManager cm = (android.text.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(text);
        }

        deselectText();

        return true;
    }

    public boolean markupSelection(final @Annotation.Type int type) {
        final ArrayList<PointF> quadPoints = new ArrayList<PointF>();
        processSelectedText(new TextProcessor() {
            RectF rect;

            public void onStartLine() {
                rect = new RectF();
            }

            public void onWord(TextWord word) {
                rect.union(word);
            }

            public void onEndLine() {
                if (!rect.isEmpty()) {
                    quadPoints.add(new PointF(rect.left, rect.bottom));
                    quadPoints.add(new PointF(rect.right, rect.bottom));
                    quadPoints.add(new PointF(rect.right, rect.top));
                    quadPoints.add(new PointF(rect.left, rect.top));
                }
            }
        });

        if (quadPoints.size() == 0)
            return false;

        mAddStrikeOut = new AsyncTask<PointF[], Void, Void>() {
            @Override
            protected Void doInBackground(PointF[]... params) {
                addMarkup(params[0], type);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                loadAnnotations();
                update();
            }
        };

        mAddStrikeOut.execute(quadPoints.toArray(new PointF[quadPoints.size()]));

        deselectText();

        return true;
    }

    public void deleteSelectedAnnotation() {
        if (mSelectedAnnotationIndex != -1) {
            if (mDeleteAnnotation != null)
                mDeleteAnnotation.cancel(true);

            mDeleteAnnotation = new AsyncTask<Integer, Void, Void>() {
                @Override
                protected Void doInBackground(Integer... params) {
                    mCore.deleteAnnotation(mPageNumber, params[0]);
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    loadAnnotations();
                    update();
                }
            };

            mDeleteAnnotation.execute(mSelectedAnnotationIndex);

            mSelectedAnnotationIndex = -1;
            setItemSelectBox(null);
        }
    }

    public void deselectAnnotation() {
        mSelectedAnnotationIndex = -1;
        setItemSelectBox(null);
    }

    public boolean saveDraw() {
        PointF[][] path = getDraw();

        if (path == null)
            return false;

        if (mAddInk != null) {
            mAddInk.cancel(true);
            mAddInk = null;
        }
        mAddInk = new AsyncTask<PointF[][], Void, Void>() {
            @Override
            protected Void doInBackground(PointF[][]... params) {
                mCore.addInkAnnotation(mPageNumber, params[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                loadAnnotations();
                update();
            }

        };

        mAddInk.execute(getDraw());
        cancelDraw();

        return true;
    }


    @Override
    protected AsyncTaskImpl<Void, Void> doDrawPage(final Bitmap bitmap, final int sizeX, final int sizeY,
                                                   final int patchX, final int patchY, final int patchWidth, final int patchHeight) {
        return new MuPDFCancellableTaskDefinition<Void, Void>(mCore) {
            @Override
            public Void doInBackground(MuPDFCore.Cookie cookie, Void... params) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    bitmap.eraseColor(0);
                }

                //绘制页面
                mCore.drawPage(bitmap, mPageNumber, sizeX, sizeY, patchX, patchY, patchWidth, patchHeight, cookie);
                return null;
            }
        };

    }

    //绘制页面过程
    protected AsyncTaskImpl<Void, Void> doUpdatePage(final Bitmap bitmap,
                                                     final int sizeX, final int sizeY,
                                                     final int patchX, final int patchY,
                                                     final int patchWidth, final int patchHeight) {
        return new MuPDFCancellableTaskDefinition<Void, Void>(mCore) {

            @Override
            public Void doInBackground(MuPDFCore.Cookie cookie, Void... params) {
                // 11 <= sdk < 14
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    bitmap.eraseColor(0);
                }

                //更新页面
                mCore.updatePage(bitmap, mPageNumber, sizeX, sizeY, patchX, patchY, patchWidth, patchHeight, cookie);
                return null;
            }
        };
    }

    @Override
    protected LinkInfo[] getLinkInfo() {
        return mCore.getPageLinks(mPageNumber);
    }

    @Override
    protected TextWord[][] getText() {
        return mCore.textLines(mPageNumber);
    }

    @Override
    protected void addMarkup(PointF[] quadPoints, @Annotation.Type int type) {
        mCore.addMarkupAnnotation(mPageNumber, quadPoints, type);
    }

    private void loadAnnotations() {
        mAnnotations = null;
        if (mLoadAnnotations != null)
            mLoadAnnotations.cancel(true);
        mLoadAnnotations = new AsyncTask<Void, Void, Annotation[]>() {
            @Override
            protected Annotation[] doInBackground(Void... params) {
                return mCore.getAnnoations(mPageNumber);
            }

            @Override
            protected void onPostExecute(Annotation[] result) {
                mAnnotations = result;
            }
        };

        mLoadAnnotations.execute();
    }

    @Override
    public void setPage(final int pageNum, PointF pageSize) {
        loadAnnotations();

        mLoadWidgetAreas = new AsyncTask<Void, Void, RectF[]>() {
            @Override
            protected RectF[] doInBackground(Void... arg0) {
                return mCore.getWidgetAreas(pageNum);
            }

            @Override
            protected void onPostExecute(RectF[] result) {
                mWidgetAreas = result;
            }
        };

        mLoadWidgetAreas.execute();

        super.setPage(pageNum, pageSize);
    }

    public void setScale(float scale) {
    }

    @Override
    public void releaseResources() {
        if (mPassClick != null) {
            mPassClick.cancel(true);
            mPassClick = null;
        }

        if (mLoadWidgetAreas != null) {
            mLoadWidgetAreas.cancel(true);
            mLoadWidgetAreas = null;
        }

        if (mLoadAnnotations != null) {
            mLoadAnnotations.cancel(true);
            mLoadAnnotations = null;
        }

        if (mSetWidgetText != null) {
            mSetWidgetText.cancel(true);
            mSetWidgetText = null;
        }

        if (mSetWidgetChoice != null) {
            mSetWidgetChoice.cancel(true);
            mSetWidgetChoice = null;
        }

        if (mAddStrikeOut != null) {
            mAddStrikeOut.cancel(true);
            mAddStrikeOut = null;
        }

        if (mDeleteAnnotation != null) {
            mDeleteAnnotation.cancel(true);
            mDeleteAnnotation = null;
        }

        super.releaseResources();
    }
}
