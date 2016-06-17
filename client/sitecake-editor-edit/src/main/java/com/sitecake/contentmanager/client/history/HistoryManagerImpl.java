package com.sitecake.contentmanager.client.history;

import java.util.ArrayList;
import java.util.List;

public class HistoryManagerImpl<T> implements History<T> {

	private List<T> items;
	private int index;
	
	public HistoryManagerImpl() {
		items = new ArrayList<T>();
		index = -1;
	}

	public void put(T item) {
		// if we already at the end, push a new transformation
		if ( index < (items.size() - 1) ) {
			// if we are not at the very end, replace the next one and remove the rest till the end
			List<T> newItems = new ArrayList<T>();
			for (int i = 0; i <= index; i++) {
				newItems.add(items.get(i));
			}
			items = newItems;
		}
		items.add(item);
		index++;
	}

	public T get() {
		return (index == -1) ? null : items.get(index);
	}

	public void clear() {
		items.clear();
		index = -1;
	}

	public T redo() {
		// if we are not at the end, step forward (re-do)
		if ( index < (items.size() - 1) ) {
			// fetch the transformation we have to advanced to
			index++;
		}
		return get();
	}

	public T undo() {
		// if we are not at the begining, step backward (undo)
		T result = get();
		if (index >= 0) {
			index--;
		}
		return result;
	}

	public boolean isAtBegining() {
		return (index < 0);
	}

	public boolean isAtEnd() {
		return (index == items.size() - 1);
	}
}
