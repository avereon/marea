package com.avereon.marea;

public interface Renderer {

	void setDpi( double dpiX, double dpiY );

	void setZoom( double scaleX, double scaleY );

	void setViewpoint( double x, double y );

	void clear();

	void drawHRule( double position, Pen pen );

	void drawVRule( double position, Pen pen );

	double getWidth();

	double getHeight();

}
