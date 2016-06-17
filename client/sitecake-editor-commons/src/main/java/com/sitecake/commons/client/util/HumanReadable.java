package com.sitecake.commons.client.util;

import com.google.gwt.i18n.client.NumberFormat;

public class HumanReadable {
	
	private static final String[] suffix = new String[] { "bytes", "KB", "MB", "GB", "TB" };

	public static String bytes(long bytes) {
		double num = bytes;
		int i;
		
		for ( i = 0; i < suffix.length; i++ ) {
			if ( num < 1024.0 ) {
				break;
			}
			num /= 1024.0;
		}
		
		NumberFormat numberFormat = NumberFormat.getFormat("######.#");
		
		return numberFormat.format(num) + suffix[i];
	}
}
