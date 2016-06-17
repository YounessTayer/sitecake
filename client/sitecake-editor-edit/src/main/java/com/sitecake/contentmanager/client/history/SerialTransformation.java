package com.sitecake.contentmanager.client.history;

import java.util.ArrayList;
import java.util.List;

public class SerialTransformation implements Transformation {
	private List<Transformation> transformations;

	public SerialTransformation() {
		super();
		transformations = new ArrayList<Transformation>();
	}

	public void add(Transformation transformation) {
		transformations.add(transformation);
	}

	public List<Transformation> getTransformations() {
		return transformations;
	}
}
