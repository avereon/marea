package com.avereon.marea;

import java.util.Collection;

public interface ShapeRenderer2d extends Renderer {

	void draw( Shape2d shape, Pen pen );

	<T extends Shape2d> void draw( Collection<T> shape, Pen pen );

	void fill( Shape2d shape, Pen pen );

	<T extends Shape2d> void fill( Collection<T> shape, Pen pen );

}
