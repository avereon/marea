package com.avereon.marea.fx;

import com.avereon.curve.math.Point;
import com.avereon.curve.math.Vector;
import com.avereon.marea.*;
import com.avereon.marea.geom.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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

	public static final LengthUnit DEFAULT_LENGTH_UNIT = LengthUnit.CENTIMETER;

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

	private ObjectProperty<LengthUnit> lengthUnit;

	private DoubleProperty dpiX;

	private DoubleProperty dpiY;

	private DoubleProperty zoomX;

	private DoubleProperty zoomY;

	private DoubleProperty viewpointX;

	private DoubleProperty viewpointY;

	private DoubleProperty viewRotate;

	private DoubleProperty zoomFactor;

	private double positiveZoomFactor;

	private double negativeZoomFactor;

	private Point2D dragViewpoint;

	private Point2D dragAnchor;

	public FxRenderer2d() {
		this( 0, 0 );
	}

	public FxRenderer2d( double width, double height ) {
		super( width, height );

		setZoomFactor( DEFAULT_ZOOM_FACTOR );

		setOnScroll( this::doOnScroll );

		setOnMousePressed( this::doOnDragBegin );

		setOnMouseDragged( this::doOnDragMouse );

		setOnMouseReleased( this::doOnDragFinish );

		lengthUnitProperty().addListener( ( p, o, n ) -> updateWorldTransforms( n,
			getDpiX(),
			getDpiY(),
			getZoomX(),
			getZoomY(),
			getViewpointX(),
			getViewpointY(),
			getViewRotate(),
			getWidth(),
			getHeight()
		) );
		widthProperty().addListener( ( p, o, n ) -> updateWorldTransforms( getLengthUnit(),
			getDpiX(),
			getDpiY(),
			getZoomX(),
			getZoomY(),
			getViewpointX(),
			getViewpointY(),
			getViewRotate(),
			n.doubleValue(),
			getHeight()
		) );
		heightProperty().addListener( ( p, o, n ) -> updateWorldTransforms( getLengthUnit(),
			getDpiX(),
			getDpiY(),
			getZoomX(),
			getZoomY(),
			getViewpointX(),
			getViewpointY(),
			getViewRotate(),
			getWidth(),
			n.doubleValue()
		) );

		updateWorldTransforms( getLengthUnit(), getDpiX(), getDpiY(), getZoomX(), getZoomY(), getViewpointX(), getViewpointY(), getViewRotate(), getWidth(), getHeight() );
	}

	@Override
	public void clear() {
		getGraphicsContext2D().setTransform( screenTransform );
		getGraphicsContext2D().clearRect( 0, 0, getWidth(), getHeight() );
	}

	@Override
	public LengthUnit getLengthUnit() {
		return lengthUnit == null ? DEFAULT_LENGTH_UNIT : lengthUnit.get();
	}

	@Override
	public void setLengthUnit( LengthUnit unit ) {
		lengthUnitProperty().set( unit );
	}

	@Override
	public ObjectProperty<LengthUnit> lengthUnitProperty() {
		if( lengthUnit == null ) lengthUnit = new SimpleObjectProperty<>( DEFAULT_LENGTH_UNIT );
		return lengthUnit;
	}

	@Override
	public double getDpiX() {
		return dpiX == null ? DEFAULT_DPI : dpiXProperty().getValue();
	}

	@Override
	public void setDpiX( double dpiX ) {
		updateWorldTransforms( getLengthUnit(), dpiX, getDpiY(), getZoomX(), getZoomY(), getViewpointX(), getViewpointY(), getViewRotate(), getWidth(), getHeight() );
		dpiXProperty().set( dpiX );
	}

	@Override
	public DoubleProperty dpiXProperty() {
		if( dpiX == null ) dpiX = new SimpleDoubleProperty( DEFAULT_DPI );
		return dpiX;
	}

	@Override
	public double getDpiY() {
		return dpiY == null ? DEFAULT_DPI : dpiYProperty().getValue();
	}

	@Override
	public void setDpiY( double dpiY ) {
		updateWorldTransforms( getLengthUnit(), getDpiX(), dpiY, getZoomX(), getZoomY(), getViewpointX(), getViewpointY(), getViewRotate(), getWidth(), getHeight() );
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
		updateWorldTransforms( getLengthUnit(), dpiX, dpiY, getZoomX(), getZoomY(), getViewpointX(), getViewpointY(), getViewRotate(), getWidth(), getHeight() );
		dpiXProperty().set( dpiX );
		dpiYProperty().set( dpiY );
	}

	@Override
	public double getZoomX() {
		return zoomX == null ? DEFAULT_ZOOM : zoomXProperty().getValue();
	}

	@Override
	public void setZoomX( double zoomX ) {
		updateWorldTransforms( getLengthUnit(), getDpiX(), getDpiY(), zoomX, getZoomY(), getViewpointX(), getViewpointY(), getViewRotate(), getWidth(), getHeight() );
		zoomXProperty().set( zoomX );
	}

	@Override
	public DoubleProperty zoomXProperty() {
		if( zoomX == null ) zoomX = new SimpleDoubleProperty( DEFAULT_ZOOM );
		return zoomX;
	}

	@Override
	public double getZoomY() {
		return zoomY == null ? DEFAULT_ZOOM : zoomYProperty().getValue();
	}

	@Override
	public void setZoomY( double zoomY ) {
		updateWorldTransforms( getLengthUnit(), getDpiX(), getDpiY(), getZoomX(), zoomY, getViewpointX(), getViewpointY(), getViewRotate(), getWidth(), getHeight() );
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
		updateWorldTransforms( getLengthUnit(), getDpiX(), getDpiY(), zoomX, zoomY, getViewpointX(), getViewpointY(), getViewRotate(), getWidth(), getHeight() );
		zoomXProperty().set( zoomX );
		zoomYProperty().set( zoomY );
	}

	@Override
	public double getZoomFactor() {
		return zoomFactor == null ? DEFAULT_ZOOM_FACTOR : zoomFactorProperty().getValue();
	}

	@Override
	public void setZoomFactor( double zoomFactor ) {
		zoomFactorProperty().set( zoomFactor );
		positiveZoomFactor = 1.0 + zoomFactor;
		negativeZoomFactor = 1.0 / positiveZoomFactor;
	}

	@Override
	public DoubleProperty zoomFactorProperty() {
		if( zoomFactor == null ) zoomFactor = new SimpleDoubleProperty( DEFAULT_ZOOM_FACTOR );
		return zoomFactor;
	}

	@Override
	public double getViewpointX() {
		return viewpointX == null ? 0.0 : viewpointXProperty().getValue();
	}

	@Override
	public void setViewpointX( double viewpointX ) {
		updateWorldTransforms( getLengthUnit(), getDpiX(), getDpiY(), getZoomX(), getZoomY(), viewpointX, getViewpointY(), getViewRotate(), getWidth(), getHeight() );
		viewpointXProperty().set( viewpointX );
	}

	@Override
	public DoubleProperty viewpointXProperty() {
		if( viewpointX == null ) viewpointX = new SimpleDoubleProperty( 0.0 );
		return viewpointX;
	}

	@Override
	public double getViewpointY() {
		return viewpointY == null ? 0.0 : viewpointYProperty().getValue();
	}

	@Override
	public void setViewpointY( double viewpointY ) {
		updateWorldTransforms( getLengthUnit(), getDpiX(), getDpiY(), getZoomX(), getZoomY(), getViewpointX(), viewpointY, getViewRotate(), getWidth(), getHeight() );
		viewpointYProperty().set( viewpointY );
	}

	@Override
	public DoubleProperty viewpointYProperty() {
		if( viewpointY == null ) viewpointY = new SimpleDoubleProperty( 0.0 );
		return viewpointY;
	}

	@Override
	public Point2D getViewpoint() {
		return new Point2D( getViewpointX(), getViewpointY() );
	}

	@Override
	public void setViewpoint( double viewpointX, double viewpointY ) {
		updateWorldTransforms( getLengthUnit(), getDpiX(), getDpiY(), getZoomX(), getZoomY(), viewpointX, viewpointY, getViewRotate(), getWidth(), getHeight() );
		viewpointXProperty().set( viewpointX );
		viewpointYProperty().set( viewpointY );
	}

	@Override
	public double getViewRotate() {
		return viewRotate == null ? 0.0 : viewRotateProperty().getValue();
	}

	@Override
	public void setViewRotate( double viewRotate ) {
		updateWorldTransforms( getLengthUnit(), getDpiX(), getDpiY(), getZoomX(), getZoomY(), getViewpointX(), getViewpointY(), viewRotate, getWidth(), getHeight() );
		viewRotateProperty().set( viewRotate );
	}

	@Override
	public DoubleProperty viewRotateProperty() {
		if( viewRotate == null ) viewRotate = new SimpleDoubleProperty( 0.0 );
		return viewRotate;
	}

	public void setZoomAt( double viewpointX, double viewpointY, double zoomX, double zoomY ) {
		double x = viewpointX + (getViewpointX() - viewpointX) * getZoomX() / zoomX;
		double y = viewpointY + (getViewpointY() - viewpointY) * getZoomY() / zoomY;

		// Set the new zoom and viewpoint
		updateWorldTransforms( getLengthUnit(), getDpiX(), getDpiY(), zoomX, zoomY, x, y, getViewRotate(), getWidth(), getHeight() );
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
		useFontScales( text );
		double[] anchor = text.getAnchor();
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
		useFontScales( text );
		double[] anchor = text.getAnchor();
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

	private void useFontScales( Text text ) {
		double[] anchor = text.getAnchor();
		getGraphicsContext2D().setLineWidth( getGraphicsContext2D().getLineWidth() * FONT_POINT_SIZE );
		if( getGraphicsContext2D().getLineDashes() != null ) getGraphicsContext2D().setLineDashes( Arrays.stream( getGraphicsContext2D().getLineDashes() ).map( d -> d * FONT_POINT_SIZE ).toArray() );
		getGraphicsContext2D().setLineDashOffset( getGraphicsContext2D().getLineDashOffset() * FONT_POINT_SIZE );
		getGraphicsContext2D().setTransform( rotate( worldTextTransform, -text.getRotate(), Vector.scale( anchor, FONT_POINT_SIZE, -FONT_POINT_SIZE ) ) );
		getGraphicsContext2D().setFont( new Font( text.getHeight() * FONT_POINT_SIZE ) );
	}

	private void updateWorldTransforms( LengthUnit lengthUnit, double dpiX, double dpiY, double zoomX, double zoomY, double viewpointX, double viewpointY, double rotate, double width, double height ) {
		worldTransform = createWorldTransform( lengthUnit, dpiX, dpiY, zoomX, zoomY, viewpointX, viewpointY, rotate, width, height, false );
		worldTextTransform = createWorldTransform( lengthUnit, dpiX, dpiY, zoomX, zoomY, viewpointX, viewpointY, rotate, width, height, true );
	}

	private static Affine createWorldTransform(
		LengthUnit lengthUnit, double dpiX, double dpiY, double zoomX, double zoomY, double viewpointX, double viewpointY, double rotate, double width, double height, boolean isFontTransform
	) {
		double fontPointSize = 1.0;
		if( isFontTransform ) {
			fontPointSize = FONT_POINT_SIZE;
			rotate = -rotate;
		} else {
			zoomY = -zoomY;
			viewpointY = -viewpointY;
		}

		Affine affine = new Affine();

		// Center the origin before applying scale and zoom
		affine.append( Transform.translate( 0.5 * width, 0.5 * height ) );

		// Scale for screen DPI
		affine.append( Transform.scale( lengthUnit.convert( dpiX ), lengthUnit.convert( dpiY ) ) );

		// Apply the zoom factor
		affine.append( Transform.scale( zoomX / fontPointSize, zoomY / fontPointSize ) );

		// Rotate the view
		affine.append( Transform.rotate( rotate, -viewpointX * fontPointSize, viewpointY * fontPointSize ) );

		// Center the viewpoint. The viewpoint is given in world coordinates.
		affine.append( Transform.translate( -viewpointX * fontPointSize, viewpointY * fontPointSize ) );

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

	private void doOnDragBegin( MouseEvent e ) {
		dragViewpoint = getViewpoint();
		dragAnchor = new Point2D( e.getX(), e.getY() );
	}

	private void doOnDragMouse( MouseEvent e ) {
		dragMove( e.getX(), e.getY() );
	}

	private void doOnDragFinish( MouseEvent e ) {
		dragMove( e.getX(), e.getY() );
		dragAnchor = null;
		dragViewpoint = null;
	}

	private void dragMove( double x, double y ) {
		double dx = (x - dragAnchor.getX()) / getLengthUnit().convert( getDpiX() * getZoomX() );
		double dy = (y - dragAnchor.getY()) / getLengthUnit().convert( getDpiY() * getZoomY() );
		setViewpoint( dragViewpoint.getX() - dx, dragViewpoint.getY() + dy );
	}

	private void doOnScroll( ScrollEvent e) {
		if( e.getDeltaY() != 0.0 ) {
			double zoomX = getZoom().getX();
			double zoomY = getZoom().getY();

			double scale = Math.signum( e.getDeltaY() ) < 0 ? negativeZoomFactor : positiveZoomFactor;

			zoomX = scale * zoomX;
			zoomY = scale * zoomY;

			Point2D mouse = parentToLocal( e.getX(), e.getY() );
			setZoomAt( mouse.getX(), mouse.getY(), zoomX, zoomY );
		}
	}

	private void runPath( Path path ) {
		double[] a = path.getAnchor();
		getGraphicsContext2D().beginPath();
		getGraphicsContext2D().moveTo( a[ 0 ], a[ 1 ] );
		path.getElements().forEach( e -> {
			double[] data = e.getData();
			switch( e.getCommand() ) {
				case ARC -> getGraphicsContext2D().arc( data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ], data[ 4 ], data[ 5 ] );
				case CLOSE -> getGraphicsContext2D().closePath();
				case CURVE -> getGraphicsContext2D().bezierCurveTo( data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ], data[ 4 ], data[ 5 ] );
				case LINE -> getGraphicsContext2D().lineTo( data[ 0 ], data[ 1 ] );
				case MOVE -> getGraphicsContext2D().moveTo( data[ 0 ], data[ 1 ] );
				case QUAD -> getGraphicsContext2D().quadraticCurveTo( data[ 0 ], data[ 1 ], data[ 2 ], data[ 3 ] );
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
