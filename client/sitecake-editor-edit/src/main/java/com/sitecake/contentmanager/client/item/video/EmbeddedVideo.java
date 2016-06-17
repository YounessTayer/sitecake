package com.sitecake.contentmanager.client.item.video;

public class EmbeddedVideo {
	public enum VideoType {
		YOUTUBE,
		VIMEO
	}
	
	public VideoType type;
	public double ratio;
	public double width;
	public double height;
	public String publicCode;
	public String editCode;
	
	public EmbeddedVideo clone() {
		EmbeddedVideo clone = new EmbeddedVideo();
		clone.type = this.type;
		clone.ratio = this.ratio;
		clone.width = this.width;
		clone.height = this.height;
		clone.publicCode = this.publicCode;
		clone.editCode = this.editCode;
		
		return clone;
	}
}
