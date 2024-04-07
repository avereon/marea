package com.avereon.marea;

import com.avereon.marea.geom.Path;
import javafx.scene.paint.Paint;

public interface DirectRenderer2d {

	void setPen( Paint paint, double width, LineCap cap, LineJoin join, double[] dashes, double offset );

	void drawLine( double x1, double y1, double x2, double y2 );

	void drawEllipse( double cx, double cy, double rx, double ry, double rotation );

	void drawArc( double cx, double cy, double rx, double ry, double start, double extent, double rotation  );

	void drawQuad( double x1, double y1, double x2, double y2, double x3, double y3 );

	void drawCubic( double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4 );

	void drawPath( Path.Element[] curve, boolean close );

	void drawText( double x, double y, double height, double rotate, String text );

	void fillPath( Path.Element[] curve, boolean close );

	void fillText( double x, double y, double height, double rotate, String text );

}
