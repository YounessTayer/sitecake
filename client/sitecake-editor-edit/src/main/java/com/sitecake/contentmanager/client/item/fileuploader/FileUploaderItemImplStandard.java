package com.sitecake.contentmanager.client.item.fileuploader;

import com.sitecake.contentmanager.client.item.ContentItem;

public class FileUploaderItemImplStandard extends FileUploaderItem {
	
	@Override
	public ContentItem cloneItem() {
		FileUploaderItemImplStandard clone = new FileUploaderItemImplStandard();
		clone.init(type);
		return super.cloneItem(clone);
	}
}
