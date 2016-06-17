package com.sitecake.contentmanager.client.upload;

import com.sitecake.commons.client.util.BasicServiceResponse;

public class UploadObjectResponse extends BasicServiceResponse {

	protected UploadObjectResponse() {}
	
	public final native String getId()/*-{
		return ( this.id ) ? this.id : null;
	}-*/;

	public final native String getUrl()/*-{
		return ( this.url ) ? this.url : null;
	}-*/;
}
