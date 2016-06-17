package com.sitecake.contentmanager.client.history;

import com.sitecake.contentmanager.client.container.ContentContainer;

public class Position {
	private ContentContainer container;
	private int index;
	
	public Position(ContentContainer container, int index) {
		this.container = container;
		this.index = index;
	}

	/**
	 * @return the container
	 */
	public ContentContainer getContainer() {
		return container;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if((obj == null) || (obj.getClass() != this.getClass()))
			return false;

		Position test = (Position)obj;
		return index == test.index &&
			(container == test.container || (container != null && container.equals(test.container)));
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + index;
		hash = 31 * hash + (null == container ? 0 : container.hashCode());
		return hash;
	}
	
	
}
