package com.avereon.marea;

public class Circle implements Shape2d, Shape3d{

	@Override
	public ShapeType type() {
		return ShapeType.CIRCLE;
	}

}
