package com.sitecake.contentmanager.client.upload;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
import com.sitecake.commons.client.config.Globals;
import com.sitecake.commons.client.util.UrlBuilder;
import com.sitecake.contentmanager.client.dom.XMLHttpRequest2;
import com.sitecake.contentmanager.client.dom.XMLHttpRequestProgressEvent;
import com.sitecake.contentmanager.client.dom.XMLHttpRequestProgressHandler;
import com.sitecake.contentmanager.client.dom.XMLHttpRequestUpload;
import com.sitecake.contentmanager.client.upload.UploadBatch.Status;

public class UploadManagerImpl implements UploadManager {

	private LinkedList<UploadBatch> queue;

	private XMLHttpRequest2 xhr;

	private UploadBatch uploadBatch;
	
	private long batchSize;
	
	private long bytesUploaded;
	
	private Iterator<UploadObject> batchIterator;
	
	private UploadObject uploadObject;
	
	private boolean sending;
	
	public UploadManagerImpl() {
		queue = new LinkedList<UploadBatch>();
		sending = false;
	}

	@Override
	public void abort(UploadBatch uploadBatch) {
		uploadBatch.setStatus(Status.ABORTED);
		
		// check if the reqested upload batch is the active one (currently executed)
		if ( this.uploadBatch != null && this.uploadBatch == uploadBatch ) {
			xhr.abort();
			this.uploadBatch = null;
			sending = false;
		} else {
			// if not currently executed, remove it from the queue
			queue.remove(uploadBatch);
		}
		uploadBatch.onUploadAborted();
	}

	@Override
	public void abortAll() {
		// abort current uploadBatch
		if ( uploadBatch != null ) {
			abort(uploadBatch);
		}
		
		// abort all waiting uploadBatches
		for ( UploadBatch queuedBatch : queue ) {
			abort(queuedBatch);
		}
		queue.clear();
	}

	@Override
	public boolean isActive() {
		return !queue.isEmpty() || sending;
	}

	@Override
	public void upload(UploadBatch uploadBatch) {
		queue.addLast(uploadBatch);
		if ( !sending ) {
			uploadNextBatch();
		}
	}
	
	private void uploadNextBatch() {

		try {
			uploadBatch = queue.getFirst();
		} catch (NoSuchElementException e) {
			uploadBatch = null;
		}
		
		if ( uploadBatch != null ) {
			queue.remove(uploadBatch);
			
			uploadBatch.setStatus(Status.STARTED);
			uploadBatch.onUploadStarted();
			bytesUploaded = 0;
			
			// calculate the total batch size in bytes
			batchSize = 0;
			for ( UploadObject uploadObject : uploadBatch.getUploadObjects() ) {
				batchSize += uploadObject.getFile().size();
			}
			
			sending = true;
			batchIterator = uploadBatch.getUploadObjects().iterator();
			uploadNextFile();
		} else {
			sending = false;
		}
	}
	
	private void uploadNextFile() {
		if ( uploadBatch != null && batchIterator.hasNext() ) {
			uploadObject = batchIterator.next();

			UrlBuilder urlBuilder = new UrlBuilder( Globals.get().getServiceUrl());
			if (uploadObject.getHeader("X-IMAGE") != null) {
				urlBuilder.setParameter("service", "_image");
				urlBuilder.setParameter("action", "upload");				
			} else {
				urlBuilder.setParameter("service", "_upload");
				urlBuilder.setParameter("action", "upload");
			}
			
			xhr = XMLHttpRequest2.create2();
			xhr.open("post", urlBuilder.buildString());
			xhr.setRequestHeader("Content-Type", "application/octet-stream");
			xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
				public void onReadyStateChange(XMLHttpRequest xhr) {
					if ( xhr.getReadyState() == XMLHttpRequest.DONE ) {
						if ( UploadBatch.Status.ABORTED.equals(uploadBatch.getStatus()) ) {
							uploadObject.setStatus(UploadObject.Status.ABORTED);
							batchSize -= uploadObject.getFile().size();
							setProgress(0);
						} else if ( xhr.getStatus() < 400 ) {
							setUploadObjectResponse();
							
							if ( UploadObject.Status.UPLOADED.equals(uploadObject.getStatus()) ) {
								bytesUploaded += uploadObject.getFile().size();
								setProgress(0);
							} else {
								setProgress(0);
								batchSize -= uploadObject.getFile().size();
								setProgress(0);
								uploadBatch.onUploadError(uploadObject);
							}
						} else {
							// error condition
							uploadObject.setStatus(UploadObject.Status.ERROR);
							uploadObject.setErrorMessage(xhr.getStatusText());
							batchSize -= uploadObject.getFile().size();
							setProgress(0);
							uploadBatch.onUploadError(uploadObject);
						}
						Scheduler.get().scheduleDeferred(new ScheduledCommand() {
							
							@Override
							public void execute() {
								uploadNextFile();
							}
						});
					}
				}
			});
			
			XMLHttpRequestUpload upload = xhr.getUpload();
			if ( upload != null ) {
				upload.setOnProgressHandler(new XMLHttpRequestProgressHandler() {
					public void onProgress(XMLHttpRequestProgressEvent event) {
						setProgress(event.loaded());
					}
				});
			}

			setUploadObjectRequest();
			
			try {
				xhr.send(uploadObject.getFile());
			} catch (JavaScriptException e) {
				uploadObject.setStatus(UploadObject.Status.ERROR);
				uploadObject.setErrorMessage(e.getMessage());
				batchSize -= uploadObject.getFile().size();
				setProgress(0);
				uploadBatch.onUploadError(uploadObject);
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					
					@Override
					public void execute() {
						uploadNextFile();
					}
				});
			}

		} else {
			if ( uploadBatch != null ) {
				uploadBatch.setStatus(Status.COMPLETED);
				uploadBatch.onUploadCompleted();
			}
			uploadNextBatch();
		}
	}
	
	private void setUploadObjectResponse() {
		String responseText = xhr.getResponseText();
		UploadObjectResponse response = UploadObjectResponse.get(responseText).cast();
		
		if ( !response.isSuccess() ) {
			uploadObject.setStatus(UploadObject.Status.ERROR);
			uploadObject.setErrorMessage(response.getErrorMessage());
			return;
		}
		
		uploadObject.setStatus(UploadObject.Status.UPLOADED);
		uploadObject.setResponse(response);
	}
	
	private void setUploadObjectRequest() {
		Map<String, String> headers = uploadObject.headers();
		for (String name : headers.keySet()) {
			xhr.setRequestHeader(name, headers.get(name));
		}
	}
	
	private void setProgress(int fileBytesUploaded) {
		long totalPercentage = ( batchSize > 0 ) ? (100 * ( bytesUploaded + fileBytesUploaded )) / batchSize : 100;
		uploadBatch.onUploadProgress(totalPercentage);
	}
	
}
