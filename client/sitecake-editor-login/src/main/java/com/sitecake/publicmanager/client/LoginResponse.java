package com.sitecake.publicmanager.client;

import com.sitecake.commons.client.util.BasicServiceResponse;

public class LoginResponse extends BasicServiceResponse {

	public static final int INVALID_CREDENTIAL = 1;
	public static final int LOCKED = 2;
	
	protected LoginResponse() {}
	
}
