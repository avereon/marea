package com.avereon.marea;

import javafx.scene.paint.Paint;

public interface Renderer {

	void resize( double width, double height );

	void setOutputScale( double scaleX, double scaleY );

	void drawHRule( double position, Paint paint, double width );

	void drawVRule( double position, Paint paint, double width );

}
