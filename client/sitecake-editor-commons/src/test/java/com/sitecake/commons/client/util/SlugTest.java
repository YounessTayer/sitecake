package com.sitecake.commons.client.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class SlugTest {

	@Test
	public void test() {
		assertEquals("123-ab-ca-aaaaaaaaccccccddeeekkk", Slug.get("123---     Ab/cà  áäâãåāăąćĉċčçÇďđèéëkkk"));
	}

}
