package com.sitecake.contentmanager.client;

import com.sitecake.commons.compile.RawStringValue;

public interface GlobalConstants {
	
	/** Raw sitecake version number x.y.z */
	@RawStringValue(value = "${pom.parent.version}", develValue = "1.0.0")
	public String rawVersion();
	
	/**
	 * The sitecake editor version.
	 */
	@RawStringValue(value = "SiteCake ver ${pom.parent.version}", develValue = "SiteCake ver 1.0.0")
	public String version();

	/**
	 * The name/ID of the sitecake provider (e.g. sitecake.com)
	 */
	@RawStringValue(value = "${sitecake.build.hostSystem}")
	public String hostSystem();
	
	/**
	 * The URL used to check if an update is available.
	 * The current version and the host system ID will be reported.
	 */
	@RawStringValue(value = "${sitecake.build.updateCheckURL}", develValue = "http://sitecake.com/services/update/check1")
	public String updateCheckUrl();		
}
