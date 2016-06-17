package com.sitecake.contentmanager.client.item.slideshow;

public class SlideshowImage {
	
	private String id;
	
	private String url;
	
	private String coverUrl;
	
	private double coverWidth;
	
	private double coverHeight;
	
	private boolean cover;
	
	private String description = "";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCoverUrl() {
		return coverUrl;
	}

	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}

	public double getCoverWidth() {
		return coverWidth;
	}

	public void setCoverWidth(double coverWidth) {
		this.coverWidth = coverWidth;
	}

	public double getCoverHeight() {
		return coverHeight;
	}

	public void setCoverHeight(double coverHeight) {
		this.coverHeight = coverHeight;
	}

	public boolean isCover() {
		return cover;
	}

	public void setCover(boolean cover) {
		this.cover = cover;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
