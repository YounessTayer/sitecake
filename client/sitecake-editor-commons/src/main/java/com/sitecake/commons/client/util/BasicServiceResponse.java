package com.sitecake.commons.client.util;

public class BasicServiceResponse extends JsonResponse {

	public static final int SUCCESS = 0;
	public static final int ERROR = -1;

	protected BasicServiceResponse() {
		super();
	}

	public final boolean isSuccess() {
		return getStatus() == SUCCESS;
	}
	
	public final native int getStatus()/*-{
		return ( this.status != undefined ) ? this.status : @com.sitecake.commons.client.util.BasicServiceResponse::ERROR;
	}-*/;
	
	public final native String getErrorMessage()/*-{
		return ( this.errorMessage ) ? this.errorMessage : "";
	}-*/;
}
