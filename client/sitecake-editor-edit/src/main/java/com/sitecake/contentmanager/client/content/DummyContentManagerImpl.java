package com.sitecake.contentmanager.client.content;

import java.util.List;
import java.util.Map;

public class DummyContentManagerImpl implements ContentManager {

	@Override
	public void save(Map<String, String> content) {
	}

	@Override
	public void publish(List<String> containers) {
	}

	@Override
	public void abortAll() {
	}

	@Override
	public boolean isActive() {
		return false;
	}

	
}
