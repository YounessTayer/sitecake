package com.sitecake.contentmanager.client.history;

public class SimpleTransformation<T> implements Transformation {
	
	public enum Type {
		MOVE,
		ADD,
		DELETE,
		EDIT
	}
	
	private T oldItem;
	
	private Position oldPosition;
	
	private T newItem;
	
	private Position newPosition;
	
	
	/**
	 * @return the oldItem
	 */
	public T getOldItem() {
		return oldItem;
	}

	/**
	 * @param oldItem the oldItem to set
	 */
	public void setOldItem(T oldItem) {
		this.oldItem = oldItem;
	}

	/**
	 * @return the oldPosition
	 */
	public Position getOldPosition() {
		return oldPosition;
	}

	/**
	 * @param oldPosition the oldPosition to set
	 */
	public void setOldPosition(Position oldPosition) {
		this.oldPosition = oldPosition;
	}

	/**
	 * @return the newItem
	 */
	public T getNewItem() {
		return newItem;
	}

	/**
	 * @param newItem the newItem to set
	 */
	public void setNewItem(T newItem) {
		this.newItem = newItem;
	}

	/**
	 * @return the newPosition
	 */
	public Position getNewPosition() {
		return newPosition;
	}

	/**
	 * @param newPosition the newPosition to set
	 */
	public void setNewPosition(Position newPosition) {
		this.newPosition = newPosition;
	}

	public SimpleTransformation(T oldItem, Position oldPosition, T newItem, Position newPosition) {
		if ((oldItem == null && newItem == null) ||
				(oldItem != null && oldPosition == null) ||
				(newItem != null && newPosition == null)) {
			throw new IllegalArgumentException();
		};
		
		this.oldItem = oldItem;
		this.oldPosition = oldPosition;
		this.newItem = newItem;
		this.newPosition = newPosition;
	}
	
	public SimpleTransformation<T> getInverted() {
		return new SimpleTransformation<T>(newItem, newPosition, oldItem, oldPosition);
	}
	
	public Type getType() {
		Type type;
		
		if ( oldItem == null && newItem != null ) {
			type = Type.ADD;
		}
		else if (oldItem != null && newItem == null) {
			type = Type.DELETE;
		}
		else if ( newPosition != null && newPosition.equals(oldPosition) ){
			type = Type.EDIT;
		} else {
			type = Type.MOVE;
		}
		
		return type;
	}
}
