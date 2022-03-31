package com.avereon.marea;

public interface Renderer2d extends Renderer {

	void draw( Shape2d shape, Pen pen );

	void fill( Shape2d shape, Pen pen );

}
