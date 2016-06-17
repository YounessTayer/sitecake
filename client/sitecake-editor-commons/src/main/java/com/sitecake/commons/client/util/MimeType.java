package com.sitecake.commons.client.util;

import java.util.HashMap;
import java.util.Map;

//TODO: implement with positioned list lookup to gain performance
public enum MimeType {
	APPLICATION_MSWORD("application/msword"),
	APPLICATION_PDF("application/pdf"),
	APPLICATION_PGP_SIGNATURE("application/pgp-signature,pgp,"),
	APPLICATION_POSTSCRIPT("application/postscript"),
	APPLICATION_RTF("application/rtf"),
	APPLICATION_VND_MS_EXCEL("application/vnd.ms-excel"),
	APPLICATION_VND_MS_POWERPOINT("application/vnd.ms-powerpoint"),
	APPLICATION_ZIP("application/zip"),
	APPLICATION_X_SHOCKWAVE_FLASH("application/x-shockwave-flash"),
	APPLICATION_VND_OPENXMLFORMAT("application/vnd.openxmlformats"),
	APPLICATION_OCTET_STREAM("application/octet-stream"),		

	TEXT_HTML("text/html"),
	TEXT_RTF("text/rtf"),
	TEXT_PLAIN("text/plain"),

	IMAGE_BMP("image/bmp"),
	IMAGE_GIF("image/gif"),
	IMAGE_JPEG("image/jpeg"),
	IMAGE_PNG("image/png"),
	IMAGE_SVG_XML("image/svg+xml"),
	IMAGE_TIFF("image/tiff"),
	
	VIDEO_MPEG("video/mpeg"),
	VIDEO_QUICKTIME("video/quicktime"),
	VIDEO_X_FLV("video/x-flv"),
	VIDEO_VND_RN_REALVIDEO("video/vnd.rn-realvideo"),
	
	AUDIO_MPEG("audio/mpeg"),
	AUDIO_X_WAV("audio/x-wav"),
	
	GENERIC("");
	
	private static Map<String, MimeType> mimeLookup;
	
	private String value;
	
	private MimeType(String value) {
		this.value = value;
	}

	public static MimeType lookup(String fileExt) {
		if ( mimeLookup == null ) {
			initLookup();
		}
		MimeType mimeType = mimeLookup.get(fileExt.toLowerCase().trim());
		return ( mimeType != null ) ? mimeType : GENERIC;
	}
	
	public static String plainValue(String mimeType) {
		return mimeType.toLowerCase().replaceAll("[^a-z]", "_");
	}
	
	public String toString() {
		return value;
	}

	private static void initLookup() {
		mimeLookup = new HashMap<String, MimeType>();
		
		mimeLookup.put("doc", APPLICATION_MSWORD);
		mimeLookup.put("dot", APPLICATION_MSWORD);
		
		mimeLookup.put("pdf", APPLICATION_PDF);
		
		mimeLookup.put("pgp", APPLICATION_PGP_SIGNATURE);
		
		mimeLookup.put("ps", APPLICATION_POSTSCRIPT);
		mimeLookup.put("ai", APPLICATION_POSTSCRIPT);
		mimeLookup.put("eps", APPLICATION_POSTSCRIPT);
		
		mimeLookup.put("rtf", APPLICATION_RTF);
		
		mimeLookup.put("xls", APPLICATION_VND_MS_EXCEL);
		mimeLookup.put("xlb", APPLICATION_VND_MS_EXCEL);
		
		mimeLookup.put("ppt", APPLICATION_VND_MS_POWERPOINT);
		mimeLookup.put("pps", APPLICATION_VND_MS_POWERPOINT);
		mimeLookup.put("pot", APPLICATION_VND_MS_POWERPOINT);

		mimeLookup.put("zip", APPLICATION_ZIP);

		mimeLookup.put("swf", APPLICATION_X_SHOCKWAVE_FLASH);
		mimeLookup.put("swfl", APPLICATION_X_SHOCKWAVE_FLASH);

		mimeLookup.put("docx", APPLICATION_VND_OPENXMLFORMAT);
		mimeLookup.put("pptx", APPLICATION_X_SHOCKWAVE_FLASH);
		mimeLookup.put("xlsx", APPLICATION_X_SHOCKWAVE_FLASH);

		mimeLookup.put("exe", APPLICATION_OCTET_STREAM);

		mimeLookup.put("bmp", IMAGE_BMP);
		
		mimeLookup.put("gif", IMAGE_GIF);
		
		mimeLookup.put("jpeg", IMAGE_JPEG);
		mimeLookup.put("jpg", IMAGE_JPEG);
		mimeLookup.put("jpe", IMAGE_JPEG);
		
		mimeLookup.put("png", IMAGE_PNG);
		
		mimeLookup.put("svg", IMAGE_SVG_XML);
		mimeLookup.put("svgz", IMAGE_SVG_XML);
		
		mimeLookup.put("tiff", IMAGE_TIFF);
		mimeLookup.put("tif", IMAGE_TIFF);
		
		mimeLookup.put("asc", TEXT_PLAIN);
		mimeLookup.put("txt", TEXT_PLAIN);
		mimeLookup.put("text", TEXT_PLAIN);
		mimeLookup.put("diff", TEXT_PLAIN);
		mimeLookup.put("log", TEXT_PLAIN);
		
		mimeLookup.put("htm", TEXT_HTML);
		mimeLookup.put("html", TEXT_HTML);
		mimeLookup.put("xhtml", TEXT_HTML);
		
		mimeLookup.put("rtf", TEXT_RTF);
		
		mimeLookup.put("mpeg", VIDEO_MPEG);
		mimeLookup.put("mpg", VIDEO_MPEG);
		mimeLookup.put("mpe", VIDEO_MPEG);
		
		mimeLookup.put("qt", VIDEO_QUICKTIME);
		mimeLookup.put("mov", VIDEO_QUICKTIME);
		
		mimeLookup.put("flv", VIDEO_X_FLV);
		
		mimeLookup.put("rv", VIDEO_VND_RN_REALVIDEO);
		
		mimeLookup.put("mpga", AUDIO_MPEG);
		mimeLookup.put("mpega", AUDIO_MPEG);
		mimeLookup.put("mp2", AUDIO_MPEG);
		mimeLookup.put("mp3", AUDIO_MPEG);
		mimeLookup.put("wav", AUDIO_X_WAV);		
	}
}
