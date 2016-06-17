package com.sitecake.contentmanager.client.history;

import java.util.ArrayList;
import java.util.List;

public class ParallelTransformation<T> implements Transformation {
	private List<SimpleTransformation<T>> transformations;

	public ParallelTransformation() {
		super();
		transformations = new ArrayList<SimpleTransformation<T>>();
	}

	public void add(SimpleTransformation<T> transformation) {
		transformations.add(transformation);
	}

	public List<SimpleTransformation<T>> getTransformations() {
		return transformations;
	}
}
