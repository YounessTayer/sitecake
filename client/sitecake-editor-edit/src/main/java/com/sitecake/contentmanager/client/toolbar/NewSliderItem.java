package com.sitecake.contentmanager.client.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.fileuploader.FileUploaderItem;
import com.sitecake.contentmanager.client.item.fileuploader.FileUploaderItem.Type;
import com.sitecake.contentmanager.client.resources.EditorClientBundle;

public class NewSliderItem extends NewItem {

	private static NewSliderItemUiBinder uiBinder = GWT.create(NewSliderItemUiBinder.class);

	interface NewSliderItemUiBinder extends UiBinder<Element, NewSliderItem> {}
	
	public NewSliderItem() {
		setElement(uiBinder.createAndBindUi(this));
		addStyleName(EditorClientBundle.INSTANCE.css().newSliderItem());
		addActiveHandler();
	}

	@Override
	public ContentItemCreator getContentItemCreator() {
		return new ContentItemCreator() {
			public ContentItem create() {
				return FileUploaderItem.create(Type.SLIDER);
			}
		};
	}

	@Override
	public Widget getDragProxy() {
		HTML proxy = new HTML("<span/><div/>");
		proxy.addStyleName(EditorClientBundle.INSTANCE.css().newSliderItemActive());
		proxy.addStyleName(EditorClientBundle.INSTANCE.css().newItemDragProxy());
		return proxy;
	}

	@Override
	public void markAsActive() {
		addStyleName(EditorClientBundle.INSTANCE.css().newSliderItemActive());
	}

	@Override
	public void markAsInactive() {
		removeStyleName(EditorClientBundle.INSTANCE.css().newSliderItemActive());
	}	
	
}
