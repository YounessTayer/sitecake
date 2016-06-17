package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface UploadHandler extends EventHandler {
	public void onUpload(UploadEvent event);
}
