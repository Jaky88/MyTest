package com.jaky.mupdf.ui.views;

import android.graphics.PointF;
import android.graphics.RectF;

import com.jaky.mupdf.data.Annotation;
import com.jaky.mupdf.data.LinkInfo;
import com.jaky.mupdf.data.ReaderConstants;

public interface MuPDFView {
	void setPage(int page, PointF size);
	void setScale(float scale);
	int getPage();
	void blank(int page);
	@ReaderConstants.Hit String passClickEvent(float x, float y);
	LinkInfo hitLink(float x, float y);
	void selectText(float x0, float y0, float x1, float y1);
	void deselectText();
	boolean copySelection();
	boolean markupSelection(@Annotation.Type int type);
	void deleteSelectedAnnotation();
	void setSearchBoxes(RectF searchBoxes[]);
	void setLinkHighlighting(boolean f);
	void deselectAnnotation();
	void startDraw(float x, float y);
	void continueDraw(float x, float y);
	void cancelDraw();
	boolean saveDraw();
	void setChangeReporter(Runnable reporter);
	void update();
	void updateHq(boolean update);
	void removeHq();
	void releaseResources();
	void releaseBitmaps();
}
