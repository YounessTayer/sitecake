package com.sitecake.contentmanager.client.item.map;


import java.util.HashMap;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.sitecake.commons.client.util.Arrays;

public class GoogleDataMap {

	public class Enum {
		private String value;

		protected Enum(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Enum))
				return false;
			Enum other = (Enum) obj;

			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return value.toString();
		}	
	}
	
	private HashMap<Integer, Object> list;

	public GoogleDataMap() {
		list = new HashMap<Integer, Object>();
	}
	
	public Double getPropertyDouble(Integer... keys) {
		return (Double)getProperty(keys);
	}

	public Float getPropertyFloat(Integer... keys) {
		return (Float)getProperty(keys);
	}
	
	public Boolean getPropertyBoolean(Integer... keys) {
		return (Boolean)getProperty(keys);
	}
	
	public Integer getPropertyInteger(Integer... keys) {
		return (Integer)getProperty(keys);
	}

	public Long getPropertyLong(Integer... keys) {
		return (Long)getProperty(keys);
	}
	
	public String getPropertyString(Integer... keys) {
		return (String)getProperty(keys);
	}
	
	public Enum getPropertyEnum(Integer... keys) {
		return (Enum)getProperty(keys);
	}
	
	public GoogleDataMap getPropertyMap(Integer... keys) {
		return (GoogleDataMap)getProperty(keys);
	}

	private Object getProperty(Integer... keys) {
		Object node = list.get(keys[0]);
		if (keys.length > 1 && (node instanceof GoogleDataMap)) {
			return ((GoogleDataMap) node).getProperty(Arrays.copyOfRange(keys, 1, keys.length));
		}
		return node;
	}
	
	public void setProperty(Double value, Integer... keys) {
		setProperty((Object)value, keys);
	}

	public void setProperty(Float value, Integer... keys) {
		setProperty((Object)value, keys);
	}
	
	public void setProperty(Integer value, Integer...keys) {
		setProperty((Object)value, keys);
	}

	public void setProperty(Long value, Integer...keys) {
		setProperty((Object)value, keys);
	}
	
	public void setProperty(String value, Integer... keys) {
		setProperty((Object)value, keys);
	}

	public void setProperty(Enum value, Integer... keys) {
		setProperty((Object)value, keys);
	}
	
	public void setProperty(Boolean value, Integer... keys) {
		setProperty((Object)value, keys);
	}
	
	public void setProperty(GoogleDataMap value, Integer... keys) {
		setProperty((Object)value, keys);
	}
	
	private void setProperty(Object value, Integer... keys) {
		if (keys.length > 1) {
			// dive deeper, recursively
			Object citem = list.get(keys[0]);
			if (!(citem instanceof GoogleDataMap)) {
				citem = new GoogleDataMap();
				list.put(keys[0], citem);
			}
			Integer[] subkeys = Arrays.copyOfRange(keys, 1, keys.length);
			((GoogleDataMap) citem).setProperty(value, subkeys);
		} else {
			// store value
			list.put(keys[0], value);
		}
	}
	
	private int count() {
		int cnt = list.size();
		for (Object node : list.values()) {
			if (node instanceof GoogleDataMap) {
				cnt += ((GoogleDataMap)node).count();
			}
		}
		return cnt;
	}
	
	@Override
	public String toString() {
		String out = "";
		for (Integer key : list.keySet()) {
			Object node = list.get(key);
			if (node instanceof GoogleDataMap) {
				GoogleDataMap submap = (GoogleDataMap)node;
				out += "!" + key + "m" + submap.count() + submap.toString();
			} else if (node instanceof Integer) {
				out += "!" + key + "i" + (Integer)node;	
			} else if (node instanceof Long) {
				out += "!" + key + "v" + (Long)node;					
			} else if (node instanceof Double) {
				out += "!" + key + "d" + (Double)node;	
			} else if (node instanceof Float) {
				out += "!" + key + "f" + (Float)node;					
			} else if (node instanceof Boolean) {
				out += "!" + key + "b" + ((Boolean)node ? "1" : "0");				
			} else if (node instanceof String) {
				out += "!" + key + "s" + (String)node;
			} else if (node instanceof Enum) {
				out += "!" + key + "e" + (Enum)node;
			}
		}
		return out;
	}
	
	public static GoogleDataMap fromString(String in) {
		String [] elements = in.split("!");
		return fromStrings(Arrays.copyOfRange(elements, 1, elements.length));
	}
	
	private static GoogleDataMap fromStrings(String [] elements) {
		GoogleDataMap map = new GoogleDataMap();
		RegExp re = RegExp.compile("([0-9]+)(m|s|i|d|b|f|e|v)(.*)");
		for (int i = 0; i < elements.length; i++) {
			String el = elements[i];
			
			MatchResult match = re.exec(el);
			Integer key = Integer.valueOf(match.getGroup(1));
			String type = match.getGroup(2);
			String value = match.getGroup(3);
			
			if (type.equals("m")) {
				int len = Integer.valueOf(value);
				GoogleDataMap submap = GoogleDataMap.fromStrings(Arrays.copyOfRange(elements, i+1, i+len+1));
				map.setProperty(submap, key);
				i += len;
			} else if (type.equals("i")) {
				map.setProperty(Integer.valueOf(value), key);
			} else if (type.equals("d")) {
				map.setProperty(Double.valueOf(value), key);
			} else if (type.equals("f")) {
				map.setProperty(Float.valueOf(value), key);
			} else if (type.equals("v")) {
				map.setProperty(Long.valueOf(value), key);				
			} else if (type.equals("s")) {
				map.setProperty(value, key);
			} else if (type.equals("e")) {
				map.setProperty(map.new Enum(value), key);
			} else if (type.equals("b")) {
				map.setProperty("1".equals(value) ? Boolean.valueOf(true) : Boolean.valueOf(false), key);
			}
		}
		return map;
		
	}
}
