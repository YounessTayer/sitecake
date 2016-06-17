package com.sitecake.contentmanager.client.item.photoset;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.sitecake.commons.client.util.DomSelector;
import com.sitecake.commons.client.util.DomUtil;
import com.sitecake.contentmanager.client.GinInjector;
import com.sitecake.contentmanager.client.item.ContentItem;
import com.sitecake.contentmanager.client.item.image.ImageObject;

public class PhotoSetItem extends ContentItem {
	public static final String DISCRIMINATOR = "sc-photoset";
	
	public static final String CONTENT_TYPE_NAME = "PHOTOSET";
	
	interface PhotoSetItemUiBinder extends UiBinder<Element, PhotoSetItem> {}
	
	private static PhotoSetItemUiBinder uiBinder = GWT.create(PhotoSetItemUiBinder.class);
	
	interface CssStyle extends CssResource {
		
	}
		
	@UiField
	CssStyle cssStyle;
	
	@UiField
	Element setContainer;
	
	//private EventBus eventBus = GinInjector.instance.getEventBus();
	
	//private Messages messages = GinInjector.instance.getLocaleProxy().messages();
	
	private DomSelector domSelector = GinInjector.instance.getDomSelector();
	
	private List<ImageObject> images;
	
	@Override
	public String getContentTypeName() {
		return CONTENT_TYPE_NAME;
	}

	@Override
	public String getItemSelector() {
		return "div." + DISCRIMINATOR;
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public boolean isResizable() {
		return false;
	}
	
	@Override
	public ContentItem cloneItem() {
		PhotoSetItem clone = new PhotoSetItem();
		super.cloneItem(clone);
		clone.init(this.images, null);		
		return clone;
	}

	@Override
	public String getHtml() {
		return "<div class=\"" + DISCRIMINATOR + "\">" + getPhotoSetCode() + "</div>";
	}
	
	private String getPhotoSetCode() {
		double cntWidth = 680;
		if (container != null) {
			cntWidth = DomUtil.getElementInnerWidth(container.getElement());
		}
		
		String code = "<div>";
		int idx = 0;
		for (Integer cols : rows(images.size())) {
			List<ImageObject> imgs = new ArrayList<ImageObject>(cols);
			double minRatio = Double.MAX_VALUE;
			double sumRatio = 0;
			for (int i = 0; i < cols; i++, idx++) {
				ImageObject img = images.get(idx);
				imgs.add(img);
				if (img.getRatio() < minRatio) {
					minRatio = img.getRatio();
				}
				sumRatio += img.getRatio();
			}
			sumRatio = sumRatio / minRatio;
			
			code += "<div>";
			for (ImageObject img : imgs) {
				double width = (100.0 / sumRatio) * (img.getRatio() / minRatio);
				code += "<figure style=\"margin:0;display:inline-block;width:" + width + "%\"><a href=\"" + img.getLargestSrc() + "\">" +
						"<img width=\"" + 100 + "%\" src=\"" + img.getSrc(cntWidth) + "\" srcset=\"" + img.getSrcset() + "\"/>" +
						"</a></figure>";
			}
			code += "</div>";
		}
		code += "</div>";
		return code;
	}
	
	//Google+ album URL schema
	//private RegExp gplusUrlSchema = RegExp.compile("https?:\\/\\/plus\\.google\\.com\\/photos\\/[0-9a-z]+\\/albums\\/[0-9a-z]+");
	
	// uniq(matches[][1]) 
	//private RegExp gplusLinksRe = RegExp.compile("\\[\"(https:\\/\\/[^\\.]+\\.googleusercontent\\.com\\/[^\"]+)\\\",\\d+,\\d+\\]", "gmi");
	
	// DropBox album URL schema
	//private RegExp dropboxUrlSchema = RegExp.compile("https?:\\/\\/www\\.dropbox\\.com\\/sc\\/[^\\s]+");

	public static boolean testText(String text) {
		//return (testRe.exec(text) != null);
		return false;
	}
	
	public static PhotoSetItem create(String text) {
		PhotoSetItem item = new PhotoSetItem();
		//String tweetId = testRe.exec(text).getGroup(1);
		//item.init(tweetId, null);
		return item;
	}
	
	public static PhotoSetItem create(List<ImageObject> images) {
		PhotoSetItem item = new PhotoSetItem();
		item.init(images, null);
		return item;
	}
	
	public static PhotoSetItem create(Element element) {
		PhotoSetItem item = new PhotoSetItem();
		item.init(element);
		return item;
	}
	
	public void init(Element origElement) {
		List<ImageObject> images = new ArrayList<ImageObject>();
		JsArray<Element> items = domSelector.select("img", origElement);
		for (int i = 0; i < items.length(); i++) {
			ImageElement imgElement = (ImageElement)items.get(i); 
			double widthPx = imgElement.getWidth();
			double heightPx = imgElement.getHeight();
			ImageObject img = ImageObject.create(imgElement.getSrc(), imgElement.getAttribute("srcset"), 
					100, widthPx, heightPx);
			images.add(img);
		}
		init(images, origElement);
	}
	
	public void init(List<ImageObject> images, Element origElement) {
		Element element = uiBinder.createAndBindUi(this);
		if ( origElement != null ) {
			DomUtil.replaceElement(origElement, element);
		}
		setElement(element);
		
		this.images = images;
		setContainer.setInnerHTML(getPhotoSetCode());
	}
	
	private List<Integer> rows(int numElements) {
		List<Integer> rows = new ArrayList<Integer>();
		int i = 0, rest, rowIdx = 0, rowCnt;
		while (i < numElements) {
			rest = numElements - i;
			
			if (rowIdx % 2 == 0) {
				rowCnt = Math.min(2, rest);
				if ((rest - rowCnt) == 1) {
					rowCnt = 3;
				}
			} else {
				rowCnt = Math.min(3, rest);
				if ((rest - rowCnt) == 1) {
					rowCnt = 2;
				}
			}
			i += rowCnt;
			rowIdx += 1;
			rows.add(rowCnt);
		}
		return rows;
	}
}
