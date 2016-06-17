package com.sitecake.contentmanager.client;

import java.util.List;

public interface ContentStyleRegistry {
	public List<String> get(String containerName, String itemSelector);
}
