package com.sitecake.commons.client.util;

import java.util.Date;

import com.google.gwt.i18n.client.NumberFormat;

public class Guid {
	private static long count = 1L;
	private static NumberFormat numberFormat = NumberFormat.getFormat("############");
	
	public static String get() {
		
		String guid = numberFormat.format(new Date().getTime());

		for ( int i = 0; i < 5; i++ ) {
			guid += numberFormat.format( Math.floor(Math.random() * 65535) );
		}

		return guid + numberFormat.format(count++);
	}
}
