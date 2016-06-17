package com.sitecake.contentmanager.client.item.image;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.sitecake.commons.client.util.JSObject;

/**
 * Representation of an image.
 */
public class ImageObject {
	
	public class SrcItem {
		private double width;
		private double height;
		private String url;
		
		public double getWidth() {
			return width;
		}

		public void setWidth(double width) {
			this.width = width;
		}

		public double getHeight() {
			return height;
		}

		public void setHeight(double height) {
			this.height = height;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public SrcItem(double width, double height, String url) {
			super();
			this.width = width;
			this.height = height;
			this.url = url;
		}
	}
	
	private List<SrcItem> items;
	
	private double ratio;
	
	/**
	 * The image width in percent.
	 */
	private double width;

	protected ImageObject() {}
	
	public String getLargestSrc() {
		return items.get(0).url;
	}
	
	public String getSrc(double containerWidth) {
		String bestSrc = items.get(0).url;
		double minDiff = Double.MAX_VALUE;
		double targetWidth = (containerWidth * width) / 100.0;
		for (SrcItem item : items) {
			double diff = Math.abs(targetWidth - item.width);
			if (diff < minDiff) {
				minDiff = diff;
				bestSrc = item.url;
			}
		}
		return bestSrc;
	}
	
	public String getSrcset() {
		String srcset = "";
		for (SrcItem item : items) {
			srcset += "," + item.url + " " + Math.round(item.width) + "w";
		}
		return srcset.substring(1);
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getRatio() {
		return ratio;
	}
	
	public double getWidthPx(double ref) {
		return (width * ref) / 100.0;
	}
	
	public double getHeightPx(double ref) {
		return getWidthPx(ref) / getRatio();
	}
	
	/**
	 * Returns a cloned <code>ImageObject</code> instance.
	 */
	public ImageObject clone() {
		ImageObject clone = new ImageObject();

		clone.width = width;
		clone.ratio = ratio;
		clone.items = new ArrayList<SrcItem>(items);
		
		return clone;
	}
	
	public static ImageObject create(JSObject obj) {
		return create(obj, 0);
	}
	
	public static ImageObject create(JSObject obj, double refWidth) {
		ImageObject img = new ImageObject();
		
		img.items = new ArrayList<ImageObject.SrcItem>();
		JsArray<JSObject> items = obj.getArrayProperty("srcset");
		for (int i = 0; i < items.length(); i++) {
			img.items.add(img.new SrcItem(
					items.get(i).getNumberProperty("width"), 
					items.get(i).getNumberProperty("height"), 
					items.get(i).getProperty("url")));
		}
		if (refWidth > 0) {
			img.width = img.items.get(0).width * 100.0 / refWidth;
			img.width = (img.width > 100.0) ? 100.0 : img.width;
		} else {
			img.width = 100.0;
		}
		img.ratio = obj.getNumberProperty("ratio");
		return img;
	}
	
	public static ImageObject create(String src, double width, double widthPx, double heightPx) {
		ImageObject img = new ImageObject();
		img.width = width;
		img.ratio = widthPx/heightPx;
		img.items = new ArrayList<ImageObject.SrcItem>();
		img.items.add(img.new SrcItem(widthPx, heightPx, src));

		return img;
	}
	
	public static ImageObject create(String src, String srcset, double width, double widthPx, double heightPx) {
		ImageObject img = new ImageObject();
		img.width = width;
		img.ratio = widthPx/heightPx;
		img.items = img.parseSrcset(srcset, img.ratio, widthPx);
		return img;		
	}
	
	private List<SrcItem> parseSrcset(String srcset, double ratio, double origWidth) {
		ArrayList<SrcItem> items = new ArrayList<ImageObject.SrcItem>();
		String[] elements = srcset.split(",");
		for (String element : elements) {
			String[] comps = element.split("\\s");
			String url = comps[0];
			String vwidth = comps[1];
			double width = origWidth;
			if (vwidth.endsWith("w")) {
				width = Double.parseDouble(vwidth.substring(0, -1));
			}
			items.add(new SrcItem(width, width/ratio, url));
		}
		return items;
	}

}
