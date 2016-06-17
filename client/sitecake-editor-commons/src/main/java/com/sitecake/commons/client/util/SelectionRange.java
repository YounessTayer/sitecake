package com.sitecake.commons.client.util;


public class SelectionRange {
	private SelectionEndPoint start;
	private SelectionEndPoint end;
	
	public SelectionEndPoint getStart() {
		return start;
	}
	
	public void setStart(SelectionEndPoint start) {
		this.start = start;
	}
	
	public SelectionEndPoint getEnd() {
		return end;
	}
	
	public void setEnd(SelectionEndPoint end) {
		this.end = end;
	}

	public SelectionRange(SelectionEndPoint start, SelectionEndPoint end) {
		super();
		this.start = start;
		this.end = end;
	}
	
}
