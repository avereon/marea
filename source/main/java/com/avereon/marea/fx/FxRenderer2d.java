package com.avereon.marea.fx;

import com.avereon.curve.math.Point;
import com.avereon.curve.math.Vector;
import com.avereon.marea.*;
import com.avereon.marea.geom.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import lombok.CustomLog;

import java.util.Arrays;

@CustomLog
public class FxRenderer2d extends Canvas implements Renderer2d {

	public static final double DEFAULT_DPI = 96;

	public static final double DEFAULT_ZOOM = 1.0;

	public static final double DEFAULT_ZOOM_FACTOR = 0.1;

	/**
	 * This value needs to be large enough to allow small font heights to be
	 * rendered correctly. This is done by choosing a value that ensures small
	 * font heights multiplied by the FONT_POINT_SIZE is greater than 1.0. This
	 * is because the font engine does not allow font sizes smaller than 1.0. The
	 * font engine does not like really large values either. A value between 1e2
	 * and 1e6 is recommended.
	 */
	private static final double FONT_POINT_SIZE = 1e4;

	private final Affine screenTransform = new Affine();

	private Affine worldTransform = new Affine( Transform.scale( 1, -1 ) );

	private Affine worldTextTransform = new Affine( Transform.scale( 1, 1 ) );

	private DoubleProperty dpiX;

	private DoubleProperty dpiY;

	private DoubleProperty zoomX;

	private DoubleProperty zoomY;

	private DoubleProperty viewpointX;

	private DoubleProperty viewpointY;

	private DoubleProperty zoomFactorProperty;

	private double positiveZoomFactor;

	private double negativeZoomFactor;

	public FxRenderer2d( double width, double height ) {
		super( width, height );

		setZoomFactor( DEFAULT_ZOOM_FACTOR );

		setOnScroll( e -> {
			if( e.getDeltaY() != 0.0 ) {
				double zoomX = getZoom().getX();
				double zoomY = getZoom().getY();

				double scale = Math.signum( e.getDeltaY() ) < 0 ? negativeZoomFactor : positiveZoomFactor;

				zoomX = scale * zoomX;
				zoomY = scale * zoomY;

				Point2D mouse = parentToLocal( e.getX(), e.getY() );
				setZoomAt( mouse.getX(), mouse.getY(), zoomX, zoomY );
			}
		} );
	}

	@Override
	public void clear() {
		getGraphicsContext2D().setTransform( screenTransform );
		getGraphicsContext2D().clearRect( 0, 0, getWidth(), getHeight() );
	}

	@Override
	public double getZoomFactor() {
		return zoomFactorProperty().getValue();
	}

	@Override
	public void setZoomFactor( double zoomFactor ) {
		zoomFactorProperty().set( zoomFactor );
		positiveZoomFactor = 1.0 +  zoomFactor;
		negativeZoomFactor = 1.0 / positiveZoomFactor;
	}

	@Override
	public DoubleProperty zoomFactorProperty() {
		if( zoomFactorProperty == null ) zoomFactorProperty = new SimpleDoubleProperty( DEFAULT_ZOOM_FACTOR );
		return zoomFactorProperty;
	}

	@Override
	public double getDpiX() {
		return dpiXProperty().getValue();
	}

	@Override
	public void setDpiX( double dpiX ) {
		updateWorldTransforms( dpiX, getDpiY(), getZoomX(), getZoomY(), getViewpointX(), getViewpointY() );
		dpiXProperty().set( dpiX );
	}

	@Override
	public DoubleProperty dpiXProperty() {
		if( dpiX == null ) dpiX = new SimpleDoubleProperty( DEFAULT_DPI );
		return dpiX;
	}

	@Override
	public double getDpiY() {
		return dpiYProperty().getValue();
	}

	@Override
	public void setDpiY( double dpiY ) {
		updateWorldTransforms( getDpiX(), dpiY, getZoomX(), getZoomY(), getViewpointX(), getViewpointY() );
		dpiYProperty().set( dpiY );
	}

	@Override
	public DoubleProperty dpiYProperty() {
		if( dpiY == null ) dpiY = new SimpleDoubleProperty( DEFAULT_DPI );
		return dpiY;
	}

	public Point2D getDpi() {
		return new Point2D( getDpiX(), getDpiY() );
	}

	@Override
	public void setDpi( double dpiX, double dpiY ) {
		updateWorldTransform( dpiX, dpiY, getZoomX(), getZoomY(), getViewpointX(), getViewpointY() );
		dpiXProperty().set( dpiX );
		dpiYProperty().set( dpiY );
	}

	@Override
	public double getZoomX() {
		return zoomXProperty().getValue();
	}

	@Override
	public void setZoomX( double zoomX ) {
		updateWorldTransforms( getDpiX(), getDpiY(), zoomX, getZoomY(), getViewpointX(), getViewpointY() );
		zoomXProperty().set( zoomX );
	}

	@Override
	public DoubleProperty zoomXProperty() {
		if( zoomX == null ) zoomX = new SimpleDoubleProperty( DEFAULT_ZOOM );
		return zoomX;
	}

	@Override
	public double getZoomY() {
		return zoomYProperty().getValue();
	}

	@Override
	public void setZoomY( double zoomY ) {
		updateWorldTransforms( getDpiX(), getDpiY(), getZoomX(), zoomY, getViewpointX(), getViewpointY() );
		zoomYProperty().set( zoomY );
	}

	@Override
	public DoubleProperty zoomYProperty() {
		if( zoomY == null ) zoomY = new SimpleDoubleProperty( DEFAULT_ZOOM );
		return zoomY;
	}

	@Override
	public Point2D getZoom() {
		return new Point2D( getZoomX(), getZoomY() );
	}

	@Override
	public void setZoom( double zoomX, double zoomY ) {
		updateWorldTransforms( getDpiX(), getDpiY(), zoomX, zoomY, getViewpointX(), getViewpointY() );
		zoomXProperty().set( zoomX );
		zoomYProperty().set( zoomY );
	}

	@Override
	public double getViewpointX() {
		return viewpointXProperty().getValue();
	}

	@Override
	public void setViewpointX( double viewpointX ) {
		updateWorldTransforms( getDpiX(), getDpiY(), getZoomX(), getZoomY(), viewpointX, getViewpointY() );
		viewpointXProperty().set( viewpointX );
	}

	@Override
	public DoubleProperty viewpointXProperty() {
		if( viewpointX == null ) viewpointX = new SimpleDoubleProperty( DEFAULT_ZOOM );
		return viewpointX;
	}

	@Override
	public double getViewpointY() {
		return viewpointYProperty().getValue();
	}

	@Override
	public void setViewpointY( double viewpointY ) {
		updateWorldTransforms( getDpiX(), getDpiY(), getZoomX(), getZoomY(), getViewpointX(), viewpointY );
		viewpointYProperty().set( viewpointY );
	}

	@Override
	public DoubleProperty viewpointYProperty() {
		if( viewpointY == null ) viewpointY = new SimpleDoubleProperty( DEFAULT_ZOOM );
		return viewpointY;
	}

	@Override
	public Point2D getViewpoint() {
		return new Point2D( getViewpointX(), getViewpointY() );
	}

	@Override
	public void setViewpoint( double viewpointX, double viewpointY ) {
		updateWorldTransforms( getDpiX(), getDpiY(), getZoomX(), getZoomY(), viewpointX, viewpointY );
		viewpointXProperty().set( viewpointX );
		viewpointYProperty().set( viewpointY );
	}

	public void setZoomAt( double viewpointX, double viewpointY, double zoomX, double zoomY ) {
		double x = viewpointX + (getViewpointX() - viewpointX) * getZoomX() / zoomX;
		double y = viewpointY + (getViewpointY() - viewpointY) * getZoomY() / zoomY;

		// Set the new zoom and viewpoint
		updateWorldTransforms( getDpiX(), getDpiY(), zoomX, zoomY, x, y );
		viewpointXProperty().set( x );
		viewpointYProperty().set( y );
		zoomXProperty().set( zoomX );
		zoomYProperty().set( zoomY );
	}

	@Override
	public void drawHRule( double position, Pen pen ) {
		screenSetup();
		getGraphicsContext2D().setStroke( pen.paint() );
		getGraphicsContext2D().setLineWidth( pen.width() );
		getGraphicsContext2D().strokeLine( 0, position, getWidth(), position );
	}

	@Override
	public void drawVRule( double position, Pen pen ) {
		screenSetup();
		getGraphicsContext2D().setStroke( pen.paint() );
		getGraphicsContext2D().setLineWidth( pen.width() );
		getGraphicsContext2D().strokeLine( position, 0, position, getHeight() );
	}

	@Override
	public Point2D localToParent( double x, double y ) {
		return worldTransform.transform( x, y );
	}

	@Override
	public Point2D parentToLocal( double x, double y ) {
		try {
			return worldTransform.inverseTransform( x, y );
		} catch( NonInvertibleTransformException exception ) {
			return null;
		}
	}

	@Override
	public void draw( Shape2d shape, Pen pen ) {
		setPen( pen );
		switch( shape.type() ) {
			case ARC -> drawArc( (Arc)shape );
			case CURVE -> drawCurve( (Curve)shape );
			case LINE -> drawLine( (Line)shape );
			case ELLIPSE -> drawEllipse( (Ellipse)shape );
			case PATH -> drawPath( (Path)shape );
			case QUAD -> drawQuad( (Quad)shape );
			case TEXT -> drawText( (Text)shape );
		}
	}

	private void drawArc( Arc arc ) {
		double[] anchor = Vector.add( arc.getAnchor(), Point.of( -arc.getRadius()[ 0 ], -arc.getRadius()[ 1 ] ) );
		double[] radius = Vector.scale( arc.getRadius(), 2 );

		worldSetup( arc );
		getGraphicsContext2D().strokeArc( anchor[ 0 ], anchor[ 1 ], radius[ 0 ], radius[ 1 ], arc.getStart(), -arc.getExtent(), ArcType.OPEN );
	}

	private void drawEllipse( Ellipse ellipse ) {
		double[] anchor = Vector.add( ellipse.getAnchor(), Point.of( -ellipse.getRadius()[ 0 ], -ellipse.getRadius()[ 1 ] ) );
		double[] radius = Vector.scale( ellipse.getRadius(), 2 );

		worldSetup( ellipse );
		getGraphicsContext2D().strokeOval( anchor[ 0 ], anchor[ 1 ], radius[ 0 ], radius[ 1 ] );
	}

	private void drawLine( Line line ) {
		double[] anchor = line.getAnchor();
		double[] vector = line.getVector();

		worldSetup( line );
		getGraphicsContext2D().strokeLine( anchor[ 0 ], anchor[ 1 ], vector[ 0 ], vector[ 1 ] );
	}

	private void drawCurve( Curve curve ) {
		double[] a = curve.getAnchor();
		double[] b = curve.getAnchorControl();
		double[] c = curve.getVectorControl();
		double[] d = curve.getVector();

		worldSetup( curve );
		getGraphicsContext2D().beginPath();
		getGraphicsContext2D().moveTo( a[ 0 ], a[ 1 ] );
		getGraphicsContext2D().bezierCurveTo( b[ 0 ], b[ 1 ], c[ 0 ], c[ 1 ], d[ 0 ], d[ 1 ] );
		getGraphicsContext2D().stroke();
	}

	private void drawQuad( Quad quad ) {
		double[] a = quad.getAnchor();
		double[] b = quad.getControl();
		double[] c = quad.getVector();

		worldSetup( quad );
		getGraphicsContext2D().beginPath();
		getGraphicsContext2D().moveTo( a[ 0 ], a[ 1 ] );
		getGraphicsContext2D().quadraticCurveTo( b[ 0 ], b[ 1 ], c[ 0 ], c[ 1 ] );
		getGraphicsContext2D().stroke();
	}

	private void drawPath( Path path ) {
		worldSetup( path );
		runPath( path );
		getGraphicsContext2D().stroke();
	}

	private void drawText( Text text ) {
		double[] anchor = text.getAnchor();

		scalePen( FONT_POINT_SIZE );
		getGraphicsContext2D().setTransform( rotate( worldTextTransform, -text.getRotate(), Vector.scale( anchor, FONT_POINT_SIZE, -FONT_POINT_SIZE ) ) );
		getGraphicsContext2D().setFont( new Font( text.getHeight() * FONT_POINT_SIZE ) );
		getGraphicsContext2D().strokeText( text.getText(), anchor[ 0 ] * FONT_POINT_SIZE, -anchor[ 1 ] * FONT_POINT_SIZE );
	}

	@Override
	public void fill( Shape2d shape, Pen pen ) {
		setPen( pen );
		switch( shape.type() ) {
			case ELLIPSE -> fillEllipse( (Ellipse)shape );
			case PATH -> fillPath( (Path)shape );
			case TEXT -> fillText( (Text)shape );
		}
	}

	private void fillEllipse( Ellipse ellipse ) {
		double[] anchor = Vector.add( ellipse.getAnchor(), Point.of( -ellipse.getRadius()[ 0 ], -ellipse.getRadius()[ 1 ] ) );
		double[] radius = Vector.scale( ellipse.getRadius(), 2 );

		worldSetup( ellipse );
		getGraphicsContext2D().fillOval( anchor[ 0 ], anchor[ 1 ], radius[ 0 ], radius[ 1 ] );
	}

	private void fillPath( Path path ) {
		worldSetup( path );
		runPath( path );
		getGraphicsContext2D().fill();
	}

	private void fillText( Text text ) {
		double[] anchor = text.getAnchor();

		scalePen( FONT_POINT_SIZE );
		getGraphicsContext2D().setTransform( rotate( worldTextTransform, -text.getRotate(), Vector.scale( anchor, FONT_POINT_SIZE, -FONT_POINT_SIZE ) ) );
		getGraphicsContext2D().setFont( new Font( text.getHeight() * FONT_POINT_SIZE ) );
		getGraphicsContext2D().fillText( text.getText(), anchor[ 0 ] * FONT_POINT_SIZE, -anchor[ 1 ] * FONT_POINT_SIZE );
	}

	private void setPen( Pen pen ) {
		getGraphicsContext2D().setFill( pen.paint() );
		getGraphicsContext2D().setStroke( pen.paint() );
		getGraphicsContext2D().setLineWidth( pen.width() );
		getGraphicsContext2D().setLineCap( getCap( pen.cap() ) );
		getGraphicsContext2D().setLineJoin( getJoin( pen.join() ) );
		getGraphicsContext2D().setLineDashes( pen.dashes() );
		getGraphicsContext2D().setLineDashOffset( pen.offset() );
	}

	private void scalePen( double scale ) {
		getGraphicsContext2D().setLineWidth( getGraphicsContext2D().getLineWidth() * scale );
		if( getGraphicsContext2D().getLineDashes() != null ) getGraphicsContext2D().setLineDashes( Arrays.stream( getGraphicsContext2D().getLineDashes() ).map( d -> d * scale ).toArray() );
		getGraphicsContext2D().setLineDashOffset( getGraphicsContext2D().getLineDashOffset() * scale );
	}

	private void updateWorldTransforms( double dpiX, double dpiY, double zoomX, double zoomY, double viewpointX, double viewpointY ) {
		worldTextTransform = updateWorldTextTransform( dpiX, dpiY, zoomX, zoomY, viewpointX, viewpointY );
		worldTransform = updateWorldTransform( dpiX, dpiY, zoomX, zoomY, viewpointX, viewpointY );
	}

	private Affine updateWorldTransform( double dpiX, double dpiY, double zoomX, double zoomY, double viewpointX, double viewpointY ) {
		Affine affine = new Affine();

		// Center the origin
		affine.append( Transform.translate( 0.5 * getWidth(), 0.5 * getHeight() ) );

		// Invert the y-axis
		affine.append( Transform.scale( 1, -1 ) );

		// Scale for screen DPI
		affine.append( Transform.scale( dpiX, dpiY ) );

		// Apply the zoom factor
		affine.append( Transform.scale( zoomX, zoomY ) );

		// Center the viewpoint. The viewpoint is given in world coordinates
		affine.append( Transform.translate( -viewpointX, -viewpointY ) );

		return affine;
	}

	private Affine updateWorldTextTransform( double dpiX, double dpiY, double zoomX, double zoomY, double viewpointX, double viewpointY ) {
		Affine affine = new Affine();

		// Center the origin
		affine.append( Transform.translate( 0.5 * getWidth(), 0.5 * getHeight() ) );

		// Do NOT invert the y-axis

		// Scale for screen DPI
		affine.append( Transform.scale( dpiX, dpiY ) );

		// Apply the zoom factor
		affine.append( Transform.scale( zoomX / FONT_POINT_SIZE, zoomY / FONT_POINT_SIZE ) );

		// Center the viewpoint. The viewpoint is given in world coordinates
		affine.append( Transform.translate( -viewpointX * FONT_POINT_SIZE, viewpointY * FONT_POINT_SIZE ) );

		return affine;
	}

	private Affine rotate( Affine transform, double rotate, double[] anchor ) {
		Affine affine = new Affine();
		affine.append( transform );
		affine.appendRotation( rotate, anchor[ 0 ], anchor[ 1 ] );
		return affine;
	}

	private void screenSetup() {
		// set transform to screen
		getGraphicsContext2D().setTransform( screenTransform );
	}

	private void worldSetup( Shape2d shape ) {
		// set transform to screen
		getGraphicsContext2D().setTransform( rotate( worldTransform, shape.getRotate(), shape.getAnchor() ) );
	}

	private void runPath( Path path ) {
		double[] a = path.getAnchor();
		getGraphicsContext2D().beginPath();
		getGraphicsContext2D().moveTo( a[ 0 ], a[ 1 ] );
		path.getElements().forEach( e -> {
			double[] data = e.getData();
			switch( e.getType() ) {
				case LINE -> getGraphicsContext2D().lineTo( data[ 0 ], data[ 1 ] );
				case ARC -> getGraphicsContext2D().arc( data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ], data[ 4 ], data[ 5 ] );
				case QUAD -> getGraphicsContext2D().quadraticCurveTo( data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ] );
				case CURVE -> getGraphicsContext2D().bezierCurveTo( data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ], data[ 4 ], data[ 5 ] );
			}
		} );
	}

	private StrokeLineCap getCap( LineCap cap ) {
		return switch( cap ) {
			case SQUARE -> StrokeLineCap.SQUARE;
			case BUTT -> StrokeLineCap.BUTT;
			default -> StrokeLineCap.ROUND;
		};
	}

	private StrokeLineJoin getJoin( LineJoin join ) {
		return switch( join ) {
			case BEVEL -> StrokeLineJoin.BEVEL;
			case MITER -> StrokeLineJoin.MITER;
			default -> StrokeLineJoin.ROUND;
		};
	}

}
