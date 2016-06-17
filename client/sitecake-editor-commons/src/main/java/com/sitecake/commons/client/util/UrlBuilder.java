package com.sitecake.commons.client.util;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

public class UrlBuilder {
	private com.google.gwt.http.client.UrlBuilder internalUrlBuilder;
	
	private RegExp urlParseRegExp = RegExp.compile("^((https?)://)?([a-zA-Z0-9_\\-\\.]+)?(:([0-9]+))?(/[^\\?\\#]*)?(\\#[^\\?]*)?(\\?(.*))?$");
	
	public UrlBuilder(String url) {
		internalUrlBuilder = new com.google.gwt.http.client.UrlBuilder();
		
		MatchResult matches = urlParseRegExp.exec(url);
		
		if ( matches == null ) {
			throw new IllegalArgumentException("Invalid URL given " + url);
		}
		
		String protocol = matches.getGroup(2);
		String host = matches.getGroup(3);
		String port = matches.getGroup(5);
		String path = matches.getGroup(6);
		String hash = matches.getGroup(7);
		String query = matches.getGroup(9);
		
		if ( protocol != null && !"".equals(protocol) ) 
			internalUrlBuilder.setProtocol(protocol);
		else
			internalUrlBuilder.setProtocol("relurl://");
		
		if ( host != null && !"".equals(host) )
			internalUrlBuilder.setHost(host);
		
		if ( port != null && !"".equals(port) )
			internalUrlBuilder.setPort(Integer.valueOf(port));
		
		if ( path != null && !"".equals(path) )
			internalUrlBuilder.setPath(path);
		
		if ( hash != null && !"".equals(hash) )
			internalUrlBuilder.setHash(hash);
		
		if ( query != null && !"".equals(query) ) {
			String [] pairs = query.split("&");
			for ( String pair : pairs ) {
				String [] parts = pair.split("=");
				internalUrlBuilder.setParameter(parts[0], parts[1]);
			}
		}
	}
	
	  /**
	   * Build the URL and return it as an encoded string.
	   * 
	   * @return the encoded URL string
	   */
	  public String buildString() {
		  String url = internalUrlBuilder.buildString().replaceFirst("relurl://", "");
		  return url;
	  }

	  /**
	   * Remove a query parameter from the map.
	   * 
	   * @param name the parameter name
	   */
	  public UrlBuilder removeParameter(String name) {
		internalUrlBuilder.removeParameter(name);
	    return this;
	  }

	  /**
	   * Set the hash portion of the location (ex. myAnchor or #myAnchor).
	   * 
	   * @param hash the hash
	   */
	  public UrlBuilder setHash(String hash) {
		internalUrlBuilder.setHash(hash);
	    return this;
	  }

	  /**
	   * Set the host portion of the location (ex. google.com). You can also specify
	   * the port in this method (ex. localhost:8888).
	   * 
	   * @param host the host
	   */
	  public UrlBuilder setHost(String host) {
		internalUrlBuilder.setHost(host);
	    return this;
	  }

	  /**
	   * <p>
	   * Set a query parameter to a list of values. Each value in the list will be
	   * added as its own key/value pair.
	   * 
	   * <p>
	   * <h3>Example Output</h3>
	   * <code>?mykey=value0&mykey=value1&mykey=value2</code>
	   * </p>
	   * 
	   * @param key the key
	   * @param values the list of values
	   */
	  public UrlBuilder setParameter(String key, String... values) {
		internalUrlBuilder.setParameter(key, values);
	    return this;
	  }

	  /**
	   * Set the path portion of the location (ex. path/to/file.html).
	   * 
	   * @param path the path
	   */
	  public UrlBuilder setPath(String path) {
		internalUrlBuilder.setPath(path);
	    return this;
	  }

	  /**
	   * Set the port to connect to.
	   * 
	   * @param port the port, or {@link #PORT_UNSPECIFIED}
	   */
	  public UrlBuilder setPort(int port) {
		internalUrlBuilder.setPort(port);
	    return this;
	  }

	  /**
	   * Set the protocol portion of the location (ex. http).
	   * 
	   * @param protocol the protocol
	   */
	  public UrlBuilder setProtocol(String protocol) {
		internalUrlBuilder.setProtocol(protocol);
	    return this;
	  }
	
}
