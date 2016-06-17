package com.sitecake.contentmanager.client.item.map;

import java.util.Date;

import com.google.gwt.http.client.URL;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;


public class GoogleEmbeddedMap {

	private String src;
	
	public GoogleEmbeddedMap() {
		super();
	}
	
	public String getCode() {
		return "<iframe src=\"https:" + src + "\" " +
				" width=\"100%\" height=\"100%\" frameborder=\"0\" " + 
				"style=\"position:absolute;top:0;left:0\"></iframe>";
	}

	public GoogleEmbeddedMap cloneMap() {
		GoogleEmbeddedMap clone = new GoogleEmbeddedMap();
		clone.src = src;
		return clone;
	}
		
	public static GoogleEmbeddedMap create(String input) {
		GoogleEmbeddedMap map = null;
		try {
			if ((map = createFromUrl(input)) == null) {
				map = createFromEmbed(input);
			}
		} catch (Throwable e) {
			// do nothing
			map = null;
		}
		return map;
	}

	// \1 - search, \2 - place, \3 - lat, \4 - lng, \5 - zl, \6 - dst, \7 - data, \8 - hl
	private static final RegExp URL_RE = RegExp.compile(".*(?:https?):\\/\\/[^\\.]+\\.google\\.[^\\.]+\\/maps\\/(?:search\\/([^\\/]+)\\/)?(?:place\\/([^\\/]+)\\/)?(?:@(-?[0-9\\.]+),(-?[0-9\\.]+),(?:([0-9]{1,2})z)?(?:([0-9\\.]+)m)?)(?:.*data=([^\\/?#]+))?(?:.*hl=([a-z]+))?", "i");
	
	private static GoogleEmbeddedMap createFromUrl(String url) {
		GoogleEmbeddedMap map = null;
		MatchResult matches = URL_RE.exec(url);
		if (matches != null) {
			map = new GoogleEmbeddedMap();
			map.src = srcFromUrl(url, matches);
		}
		return map;
	}
	
	// \1 - src
	private static final RegExp EMBED_RE = RegExp.compile("^\\s*<iframe.*src\\s*=\\s*\"https?:(\\/\\/www\\.google\\.com\\/maps\\/embed\\?pb=[^\"]+)\".*?<\\/iframe>\\s*$", "i");
	
	private static GoogleEmbeddedMap createFromEmbed(String embed) {
		GoogleEmbeddedMap map = null;
		MatchResult matches = EMBED_RE.exec(embed);
		if (matches != null) {
			map = new GoogleEmbeddedMap();
			map.src = matches.getGroup(1);
		}
		return map;		
	}
	
	private static String srcFromUrl(String url, MatchResult matches) {
		GoogleDataMap map = new GoogleDataMap();

		String search = matches.getGroup(1);
		String place = matches.getGroup(2);
		String lat = matches.getGroup(3);
		String lng = matches.getGroup(4);
		String zl = matches.getGroup(5);
		String dst = matches.getGroup(6);
		String data = matches.getGroup(7);
		String hl = matches.getGroup(8);
		
		GoogleDataMap dataMap = null;
		if (data != null) {
			dataMap = GoogleDataMap.fromString(data);
		}
		
		if (zl != null) {
			map.setProperty(dstFromZl(Integer.valueOf(zl), Double.valueOf(lat)), 1, 1, 1, 1);
		} else if (dst != null) {
			map.setProperty(dstFromM(Double.valueOf(dst)), 1, 1, 1, 1);
		}
		map.setProperty(Double.valueOf(lng), 1, 1, 1, 2);
		map.setProperty(Double.valueOf(lat), 1, 1, 1, 3);

		// satellite view
		if (dataMap != null && (map.new Enum("3")).equals(dataMap.getPropertyEnum(3, 1))) {
			map.setProperty(Float.valueOf(0), 1, 1, 2, 1);
			map.setProperty(Float.valueOf(0), 1, 1, 2, 2);
			map.setProperty(Float.valueOf(0), 1, 1, 2, 3);
			map.setProperty(Integer.valueOf(1024), 1, 1, 3, 1);
			map.setProperty(Integer.valueOf(768), 1, 1, 3, 2);
			map.setProperty(Float.valueOf((float)13.1), 1, 1, 4);
			map.setProperty(map.new Enum("1"), 1, 5);
		}

		if (search != null) {
			map.setProperty(search, 1, 2, 1);
		}
		
		if (place != null) {
			String id;
			if (dataMap != null && (id = dataMap.getPropertyString(4, 3, 1)) != null) {
				map.setProperty(URL.encode(id), 1, 3, 1, 1);
			}
			map.setProperty(place, 1, 3, 1, 2);
		}
		map.setProperty("en", 3, 1);
		if (hl != null && !"en".equalsIgnoreCase(hl)) {
			map.setProperty(hl, 3, 2);
		}
		
		map.setProperty(Long.valueOf(new Date().getTime()), 4);
		
		return "//www.google.com/maps/embed?pb=" + map.toString();
	}
	
	private static double dstFromZl(int zl, double lat) {
		return (Math.cos(Math.toRadians(lat))*522953466.88401723392)/Math.pow(2, zl);
	}
	
	private static double dstFromM(double m) {
		return (m * 4.354653828390115);
	}

	
}
