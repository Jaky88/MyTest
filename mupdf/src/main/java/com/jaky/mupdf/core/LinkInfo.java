package com.jaky.mupdf.core;

import android.graphics.RectF;

import com.jaky.mupdf.data.LinkInfoVisitor;

public class LinkInfo {
	final public RectF rect;

	public LinkInfo(float l, float t, float r, float b) {
		rect = new RectF(l, t, r, b);
	}

	public void acceptVisitor(LinkInfoVisitor visitor) {
	}
}
