package com.sitecake.contentmanager.client.upload;

public interface UploadManager {
	public void upload(UploadBatch uploadBatch);
	public void abort(UploadBatch uploadBatch);
	public void abortAll();
	public boolean isActive();
}
