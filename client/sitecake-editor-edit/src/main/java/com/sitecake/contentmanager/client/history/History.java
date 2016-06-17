package com.sitecake.contentmanager.client.history;

public interface History<T> {
	public void put(T item);
	public T undo();
	public T redo();
	public T get();
	public void clear();
	public boolean isAtEnd();
	public boolean isAtBegining();
}
