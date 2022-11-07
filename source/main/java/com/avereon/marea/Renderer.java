package com.avereon.marea;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;

public interface Renderer {

	double getWidth();

	void setWidth( double width );

	DoubleProperty widthProperty();

	double getHeight();

	void setHeight( double height );

	DoubleProperty heightProperty();

	LengthUnit getLengthUnit();

	void setLengthUnit( LengthUnit unit );

	ObjectProperty<LengthUnit> lengthUnitProperty();

	Point2D getDpi();

	void setDpi( double dpiX, double dpiY );

	double getDpiX();

	void setDpiX( double dpiX );

	DoubleProperty dpiXProperty();

	double getDpiY();

	void setDpiY( double dpiY );

	DoubleProperty dpiYProperty();

	Point2D getZoom();

	void setZoom( double scaleX, double scaleY );

	double getZoomX();

	void setZoomX( double zoomX );

	DoubleProperty zoomXProperty();

	double getZoomY();

	void setZoomY( double zoomY );

	DoubleProperty zoomYProperty();

	double getZoomFactor();

	void setZoomFactor( double zoomFactor );

	DoubleProperty zoomFactorProperty();

	double getViewpointX();

	void setViewpointX( double viewpointX );

	DoubleProperty viewpointXProperty();

	double getViewpointY();

	void setViewpointY( double viewpointY );

	DoubleProperty viewpointYProperty();

	Point2D getViewpoint();

	void setViewpoint( double x, double y );

	void setZoomAt( double x, double y, double zoomX, double zoomY );

	void clear();

	void drawHRule( double position, Pen pen );

	void drawVRule( double position, Pen pen );

	Point2D localToParent( double x, double y );

	Point2D parentToLocal( double x, double y );

}
