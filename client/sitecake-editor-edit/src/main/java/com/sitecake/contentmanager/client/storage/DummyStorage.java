package com.sitecake.contentmanager.client.storage;

/**
 * A dummy storage implementation that keeps items in memory.
 */
public class DummyStorage extends AbstractStorage {

	private String value = "";
	
	public DummyStorage() {
		super();
	}

	@Override
	protected String getRawStorage() {
		return value;
	}

	@Override
	protected void setRawStorage(String value) {
		this.value = value;
	}

}
