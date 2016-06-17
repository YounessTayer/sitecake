package com.sitecake.commons.client.util;

import org.junit.Test;

public class UrlBuilderTest {

	@Test
	public void testUrlBuilderOk() {
		new UrlBuilder("http://localhost/sitecake-server-php/services.php?controller=session");
	}
	
}
