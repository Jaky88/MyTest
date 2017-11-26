package com.jaky.mupdf.data;

public abstract class LinkInfoVisitor {
	public abstract void visitInternal(LinkInfoInternal li);
	public abstract void visitExternal(LinkInfoExternal li);
	public abstract void visitRemote(LinkInfoRemote li);
}
