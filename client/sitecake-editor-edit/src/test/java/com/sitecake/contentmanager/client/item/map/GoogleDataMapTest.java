package com.sitecake.contentmanager.client.item.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class GoogleDataMapTest {

	@Test
	public void test_empty() {
		GoogleDataMap map = new GoogleDataMap();
		assertEquals("", map.toString());
	}

	@Test
	public void test_integer() {
		GoogleDataMap map = new GoogleDataMap();
		map.setProperty(1024, 2);
		map.setProperty(768, 1, 1);
		map.setProperty(12, 1, 2);

		assertEquals(Integer.valueOf(1024), map.getPropertyInteger(2));		
		assertEquals(Integer.valueOf(768), map.getPropertyInteger(1, 1));
		assertEquals(Integer.valueOf(12), map.getPropertyInteger(1, 2));
		assertNull(map.getPropertyString(3));
		assertNull(map.getPropertyString(3, 3, 3));
		assertNull(map.getPropertyString(1, 3, 3));
		
		GoogleDataMap submap = map.getPropertyMap(1);
		assertEquals(Integer.valueOf(768), submap.getPropertyInteger(1));
		assertEquals(Integer.valueOf(12), submap.getPropertyInteger(2));
	}
	
	@Test
	public void test_double() {
		GoogleDataMap map = new GoogleDataMap();
		map.setProperty(1024.8, 2);
		map.setProperty(768.2, 1, 1);
		map.setProperty(12.5, 1, 2);

		assertEquals(Double.valueOf(1024.8), map.getPropertyDouble(2));		
		assertEquals(Double.valueOf(768.2), map.getPropertyDouble(1, 1));
		assertEquals(Double.valueOf(12.5), map.getPropertyDouble(1, 2));
	}
	
	@Test
	public void test_toString() {
		GoogleDataMap map;
		
		map = new GoogleDataMap();
		map.setProperty(1024.8, 1, 1, 1);
		map.setProperty(-100.5, 1, 1, 3);
		map.setProperty(768.2, 1, 1, 2);		
		map.setProperty("rs", 3, 1);
		map.setProperty("en", 3, 2);
		
		assertEquals("!1m4!1m3!1d1024.8!2d768.2!3d-100.5!3m2!1srs!2sen", map.toString());
		
		map = new GoogleDataMap();
		map.setProperty(Double.valueOf(1), 1, 1, 1);
		map.setProperty(Double.valueOf(1), 1, 1, 2);
		map.setProperty(Double.valueOf(1), 1, 2, 1);
		map.setProperty(Double.valueOf(1), 1, 3, 1);
		map.setProperty(Double.valueOf(1), 1, 3, 2);
		GoogleDataMap submap = map.getPropertyMap(1, 3);
		
		assertEquals("!1m8!1m2!1d1.0!2d1.0!2m1!1d1.0!3m2!1d1.0!2d1.0", map.toString());
		assertEquals("!1d1.0!2d1.0", submap.toString());
		
		map = new GoogleDataMap();
		map.setProperty(Double.valueOf(598), 1, 1, 1, 1);
		map.setProperty(Double.valueOf(20.4658609), 1, 1, 1, 2);
		map.setProperty(Double.valueOf(44.8116186), 1, 1, 1, 3);
		map.setProperty(Float.valueOf(0), 1, 1, 2, 1);
		map.setProperty(Float.valueOf(0), 1, 1, 2, 2);
		map.setProperty(Float.valueOf(0), 1, 1, 2, 3);
		map.setProperty("en", 3, 1);
		map.setProperty(Long.valueOf(1234), 4);
		assertEquals("!1m9!1m8!1m3!1d598.0!2d20.4658609!3d44.8116186!2m3!1f0.0!2f0.0!3f0.0!3m1!1sen!4v1234", map.toString());
	}
	
	@Test
	public void test_fromString() {
		GoogleDataMap map;
		
		map = GoogleDataMap.fromString("!1d1.0!2f2.0!3i20!4b1!5b0!6sstr!7eenum!8m2!2m1!1st!9v1234");
		
		assertEquals(Double.valueOf(1.0), map.getPropertyDouble(1));
		assertEquals(Float.valueOf(2), map.getPropertyFloat(2));
		assertEquals(Integer.valueOf(20), map.getPropertyInteger(3));
		assertEquals(Boolean.valueOf(true), map.getPropertyBoolean(4));
		assertEquals(Boolean.valueOf(false), map.getPropertyBoolean(5));
		assertEquals("str", map.getPropertyString(6));
		assertEquals(map.new Enum("enum"), map.getPropertyEnum(7));
		assertEquals("t", map.getPropertyString(8, 2, 1));
		assertEquals(Long.valueOf(1234), map.getPropertyLong(9));
		
		map = GoogleDataMap.fromString("!1d1.0!8m5!2m1!1st!3m2!1i1!2i2!2send");
		
		assertEquals(Double.valueOf(1.0), map.getPropertyDouble(1));
		assertEquals("t", map.getPropertyString(8, 2, 1));
		assertEquals(Integer.valueOf(1), map.getPropertyInteger(8, 3, 1));
		assertEquals(Integer.valueOf(2), map.getPropertyInteger(8, 3, 2));
		assertEquals("end", map.getPropertyString(2));
	}
}
