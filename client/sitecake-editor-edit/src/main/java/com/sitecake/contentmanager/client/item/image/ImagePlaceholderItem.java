package com.sitecake.contentmanager.client.item.image;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.sitecake.commons.client.config.Globals;
import com.sitecake.commons.client.util.BasicServiceResponse;
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.commons.client.util.UrlBuilder;
import com.sitecake.contentmanager.client.EventBus;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent;
import com.sitecake.contentmanager.client.event.ErrorNotificationEvent.Level;
import com.sitecake.contentmanager.client.event.UploadEvent;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.toolbar.ContentItemCreator;

public class ImagePlaceholderItem extends ContentItem {
	public static final String CONTENT_TYPE_NAME = "IMAGEPLACEHOLDER";
	
	interface ImagePlaceholderItemUiBinder extends UiBinder<Element, ImagePlaceholderItem> {}
	
	interface CssStyle extends CssResource {
	}
	
	private static ImagePlaceholderItemUiBinder uiBinder = GWT.create(ImagePlaceholderItemUiBinder.class);
	
	private EventBus eventBus = GinInjector.instance.getEventBus();
	
	@UiField
	Element imgContainer;
	
	public static ImagePlaceholderItem create(String url) {
		ImagePlaceholderItem item = GWT.create(ImagePlaceholderItem.class);
		item.init(url);
		return item;
	}
	
	private void init(String url) {
		Element newElement = uiBinder.createAndBindUi(this);
		setElement(newElement);
		imgContainer.setInnerHTML("<img width=\"100%\" src=\"" + url + "\">");
	}
	
	public void upload(String url) {
		uploadExternal(url);
	}
	
	@Override
	public String getContentTypeName() {
		return CONTENT_TYPE_NAME;
	}

	@Override
	public String getItemSelector() {
		return "imageplaceholder";
	}

	@Override
	public ContentItem cloneItem() {
		return ImagePlaceholderItem.create("");
	}

	@Override
	public String getHtml() {
		return "";
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public boolean isResizable() {
		return false;
	}
	
	private void uploadExternal(String url) {
		UrlBuilder urlBuilder = new UrlBuilder(Globals.get().getServiceUrl());
		urlBuilder.setParameter("service", "_image");
		urlBuilder.setParameter("action", "uploadExternal");
		
		RequestBuilder request = new RequestBuilder(RequestBuilder.POST, urlBuilder.buildString());		
		request.setHeader("Content-Type", "application/x-www-form-urlencoded");		
		request.setCallback(new RequestCallback() {
			
			@Override
			public void onResponseReceived(Request request, Response response) {				
				onUploadExternalResponse(response);			
			}						
			
			@Override
			public void onError(Request request, Throwable exception) {				
				onUploadExternalError(exception.getMessage());			
			}		
		});
		
		StringBuilder builder = new StringBuilder();
		builder.append("src=" + URL.encodeQueryString(url));	
		request.setRequestData(builder.toString());
		
		try {
			request.send();
		} catch (RequestException e) {
			onUploadExternalError(e.getMessage());
		}
	}
	
	private void onUploadExternalResponse(Response response) {
		if ( response.getStatusCode() == 200 ) {
			BasicServiceResponse trResponse = BasicServiceResponse.get(response.getText()).cast();
			if ( trResponse.isSuccess() ) {
				List<ContentItemCreator> contentItemCreators = new ArrayList<ContentItemCreator>();
				UploadEvent event = new UploadEvent(this, contentItemCreators);
				
				final ImageObject image = ImageObject.create(trResponse);
				final double cntWidth = DomUtil.getElementInnerWidth(container.getElement());	
				
				ContentItemCreator imageItemCreator = new ContentItemCreator() {
					
					public ContentItem create() {
						ImageItem item = ImageItem.create(image, cntWidth);
						return item;
					}
				};
				
				contentItemCreators.add(imageItemCreator);
				eventBus.fireEventDeferred(event);
			} else {
				eventBus.fireEventDeferred(new ErrorNotificationEvent(Level.ERROR, 
						trResponse.getErrorMessage(), response.getText()));				
			}
			
		} else {
			eventBus.fireEventDeferred(new ErrorNotificationEvent(Level.ERROR, response.getStatusText()));			
		}
	}
	
	private void onUploadExternalError(String message) {
		eventBus.fireEventDeferred(new ErrorNotificationEvent(Level.ERROR, message));
	}	

}
