package com.sitecake.commons.client.util;

/**
 * Text to slug convertor.
 */
public class Slug {
	
	public static String get(String text) {
		String in = text.trim().toLowerCase();
		in = in.replaceAll("[·\\/_,:;\\s]", "-");
		//in = JavaScriptRegExp.replace(in, "-+", "g", "-");
		in = in.replaceAll("-+", "-");
		// remove accents, swap ñ for n, etc
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < in.length(); i++) {
			char ch = in.charAt(i);
			sb.append(( ch >= 'À') ? mapChar(ch) : ch); 
		}
		return sb.toString();
	}

	// (Basic Latin, Latin-1 Supplement, Latin Extended-A)
	private static final String from = "àáäâãåāăąćĉċčçďđèéëêēĕėęěĝğġģìíïîĩīĭįıĵĥħķĺļľŀłńñņňŉòóöôõøōŏőŕŗřśŝşšţťŧùúüûũūŭůűųŵýÿŷźżž";
	private static final String to =   "aaaaaaaaacccccddeeeeeeeeeggggiiiiiiiiijhhklllllnnnnnooooooooorrrsssstttuuuuuuuuuuwyyyzzz";

	private static char mapChar(char in) {
		for (int i = 0; i < from.length(); i++) {
			if (in == from.charAt(i)) {
				return to.charAt(i);
			}
		}
		return in;
	}
}
