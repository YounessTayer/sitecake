package com.sitecake.contentmanager.client.select;

import com.allen_sauer.gwt.dnd.client.util.Area;
import com.allen_sauer.gwt.dnd.client.util.CoordinateArea;
import com.google.gwt.dom.client.EventTarget;

public class SelectContext {
	
	public enum Phase {
		START,
		DRAG,
		END
	}
	
	private Phase phase = Phase.START;
	
	private EventTarget startTargetElement = null;
	
	private int startX = -1;
	private int startY = -1;
	
	private int endX = -1;
	private int endY = -1;
	
	private int currentX = -1;
	private int currentY = -1;

	public SelectContext() {
	}
	
	public Phase getPhase() {
		return phase;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public EventTarget getStartTargetElement() {
		return startTargetElement;
	}

	public void setStartTargetElement(EventTarget startTargetElement) {
		this.startTargetElement = startTargetElement;
	}

	public int getStartX() {
		return startX;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStartY() {
		return startY;
	}

	public void setStartY(int startY) {
		this.startY = startY;
	}

	public int getEndX() {
		return endX;
	}

	public void setEndX(int endX) {
		this.endX = endX;
	}

	public int getEndY() {
		return endY;
	}

	public void setEndY(int endY) {
		this.endY = endY;
	}

	public int getCurrentX() {
		return currentX;
	}

	public void setCurrentX(int currentX) {
		this.currentX = currentX;
	}

	public int getCurrentY() {
		return currentY;
	}

	public void setCurrentY(int currentY) {
		this.currentY = currentY;
	}

	public Area getCurrentArea() {
		if ( Phase.DRAG.equals(phase) ) {
			int left, right, bottom, top;
			left = ( currentX >= startX ) ? startX : currentX;
			right = ( currentX >= startX ) ? currentX : startX;
			top = ( currentY >= startY ) ? startY : currentY;
			bottom = ( currentY >= startY ) ? currentY : startY;
			return new CoordinateArea(left, top, right, bottom);
		} else {
			return new CoordinateArea(0, 0, 0, 0);
		}
	}
}
