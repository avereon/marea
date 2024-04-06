package com.avereon.marea;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;

/**
 * The interface that all raster renderers must implement.
 */
public interface Renderer {

	/**
	 * Get the width of the renderer in pixels.
	 *
	 * @return The width of the renderer in pixels.
	 */
	double getWidth();

	/**
	 * Set the width of the renderer in pixels.
	 *
	 * @param width The width of the renderer in pixels.
	 */
	void setWidth( double width );

	/**
	 * Get the renderer width property.
	 *
	 * @return The width property.
	 */
	DoubleProperty widthProperty();

	/**
	 * Get the height of the renderer in pixels.
	 *
	 * @return The height of the renderer in pixels.
	 */
	double getHeight();

	/**
	 * Set the height of the renderer in pixels.
	 *
	 * @param height The height of the renderer in pixels.
	 */
	void setHeight( double height );

	/**
	 * Get the renderer height property.
	 *
	 * @return The height property.
	 */
	DoubleProperty heightProperty();

	/**
	 * Get the length unit used by the renderer.
	 *
	 * @return The length unit used by the renderer.
	 */
	RenderUnit getLengthUnit();

	/**
	 * Set the length unit used by the renderer.
	 *
	 * @param unit The length unit used by the renderer.
	 */
	void setLengthUnit( RenderUnit unit );

	/**
	 * Get the length unit property.
	 *
	 * @return The length unit property.
	 */
	ObjectProperty<RenderUnit> lengthUnitProperty();

	/**
	 * Convenience method to get the PPI as a point.
	 *
	 * @return The renderer PPI as a point.
	 */
	default Point2D getPpi() {
		return new Point2D( getPpiX(), getPpiY() );
	}

	/**
	 * Convenience method to set both the x and y PPI properties of the renderer.
	 *
	 * @param ppi The PPI as a point.
	 */
	default void setPpi( Point2D ppi ) {
		setPpi( ppi.getX(), ppi.getY() );
	}

	/**
	 * Convenience method to set both the x and y PPI properties of the renderer.
	 *
	 * @param ppiX The PPI in the x direction.
	 * @param ppiY The PPI in the y direction.
	 */
	default void setPpi( double ppiX, double ppiY ) {
		setPpiX( ppiX );
		setPpiY( ppiY );
	}

	/**
	 * Get the PPI in the x direction.
	 *
	 * @return The PPI in the x direction.
	 */
	double getPpiX();

	/**
	 * Set the PPI in the x direction.
	 *
	 * @param ppiX The PPI in the x direction.
	 */
	void setPpiX( double ppiX );

	/**
	 * Get the PPI in the x direction property.
	 *
	 * @return The PPI in the x direction property.
	 */
	DoubleProperty ppiXProperty();

	/**
	 * Get the PPI in the y direction.
	 *
	 * @return The PPI in the y direction.
	 */
	double getPpiY();

	/**
	 * Set the PPI in the y direction.
	 *
	 * @param ppiY The PPI in the y direction.
	 */
	void setPpiY( double ppiY );

	/**
	 * Get the PPI in the y direction property.
	 *
	 * @return The PPI in the y direction property.
	 */
	DoubleProperty ppiYProperty();

	/**
	 * Convenience method to get the zoom factor in both the x and y direction as
	 * a point.
	 *
	 * @return The zoom factor as a point.
	 */
	default Point2D getZoom() {
		return new Point2D( getZoomX(), getZoomY() );
	}

	/**
	 * Convenience method to set both the x and y zoom properties of the renderer.
	 *
	 * @param zoom The zoom factor as a point.
	 */
	default void setZoom( Point2D zoom ) {
		setZoom( zoom.getX(), zoom.getY() );
	}

	/**
	 * Convenience method to set both the x and y zoom properties of the renderer.
	 *
	 * @param zoomX The zoom factor in the x direction.
	 * @param zoomY The zoom factor in the y direction.
	 */
	default void setZoom( double zoomX, double zoomY ) {
		setZoomX( zoomX );
		setZoomY( zoomY );
	}

	/**
	 * Get the zoom factor in the x direction.
	 *
	 * @return The zoom factor in the x direction.
	 */
	double getZoomX();

	/**
	 * Set the zoom factor in the x direction.
	 *
	 * @param zoomX The zoom factor in the x direction.
	 */
	void setZoomX( double zoomX );

	/**
	 * Get the zoom factor in the x direction property.
	 *
	 * @return The zoom factor in the x direction property.
	 */
	DoubleProperty zoomXProperty();

	/**
	 * Get the zoom factor in the y direction.
	 *
	 * @return The zoom factor in the y direction.
	 */
	double getZoomY();

	/**
	 * Set the zoom factor in the y direction.
	 *
	 * @param zoomY The zoom factor in the y direction.
	 */
	void setZoomY( double zoomY );

	/**
	 * Get the zoom factor in the y direction property.
	 *
	 * @return The zoom factor in the y direction property.
	 */
	DoubleProperty zoomYProperty();

	/**
	 * Get the zoom step. The zoom step is the multiplier applied to the zoom
	 * in both the x and y directions when zooming relative to the
	 * current zoom (in or out).
	 *
	 * @return The zoom factor.
	 */
	double getZoomStep();

	/**
	 * Set the zoom factor.
	 *
	 * @param zoomStep The zoom factor.
	 */
	void setZoomStep( double zoomStep );

	/**
	 * Get the zoom factor property.
	 *
	 * @return The zoom factor property.
	 */
	DoubleProperty zoomStepProperty();

	/**
	 * Get the viewpoint in the x direction in world units.
	 *
	 * @return The viewpoint in the x direction.
	 */
	double getViewpointX();

	/**
	 * Set the viewpoint in the x direction in world units.
	 *
	 * @param viewpointX The viewpoint in the x direction.
	 */
	void setViewpointX( double viewpointX );

	/**
	 * Get the viewpoint in the x direction property.The property value is in
	 * world units.
	 *
	 * @return The viewpoint in the x direction property.
	 */
	DoubleProperty viewpointXProperty();

	/**
	 * Get the viewpoint in the y direction in world units.
	 *
	 * @return The viewpoint in the y direction.
	 */
	double getViewpointY();

	/**
	 * Set the viewpoint in the y direction in world units.
	 *
	 * @param viewpointY The viewpoint in the y direction.
	 */
	void setViewpointY( double viewpointY );

	/**
	 * Get the viewpoint in the y direction property. The property value is in
	 * world units.
	 *
	 * @return The viewpoint in the y direction property.
	 */
	DoubleProperty viewpointYProperty();

	/**
	 * Convenience method to get the viewpoint as a point in world units.
	 *
	 * @return The viewpoint as a point.
	 */
	default Point2D getViewpoint() {
		return new Point2D( getViewpointX(), getViewpointY() );
	}

	/**
	 * Convenience method to set both the x and y viewpoint properties of the
	 * renderer in world units.
	 *
	 * @param viewpoint The viewpoint as a point.
	 */
	default void setViewpoint( Point2D viewpoint ) {
		setViewpoint( viewpoint.getX(), viewpoint.getY() );
	}

	/**
	 * Convenience method to set both the x and y viewpoint properties of the
	 * renderer in world units.
	 *
	 * @param x The viewpoint in the x direction.
	 * @param y The viewpoint in the y direction.
	 */
	default void setViewpoint( double x, double y ) {
		setViewpointX( x );
		setViewpointY( y );
	}

	/**
	 * Get the view rotation angle in degrees.
	 *
	 * @return The viewpoint in the z direction.
	 */
	double getViewRotate();

	/**
	 * Set the view rotation angle in degrees.
	 *
	 * @param viewRotate The view rotation angle in degrees.
	 */
	void setViewRotate( double viewRotate );

	/**
	 * Get the view rotation angle in degrees property.
	 *
	 * @return The view rotation angle in degrees property.
	 */
	DoubleProperty viewRotateProperty();

	/**
	 * Convenience method to set the view point and zoom values in a single call.
	 *
	 * @param x The viewpoint in the x direction in world units.
	 * @param y The viewpoint in the y direction in world units.
	 * @param zoomX The zoom factor in the x direction.
	 * @param zoomY The zoom factor in the y direction.
	 */
	void setZoomAt( double x, double y, double zoomX, double zoomY );

	/**
	 * Clear the rendering buffer. This is typically called before rendering new
	 * geometry.
	 */
	void clear();

	/**
	 * Draw a horizontal line on the renderer.
	 *
	 * @param position The position of the line in parent coordinates.
	 * @param pen The pen to use to draw the line.
	 */
	void drawHRule( double position, Pen pen );

	/**
	 * Draw a vertical line on the renderer.
	 *
	 * @param position The position of the line in parent coordinates.
	 * @param pen The pen to use to draw the line.
	 */
	void drawVRule( double position, Pen pen );

	/**
	 * Convert an x, y point in local coordinates to a Point2D in parent
	 * coordinates.
	 *
	 * @param x The x coordinate in local coordinates.
	 * @param y The y coordinate in local coordinates.
	 */
	Point2D localToParent( double x, double y );

	default Point2D localToParent( Point2D point ) {
		return localToParent( point.getX(), point.getY() );
	}

	default Point2D localToParent2d( Point3D point ) {
		return localToParent( point.getX(), point.getY() );
	}

	/**
	 * Convert an x, y, z point in local coordinates to parent coordinates.
	 *
	 * @param x The x coordinate in local coordinates.
	 * @param y The y coordinate in local coordinates.
	 * @param z The z coordinate in local coordinates.
	 */
	Point3D localToParent( double x, double y, double z );

	default Point3D localToParent( Point3D point3d ) {
		return localToParent( point3d.getX(), point3d.getY(), point3d.getZ() );
	}

	default Point3D localToParent3d( Point2D point ) {
		return localToParent( point.getX(), point.getY(), 0 );
	}

	/**
	 * Convert an x, y point in parent coordinates to local coordinates.
	 *
	 * @param x The x coordinate in parent coordinates.
	 * @param y The y coordinate in parent coordinates.
	 */
	Point2D parentToLocal( double x, double y );

	default Point2D parentToLocal( Point2D point ) {
		return parentToLocal( point.getX(), point.getY() );
	}

	default Point2D parentToLocal2d( Point3D point ) {
		return parentToLocal( point.getX(), point.getY() );
	}

	/**
	 * Convert an x, y, z point in parent coordinates to local coordinates.
	 *
	 * @param x The x coordinate in parent coordinates.
	 * @param y The y coordinate in parent coordinates.
	 * @param z The z coordinate in parent coordinates.
	 */
	Point3D parentToLocal( double x, double y, double z );

	default Point3D parentToLocal( Point3D point3d ) {
		return parentToLocal( point3d.getX(), point3d.getY(), point3d.getZ() );
	}

	default Point3D parentToLocal3d( Point2D point ) {
		return parentToLocal( point.getX(), point.getY(), 0 );
	}

}
