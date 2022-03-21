package com.avereon.marea;

import javafx.scene.paint.Paint;

public interface Renderer2d {

	void setDpi( double dpiX, double dpiY );

	void setViewpoint( double x, double y );

	void setOutputScale( double scaleX, double scaleY );

	void drawHRule( double position, Paint paint, double width );

	void drawVRule( double position, Paint paint, double width );

	void draw( Shape2d shape, Paint paint, double width );

	void fill( Shape2d shape, Paint paint );

}
