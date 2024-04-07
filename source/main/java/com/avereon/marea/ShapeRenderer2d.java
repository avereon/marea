package com.avereon.marea;

import java.util.Collection;

public interface ShapeRenderer2d extends Renderer {

	void draw( Shape2d shape, Pen pen );

	void draw( Collection<? extends Shape2d> shape, Pen pen );

	void fill( Shape2d shape, Pen pen );

	void fill( Collection<? extends Shape2d> shape, Pen pen );

}
