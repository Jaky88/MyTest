package com.jaky.mupdf.binding;

import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.jaky.mupdf.R;
import com.jaky.mupdf.core.MuPDFCore;
import com.jaky.mupdf.data.Annotation;
import com.jaky.mupdf.data.Separation;
import com.jaky.mupdf.ui.activity.MuPDFActivity;
import com.jaky.mupdf.ui.views.adapterview.MuPDFReaderView;
import com.jaky.mupdf.ui.views.baseview.MuPDFView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Jack on 2017/11/30.
 */

public class MainToolBarModel {
    private MuPDFActivity context;
    private MuPDFCore core;
    private boolean canProof;
    private boolean proof;
    private boolean gprfSurpport;

    public boolean isCanProof() {
        return canProof;
    }

    public void setCanProof(boolean canProof) {
        this.canProof = canProof;
    }

    public boolean isProof() {
        return proof;
    }

    public void setProof(boolean proof) {
        this.proof = proof;
    }

    public boolean isGprfSurpport() {
        return gprfSurpport;
    }

    public void setGprfSurpport(boolean gprfSurpport) {
        this.gprfSurpport = gprfSurpport;
    }

    public boolean isProofing() {
        String format = core.fileFormat();
        return (format.equals("GPROOF"));
    }

    public MainToolBarModel(MuPDFActivity context, MuPDFCore core) {
        this.context = context;
        this.core = core;
        canProof = core.canProof();
        proof = isProofing();
        gprfSurpport = core.gprfSupported();
    }

    public void OnMoreButtonClick(View v) {
//        mTopBarMode = MuPDFActivity.TopBarMode.More;
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
    }

    public void OnCancelMoreButtonClick(View v) {
//        mTopBarMode = MuPDFActivity.TopBarMode.Main;
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
    }

    public void OnPrintButtonClick(View v) {
//        printDoc();
    }

    public void OnSepsButtonClick(final View v) {
//        if (isProofing()) {
//
//            //  get the current page
//            final int currentPage = mDocView.getDisplayedViewIndex();
//
//            //  buid a popup menu based on the given separations
//            final PopupMenu menu = new PopupMenu(this, v);
//
//            //  This makes the popup menu display icons, which by default it does not do.
//            //  I worry that this relies on the internals of PopupMenu, which could change.
//            try {
//                Field[] fields = menu.getClass().getDeclaredFields();
//                for (Field field : fields) {
//                    if ("mPopup".equals(field.getName())) {
//                        field.setAccessible(true);
//                        Object menuPopupHelper = field.get(menu);
//                        Class<?> classPopupHelper = Class.forName(menuPopupHelper
//                                .getClass().getName());
//                        Method setForceIcons = classPopupHelper.getMethod(
//                                "setForceShowIcon", boolean.class);
//                        setForceIcons.invoke(menuPopupHelper, true);
//                        break;
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            //  get the maximum number of seps on any page.
//            //  We use this to dimension an array further down
//            int maxSeps = 0;
//            int numPages = core.countPages();
//            for (int page=0; page<numPages; page++) {
//                int numSeps = core.getNumSepsOnPage(page);
//                if (numSeps>maxSeps)
//                    maxSeps = numSeps;
//            }
//
//            //  if this is the first time, create the "enabled" array
//            if (mSepEnabled==null) {
//                mSepEnabled = new boolean[numPages][maxSeps];
//                for (int page=0; page<numPages; page++) {
//                    for (int i = 0; i < maxSeps; i++)
//                        mSepEnabled[page][i] = true;
//                }
//            }
//
//            //  count the seps on this page
//            int numSeps = core.getNumSepsOnPage(currentPage);
//
//            //  for each sep,
//            for (int i = 0; i < numSeps; i++) {
//
////				//  Robin use this to skip separations
////				if (i==12)
////					break;
//
//                //  get the name
//                Separation sep = core.getSep(currentPage,i);
//                String name = sep.name;
//
//                //  make a checkable menu item with that name
//                //  and the separation index as the id
//                MenuItem item = menu.getMenu().add(0, i, 0, name+"    ");
//                item.setCheckable(true);
//
//                //  set an icon that's the right color
//                int iconSize = 48;
//                int alpha = (sep.rgba >> 24) & 0xFF;
//                int red   = (sep.rgba >> 16) & 0xFF;
//                int green = (sep.rgba >> 8 ) & 0xFF;
//                int blue  = (sep.rgba >> 0 ) & 0xFF;
//                int color = (alpha << 24) | (red << 16) | (green << 8) | (blue << 0);
//
//                ShapeDrawable swatch = new ShapeDrawable (new RectShape());
//                swatch.setIntrinsicHeight(iconSize);
//                swatch.setIntrinsicWidth(iconSize);
//                swatch.setBounds(new Rect(0, 0, iconSize, iconSize));
//                swatch.getPaint().setColor(color);
//                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//                item.setIcon(swatch);
//
//                //  check it (or not)
//                item.setChecked(mSepEnabled[currentPage][i]);
//
//                //  establishing a menu item listener
//                item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        //  someone tapped a menu item.  get the ID
//                        int sep = item.getItemId();
//
//                        //  toggle the sep
//                        mSepEnabled[currentPage][sep] = !mSepEnabled[currentPage][sep];
//                        item.setChecked(mSepEnabled[currentPage][sep]);
//                        core.controlSepOnPage(currentPage, sep, !mSepEnabled[currentPage][sep]);
//
//                        //  prevent the menu from being dismissed by these items
//                        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
//                        item.setActionView(new View(v.getContext()));
//                        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
//                            @Override
//                            public boolean onMenuItemActionExpand(MenuItem item) {
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onMenuItemActionCollapse(MenuItem item) {
//                                return false;
//                            }
//                        });
//                        return false;
//                    }
//                });
//
//                //  tell core to enable or disable each sep as appropriate
//                //  but don't refresh the page yet.
//                core.controlSepOnPage(currentPage, i, !mSepEnabled[currentPage][i]);
//            }
//
//            //  add one for done
//            MenuItem itemDone = menu.getMenu().add(0, 0, 0, "Done");
//            itemDone.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    //  refresh the view
//                    mDocView.refresh(false);
//                    return true;
//                }
//            });
//
//            //  show the menu
//            menu.show();
//        }

    }


    public void OnCopyTextButtonClick(View v) {
//        mTopBarMode = MuPDFActivity.TopBarMode.Accept;
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
//        mAcceptMode = MuPDFActivity.AcceptMode.CopyText;
//        mDocView.setMode(MuPDFReaderView.Mode.Selecting);
//        mAnnotTypeText.setText(getString(R.string.copy_text));
//        showInfo(getString(R.string.select_text));
    }

    public void OnEditAnnotButtonClick(View v) {
//        mTopBarMode = MuPDFActivity.TopBarMode.Annot;
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
    }

    public void OnCancelAnnotButtonClick(View v) {
//        mTopBarMode = MuPDFActivity.TopBarMode.More;
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
    }

    public void OnHighlightButtonClick(View v) {
//        mTopBarMode = MuPDFActivity.TopBarMode.Accept;
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
//        mAcceptMode = MuPDFActivity.AcceptMode.Highlight;
//        mDocView.setMode(MuPDFReaderView.Mode.Selecting);
//        mAnnotTypeText.setText(R.string.highlight);
//        showInfo(getString(R.string.select_text));
    }

    public void OnUnderlineButtonClick(View v) {
//        mTopBarMode = MuPDFActivity.TopBarMode.Accept;
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
//        mAcceptMode = MuPDFActivity.AcceptMode.Underline;
//        mDocView.setMode(MuPDFReaderView.Mode.Selecting);
//        mAnnotTypeText.setText(R.string.underline);
//        showInfo(getString(R.string.select_text));
    }

    public void OnStrikeOutButtonClick(View v) {
//        mTopBarMode = MuPDFActivity.TopBarMode.Accept;
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
//        mAcceptMode = MuPDFActivity.AcceptMode.StrikeOut;
//        mDocView.setMode(MuPDFReaderView.Mode.Selecting);
//        mAnnotTypeText.setText(R.string.strike_out);
//        showInfo(getString(R.string.select_text));
    }

    public void OnInkButtonClick(View v) {
//        mTopBarMode = MuPDFActivity.TopBarMode.Accept;
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
//        mAcceptMode = MuPDFActivity.AcceptMode.Ink;
//        mDocView.setMode(MuPDFReaderView.Mode.Drawing);
//        mAnnotTypeText.setText(R.string.ink);
//        showInfo(getString(R.string.draw_annotation));
    }

    public void OnCancelAcceptButtonClick(View v) {
//        MuPDFView pageView = (MuPDFView) mDocView.getDisplayedView();
//        if (pageView != null) {
//            pageView.deselectText();
//            pageView.cancelDraw();
//        }
//        mDocView.setMode(MuPDFReaderView.Mode.Viewing);
//        switch (mAcceptMode) {
//            case CopyText:
//                mTopBarMode = MuPDFActivity.TopBarMode.More;
//                break;
//            default:
//                mTopBarMode = MuPDFActivity.TopBarMode.Annot;
//                break;
//        }
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
    }

    public void OnAcceptButtonClick(View v) {
//        MuPDFView pageView = (MuPDFView) mDocView.getDisplayedView();
//        boolean success = false;
//        switch (mAcceptMode) {
//            case CopyText:
//                if (pageView != null)
//                    success = pageView.copySelection();
//                mTopBarMode = MuPDFActivity.TopBarMode.More;
//                showInfo(success?getString(R.string.copied_to_clipboard):getString(R.string.no_text_selected));
//                break;
//
//            case Highlight:
//                if (pageView != null)
//                    success = pageView.markupSelection(Annotation.HIGHLIGHT);
//                mTopBarMode = MuPDFActivity.TopBarMode.Annot;
//                if (!success)
//                    showInfo(getString(R.string.no_text_selected));
//                break;
//
//            case Underline:
//                if (pageView != null)
//                    success = pageView.markupSelection(Annotation.UNDERLINE);
//                mTopBarMode = MuPDFActivity.TopBarMode.Annot;
//                if (!success)
//                    showInfo(getString(R.string.no_text_selected));
//                break;
//
//            case StrikeOut:
//                if (pageView != null)
//                    success = pageView.markupSelection(Annotation.STRIKEOUT);
//                mTopBarMode = MuPDFActivity.TopBarMode.Annot;
//                if (!success)
//                    showInfo(getString(R.string.no_text_selected));
//                break;
//
//            case Ink:
//                if (pageView != null)
//                    success = pageView.saveDraw();
//                mTopBarMode = MuPDFActivity.TopBarMode.Annot;
//                if (!success)
//                    showInfo(getString(R.string.nothing_to_save));
//                break;
//        }
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
//        mDocView.setMode(MuPDFReaderView.Mode.Viewing);
    }

    public void OnCancelSearchButtonClick(View v) {
//        searchModeOff();
    }

    public void OnDeleteButtonClick(View v) {
//        MuPDFView pageView = (MuPDFView) mDocView.getDisplayedView();
//        if (pageView != null)
//            pageView.deleteSelectedAnnotation();
//        mTopBarMode = MuPDFActivity.TopBarMode.Annot;
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
    }

    public void OnCancelDeleteButtonClick(View v) {
//        MuPDFView pageView = (MuPDFView) mDocView.getDisplayedView();
//        if (pageView != null)
//            pageView.deselectAnnotation();
//        mTopBarMode = MuPDFActivity.TopBarMode.Annot;
//        mTopBarSwitcher.setDisplayedChild(mTopBarMode.ordinal());
    }

    public void OnProofButtonClick(final View v) {
        //  set up the menu or resolutions.
//        final PopupMenu popup = new PopupMenu(this, v);
//        popup.getMenu().add(0, 1, 0, "Select a resolution:");
//        popup.getMenu().add(0, 72, 0, "72");
//        popup.getMenu().add(0, 96, 0, "96");
//        popup.getMenu().add(0, 150, 0, "150");
//        popup.getMenu().add(0, 300, 0, "300");
//        popup.getMenu().add(0, 600, 0, "600");
//        popup.getMenu().add(0, 1200, 0, "1200");
//        popup.getMenu().add(0, 2400, 0, "2400");
//
//        //  prevent the first item from being dismissed.
//        //  is there not a better way to do this?  It requires minimum API 14
//        MenuItem item = popup.getMenu().getItem(0);
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
//        item.setActionView(new View(v.getContext()));
//        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item) {
//                return false;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item) {
//                return false;
//            }
//        });
//
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                int id = item.getItemId();
//                if (id != 1) {
//                    //  it's a resolution.  The id is also the resolution value
//                    proofWithResolution(id);
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        popup.show();
    }


}
