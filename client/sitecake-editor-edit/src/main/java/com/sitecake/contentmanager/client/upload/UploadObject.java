package com.sitecake.contentmanager.client.upload;

import java.util.HashMap;
import java.util.Map;

import com.sitecake.commons.client.util.MimeBase64;
import com.sitecake.commons.client.util.MimeType;
import com.sitecake.contentmanager.client.dom.File;

public class UploadObject {
	
	public enum Status {
		INITALIZED,
		UPLOADED,
		ERROR,
		ABORTED
	}
	
	private Status status;
	
	private File file;
	
	private String fileName;
	
	private String mimeType;
	
	private String errorMessage;
	
	private UploadObjectResponse response;
	
	private Map<String, String> headers;
	
	public Status getStatus() {
		return status;
	}

	void setStatus(Status status) {
		this.status = status;
	}

	public File getFile() {
		return file;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
		headers.put("X-FILENAME", MimeBase64.encode(fileName));
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public UploadObjectResponse getResponse() {
		return response;
	}

	public void setResponse(UploadObjectResponse response) {
		this.response = response;
	}

	public Map<String, String> headers() {
		return headers;
	}
	
	public void setHeader(String name, String value) {
		headers.put(name, value);
	}
	
	public String getHeader(String name) {
		return headers.get(name);
	}
	
	public UploadObject(File file) {
		headers = new HashMap<String, String>();
		this.file = file;

		status = Status.INITALIZED;
		fileName = file.name();
		headers.put("X-FILENAME", MimeBase64.encode(fileName));
		
		mimeType = file.mediaType();
		if ( mimeType == null || "".equals(mimeType) ) {
			String fileExt = fileName.substring(fileName.lastIndexOf('.') + 1);
			mimeType = MimeType.lookup(fileExt).toString();
		}
	}
	
}
