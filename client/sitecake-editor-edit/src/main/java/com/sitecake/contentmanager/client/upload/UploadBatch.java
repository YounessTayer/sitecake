package com.sitecake.contentmanager.client.upload;

import java.util.List;

import com.sitecake.contentmanager.client.GinInjector;

public abstract class UploadBatch {

	public enum Status {
		READY,
		STARTED,
		ABORTED,
		ERROR,
		COMPLETED
	}
	
	private List<UploadObject> uploadObjects;
	
	private UploadManager uploadManager = GinInjector.instance.getUploadManager();
	
	private Status status;
	
	public UploadBatch(List<UploadObject> uploadObjects) {
		this.uploadObjects = uploadObjects;
		this.status = Status.READY;
	}
	
	public void abort() {
		uploadManager.abort(this);
	}

	public List<UploadObject> getUploadObjects() {
		return uploadObjects;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	/**
	 * Called by <code>UploadManager</code> when this batch upload is started.
	 */
	public abstract void onUploadStarted();
	
	public abstract void onUploadCompleted();
	
	public abstract void onUploadAborted();
	
	public abstract void onUploadError(UploadObject uploadObject);
	
	public abstract void onUploadProgress(long percentage);
}
