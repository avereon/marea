package com.avereon.marea;

import com.avereon.marea.geom.Path;
import javafx.scene.paint.Paint;

import java.util.List;

public interface DirectRenderer2d extends Renderer {

	void setDrawPen( Paint paint, double width, LineCap cap, LineJoin join, double[] dashes, double offset );

	void setFillPen( Paint paint );

	void setDrawPen( Pen pen );

	void setDrawPen( Pen pen, boolean text );

	void drawLine( double x1, double y1, double x2, double y2 );

	void drawEllipse( double cx, double cy, double rx, double ry, double rotate );

	void drawArc( double cx, double cy, double rx, double ry, double rotate, double start, double extent );

	void drawQuad( double x1, double y1, double x2, double y2, double x3, double y3 );

	void drawCubic( double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4 );

	void drawPath( List<Path.Step> path );

	void drawText( double x, double y, double height, double rotate, String text, Font font );

	void fillEllipse( double cx, double cy, double rx, double ry, double rotate );

	@Deprecated
	void fillPath( List<Path.Step> path );

	void startPath();

	void moveTo( double x, double y );

	void lineTo( double x, double y );

	void arcTo( double x, double y, double rx, double ry, double rotate, boolean large, boolean sweep );

	void arcTo( double cx, double cy, double rx, double ry, double start, double extent );

	void pathQuadTo( double x1, double y1, double x2, double y2 );

	void pathCubicTo( double x1, double y1, double x2, double y2, double x3, double y3 );

	void pathClose();

	void drawPath();

	void fillPath();

	void fillText( double x, double y, double height, double rotate, String text, Font font );

	void fillScreenOval( double x, double y, double w, double h );

	void drawScreenOval( double x, double y, double w, double h );

	void fillScreenBox( double x, double y, double w, double h );

	void drawScreenBox( double x, double y, double w, double h );

	void drawScreenHRule( double position );

	void drawScreenVRule( double position );

}
