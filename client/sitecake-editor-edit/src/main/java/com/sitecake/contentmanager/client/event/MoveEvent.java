package com.sitecake.contentmanager.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class MoveEvent extends GwtEvent<MoveHandler> {

	private static final Type<MoveHandler> TYPE = new Type<MoveHandler>();

	public static Type<MoveHandler> getType() {
		return TYPE;
	}
	
	public enum Direction {
		LEFT,
		RIGHT,
		UP,
		DOWN
	}
	
	private Direction direction;
	
	public Direction getDirection() {
		return direction;
	}

	public MoveEvent(Direction direction) {
		this.direction = direction;
	}
	
	@Override
	public final Type<MoveHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(MoveHandler handler) {
		handler.onMove(this);
	}
}
