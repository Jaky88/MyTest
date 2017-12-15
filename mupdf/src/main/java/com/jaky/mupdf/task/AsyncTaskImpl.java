package com.jaky.mupdf.task;

public interface AsyncTaskImpl<Params, Result>
{
	public Result doInBackground(Params... params);
	public void doCancel();
	public void doCleanup();
}
