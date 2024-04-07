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
import javafx.geometry.Point3D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import lombok.CustomLog;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@CustomLog
public class FxRenderer2d extends Canvas implements DirectRenderer2d, ShapeRenderer2d {

	public static final RenderUnit DEFAULT_LENGTH_UNIT = RenderUnit.CENTIMETER;

	public static final double DEFAULT_PPI = 72;

	public static final double DEFAULT_ZOOM = 1.0;

	public static final double DEFAULT_ZOOM_STEP_FACTOR = 0.1;

	/**
	 * This value needs to be large enough to allow small font heights to be
	 * rendered correctly. This is done by choosing a value that ensures small
	 * font heights multiplied by the FONT_POINT_SIZE is greater than 1.0. This
	 * is because the font engine does not allow font sizes smaller than 1.0. The
	 * font engine does not like really large values either. A value between 1e2
	 * and 1e6 is recommended.
	 */
	private static final double FONT_POINT_SIZE = 1e4;

	// Transforms ---------------------------------------------------------------

	private final Affine screenTransform = new Affine();

	private Affine worldTransform = new Affine( Transform.scale( 1, -1 ) );

	private Affine worldTextTransform = new Affine( Transform.scale( 1, 1 ) );

	// Properties ---------------------------------------------------------------

	private ObjectProperty<RenderUnit> lengthUnit;

	private DoubleProperty ppiX;

	private DoubleProperty ppiY;

	private DoubleProperty zoomX;

	private DoubleProperty zoomY;

	private DoubleProperty zoomStep;

	private DoubleProperty viewpointX;

	private DoubleProperty viewpointY;

	private DoubleProperty viewRotate;

	// Internal variables -------------------------------------------------------

	private double positiveZoomStep;

	private double negativeZoomStep;

	private Point2D dragViewpoint;

	private Point2D dragAnchor;

	public FxRenderer2d() {
		this( 0, 0 );
	}

	public FxRenderer2d( double width, double height ) {
		super( width, height );

		setZoomStep( DEFAULT_ZOOM_STEP_FACTOR );

		// These are the default handlers
		setOnScroll( this::doOnScroll );
		setOnMousePressed( this::doOnDragBegin );
		setOnMouseDragged( this::doOnDragMouse );
		setOnMouseReleased( this::doOnDragFinish );

		lengthUnitProperty().addListener( ( p, o, n ) -> updateWorldTransforms( n,
			getPpiX(),
			getPpiY(),
			getZoomX(),
			getZoomY(),
			getViewpointX(),
			getViewpointY(),
			getViewRotate(),
			getWidth(),
			getHeight()
		) );
		widthProperty().addListener( ( p, o, n ) -> updateWorldTransforms( getLengthUnit(),
			getPpiX(),
			getPpiY(),
			getZoomX(),
			getZoomY(),
			getViewpointX(),
			getViewpointY(),
			getViewRotate(),
			n.doubleValue(),
			getHeight()
		) );
		heightProperty().addListener( ( p, o, n ) -> updateWorldTransforms( getLengthUnit(),
			getPpiX(),
			getPpiY(),
			getZoomX(),
			getZoomY(),
			getViewpointX(),
			getViewpointY(),
			getViewRotate(),
			getWidth(),
			n.doubleValue()
		) );

		updateWorldTransforms( getLengthUnit(), getPpiX(), getPpiY(), getZoomX(), getZoomY(), getViewpointX(), getViewpointY(), getViewRotate(), getWidth(), getHeight() );
	}

	@Override
	public void clear() {
		getGraphicsContext2D().setTransform( screenTransform );
		getGraphicsContext2D().clearRect( 0, 0, getWidth(), getHeight() );
	}

	@Override
	public RenderUnit getLengthUnit() {
		return lengthUnit == null ? DEFAULT_LENGTH_UNIT : lengthUnit.get();
	}

	@Override
	public void setLengthUnit( RenderUnit unit ) {
		lengthUnitProperty().set( unit );
	}

	@Override
	public ObjectProperty<RenderUnit> lengthUnitProperty() {
		if( lengthUnit == null ) lengthUnit = new SimpleObjectProperty<>( DEFAULT_LENGTH_UNIT );
		return lengthUnit;
	}

	@Override
	public double getPpiX() {
		return ppiX == null ? DEFAULT_PPI : ppiXProperty().getValue();
	}

	@Override
	public void setPpiX( double ppiX ) {
		updateWorldTransforms( getLengthUnit(), ppiX, getPpiY(), getZoomX(), getZoomY(), getViewpointX(), getViewpointY(), getViewRotate(), getWidth(), getHeight() );
		ppiXProperty().set( ppiX );
	}

	@Override
	public DoubleProperty ppiXProperty() {
		if( ppiX == null ) ppiX = new SimpleDoubleProperty( DEFAULT_PPI );
		return ppiX;
	}

	@Override
	public double getPpiY() {
		return ppiY == null ? DEFAULT_PPI : ppiYProperty().getValue();
	}

	@Override
	public void setPpiY( double ppiY ) {
		updateWorldTransforms( getLengthUnit(), getPpiX(), ppiY, getZoomX(), getZoomY(), getViewpointX(), getViewpointY(), getViewRotate(), getWidth(), getHeight() );
		ppiYProperty().set( ppiY );
	}

	@Override
	public DoubleProperty ppiYProperty() {
		if( ppiY == null ) ppiY = new SimpleDoubleProperty( DEFAULT_PPI );
		return ppiY;
	}

	/**
	 * Small optimization over the default implementation by updating the world
	 * transforms only once.
	 *
	 * @param dpiX The DPI in the x direction.
	 * @param dpiY The DPI in the y direction.
	 */
	@Override
	public void setPpi( double dpiX, double dpiY ) {
		updateWorldTransforms( getLengthUnit(), dpiX, dpiY, getZoomX(), getZoomY(), getViewpointX(), getViewpointY(), getViewRotate(), getWidth(), getHeight() );
		ppiXProperty().set( dpiX );
		ppiYProperty().set( dpiY );
	}

	/**
	 * Get the zoom factor in the x direction.
	 */
	@Override
	public double getZoomX() {
		return zoomX == null ? DEFAULT_ZOOM : zoomXProperty().getValue();
	}

	@Override
	public void setZoomX( double zoomX ) {
		updateWorldTransforms( getLengthUnit(), getPpiX(), getPpiY(), zoomX, getZoomY(), getViewpointX(), getViewpointY(), getViewRotate(), getWidth(), getHeight() );
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
		updateWorldTransforms( getLengthUnit(), getPpiX(), getPpiY(), getZoomX(), zoomY, getViewpointX(), getViewpointY(), getViewRotate(), getWidth(), getHeight() );
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

	/**
	 * A small optimization over the default implementation by updating the world
	 * transforms only once.
	 *
	 * @param zoomX The zoom factor in the x direction.
	 * @param zoomY The zoom factor in the y direction.
	 */
	@Override
	public void setZoom( double zoomX, double zoomY ) {
		updateWorldTransforms( getLengthUnit(), getPpiX(), getPpiY(), zoomX, zoomY, getViewpointX(), getViewpointY(), getViewRotate(), getWidth(), getHeight() );
		zoomXProperty().set( zoomX );
		zoomYProperty().set( zoomY );
	}

	@Override
	public double getZoomStep() {
		return zoomStep == null ? DEFAULT_ZOOM_STEP_FACTOR : zoomStepProperty().getValue();
	}

	@Override
	public void setZoomStep( double zoomStep ) {
		zoomStepProperty().set( zoomStep );
		positiveZoomStep = 1.0 + zoomStep;
		negativeZoomStep = 1.0 / positiveZoomStep;
	}

	@Override
	public DoubleProperty zoomStepProperty() {
		if( zoomStep == null ) zoomStep = new SimpleDoubleProperty( DEFAULT_ZOOM_STEP_FACTOR );
		return zoomStep;
	}

	@Override
	public double getViewpointX() {
		return viewpointX == null ? 0.0 : viewpointXProperty().getValue();
	}

	@Override
	public void setViewpointX( double viewpointX ) {
		updateWorldTransforms( getLengthUnit(), getPpiX(), getPpiY(), getZoomX(), getZoomY(), viewpointX, getViewpointY(), getViewRotate(), getWidth(), getHeight() );
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
		updateWorldTransforms( getLengthUnit(), getPpiX(), getPpiY(), getZoomX(), getZoomY(), getViewpointX(), viewpointY, getViewRotate(), getWidth(), getHeight() );
		viewpointYProperty().set( viewpointY );
	}

	@Override
	public DoubleProperty viewpointYProperty() {
		if( viewpointY == null ) viewpointY = new SimpleDoubleProperty( 0.0 );
		return viewpointY;
	}

	/**
	 * A small optimization over the default implementation by updating the world
	 * transforms only once.
	 *
	 * @param viewpointX The viewpoint in the x direction.
	 * @param viewpointY The viewpoint in the y direction.
	 */
	@Override
	public void setViewpoint( double viewpointX, double viewpointY ) {
		updateWorldTransforms( getLengthUnit(), getPpiX(), getPpiY(), getZoomX(), getZoomY(), viewpointX, viewpointY, getViewRotate(), getWidth(), getHeight() );
		viewpointXProperty().set( viewpointX );
		viewpointYProperty().set( viewpointY );
	}

	@Override
	public double getViewRotate() {
		return viewRotate == null ? 0.0 : viewRotateProperty().getValue();
	}

	@Override
	public void setViewRotate( double viewRotate ) {
		updateWorldTransforms( getLengthUnit(), getPpiX(), getPpiY(), getZoomX(), getZoomY(), getViewpointX(), getViewpointY(), viewRotate, getWidth(), getHeight() );
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
		updateWorldTransforms( getLengthUnit(), getPpiX(), getPpiY(), zoomX, zoomY, x, y, getViewRotate(), getWidth(), getHeight() );
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
	public Point3D localToParent( double x, double y, double z ) {
		return worldTransform.transform( x, y, z );
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
	public Point3D parentToLocal( double x, double y, double z ) {
		try {
			return worldTransform.inverseTransform( x, y, z );
		} catch( NonInvertibleTransformException exception ) {
			return null;
		}
	}

	public void setDrawPen( Paint paint, double width, LineCap cap, LineJoin join, double[] dashes, double offset ) {
		getGraphicsContext2D().setStroke( paint );
		getGraphicsContext2D().setLineWidth( width );
		getGraphicsContext2D().setLineCap( getCap( cap ) );
		getGraphicsContext2D().setLineJoin( getJoin( join ) );
		getGraphicsContext2D().setLineDashes( dashes );
		getGraphicsContext2D().setLineDashOffset( offset );
	}

	public void setFillPen( Paint paint ) {
		getGraphicsContext2D().setFill( paint );
	}

	public void setFont( Font font ) {
		getGraphicsContext2D().setFont( Font.toFxFont( font ) );
	}

	public void drawLine( double x1, double y1, double x2, double y2 ) {
		shapeSetup( x1, y1, 0 );
		getGraphicsContext2D().strokeLine( x1, y1, x2, y2 );
	}

	public void drawEllipse( double cx, double cy, double rx, double ry, double rotate ) {
		shapeSetup( cx, cy, rotate );
		getGraphicsContext2D().strokeOval( cx - rx, cy - ry, 2 * rx, 2 * ry );
	}

	public void drawArc( double cx, double cy, double rx, double ry, double start, double extent, double rotate ) {
		shapeSetup( cx, cy, rotate );
		getGraphicsContext2D().strokeArc( cx - rx, cy - ry, 2 * rx, 2 * ry, -start, -extent, ArcType.OPEN );
	}

	public void drawQuad( double x1, double y1, double x2, double y2, double x3, double y3 ) {
		shapeSetup( x1, y1, 0 );
		getGraphicsContext2D().beginPath();
		getGraphicsContext2D().moveTo( x1, y1 );
		getGraphicsContext2D().quadraticCurveTo( x2, y2, x3, y3 );
		getGraphicsContext2D().stroke();
	}

	public void drawCubic( double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4 ) {
		shapeSetup( x1, y1, 0 );
		getGraphicsContext2D().beginPath();
		getGraphicsContext2D().moveTo( x1, y1 );
		getGraphicsContext2D().bezierCurveTo( x2, y2, x3, y3, x4, y4 );
		getGraphicsContext2D().stroke();
	}

	@Override
	public void drawPath( List<Path.Element> path ) {
		shapeSetup( 0, 0, 0 );
		getGraphicsContext2D().beginPath();
		runPath( path );
		getGraphicsContext2D().stroke();
	}

	public void drawText( double x, double y, double height, double rotate, String text, Font font ) {
		shapeSetup( x, y, rotate );
		getGraphicsContext2D().save();
		useFontScales( x, y, height, rotate, font );
		getGraphicsContext2D().setTransform( rotate( worldTextTransform, -rotate, new double[]{ x, y } ) );
		getGraphicsContext2D().strokeText( text, x, y );
		getGraphicsContext2D().restore();
	}

	public void fillEllipse( double cx, double cy, double rx, double ry, double rotate ) {
		shapeSetup( cx, cy, rotate );
		getGraphicsContext2D().fillOval( cx - rx, cy - ry, 2 * rx, 2 * ry );
	}

	@Override
	public void fillPath( List<Path.Element> path ) {
		shapeSetup( 0, 0, 0 );
		getGraphicsContext2D().beginPath();
		runPath( path );
		getGraphicsContext2D().fill();
	}

	@Override
	public void fillText( double x, double y, double height, double rotate, String text, Font font ) {
		shapeSetup( x, y, rotate );
		getGraphicsContext2D().save();
		useFontScales( x, y, height, rotate, font );
		getGraphicsContext2D().setTransform( rotate( worldTextTransform, -rotate, new double[]{ x, y } ) );
		getGraphicsContext2D().fillText( text, x, y );
		getGraphicsContext2D().restore();
	}

	@Override
	@Deprecated
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

	@Deprecated
	public void draw( Collection<? extends Shape2d> shapes, Pen pen ) {
		shapes.forEach( s -> draw( s, pen ) );
	}

	@Deprecated
	private void drawArc( Arc arc ) {
		double[] anchor = Vector.add( arc.getAnchor(), Point.of( -arc.getRadius()[ 0 ], -arc.getRadius()[ 1 ] ) );
		double[] radius = Vector.scale( arc.getRadius(), 2 );

		shapeSetup( arc );
		getGraphicsContext2D().strokeArc( anchor[ 0 ], anchor[ 1 ], radius[ 0 ], radius[ 1 ], -arc.getStart(), -arc.getExtent(), ArcType.OPEN );
	}

	@Deprecated
	private void drawEllipse( Ellipse ellipse ) {
		double[] anchor = Vector.add( ellipse.getAnchor(), Point.of( -ellipse.getRadius()[ 0 ], -ellipse.getRadius()[ 1 ] ) );
		double[] radius = Vector.scale( ellipse.getRadius(), 2 );

		shapeSetup( ellipse );
		getGraphicsContext2D().strokeOval( anchor[ 0 ], anchor[ 1 ], radius[ 0 ], radius[ 1 ] );
	}

	@Deprecated
	private void drawLine( Line line ) {
		double[] anchor = line.getAnchor();
		double[] vector = line.getVector();

		shapeSetup( line );
		getGraphicsContext2D().strokeLine( anchor[ 0 ], anchor[ 1 ], vector[ 0 ], vector[ 1 ] );
	}

	@Deprecated
	private void drawCurve( Curve curve ) {
		double[] a = curve.getAnchor();
		double[] b = curve.getAnchorControl();
		double[] c = curve.getVectorControl();
		double[] d = curve.getVector();

		shapeSetup( curve );
		getGraphicsContext2D().beginPath();
		getGraphicsContext2D().moveTo( a[ 0 ], a[ 1 ] );
		getGraphicsContext2D().bezierCurveTo( b[ 0 ], b[ 1 ], c[ 0 ], c[ 1 ], d[ 0 ], d[ 1 ] );
		getGraphicsContext2D().stroke();
	}

	@Deprecated
	private void drawQuad( Quad quad ) {
		double[] a = quad.getAnchor();
		double[] b = quad.getControl();
		double[] c = quad.getVector();

		shapeSetup( quad );
		getGraphicsContext2D().beginPath();
		getGraphicsContext2D().moveTo( a[ 0 ], a[ 1 ] );
		getGraphicsContext2D().quadraticCurveTo( b[ 0 ], b[ 1 ], c[ 0 ], c[ 1 ] );
		getGraphicsContext2D().stroke();
	}

	@Deprecated
	private void drawPath( Path path ) {
		shapeSetup( path );
		runPath( path.getElements() );
		getGraphicsContext2D().stroke();
	}

	@Deprecated
	private void drawText( Text text ) {
		useFontScales( text );
		double[] anchor = text.getAnchor();
		getGraphicsContext2D().strokeText( text.getText(), anchor[ 0 ] * FONT_POINT_SIZE, -anchor[ 1 ] * FONT_POINT_SIZE );
	}

	@Override
	@Deprecated
	public void fill( Shape2d shape, Pen pen ) {
		setPen( pen );
		switch( shape.type() ) {
			case ELLIPSE -> fillEllipse( (Ellipse)shape );
			case PATH -> fillPath( (Path)shape );
			case TEXT -> fillText( (Text)shape );
		}
	}

	@Deprecated
	public void fill( Collection<? extends Shape2d> shapes, Pen pen ) {
		shapes.forEach( s -> fill( s, pen ) );
	}

	@Deprecated
	private void fillEllipse( Ellipse ellipse ) {
		double[] anchor = Vector.add( ellipse.getAnchor(), Point.of( -ellipse.getRadius()[ 0 ], -ellipse.getRadius()[ 1 ] ) );
		double[] radius = Vector.scale( ellipse.getRadius(), 2 );

		shapeSetup( ellipse );
		getGraphicsContext2D().fillOval( anchor[ 0 ], anchor[ 1 ], radius[ 0 ], radius[ 1 ] );
	}

	@Deprecated
	private void fillPath( Path path ) {
		shapeSetup( path );
		runPath( path.getElements() );
		getGraphicsContext2D().fill();
	}

	@Deprecated
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
		useFontScales( text.getAnchor(), text.getHeight(), text.getRotate(), text.getFont() );
	}

	private void useFontScales( double[] anchor, double height, double rotate, Font font ) {
		useFontScales( anchor[ 0 ], anchor[ 1 ], height, rotate, font );
	}

	private void useFontScales( double x, double y, double height, double rotate, Font font ) {
		getGraphicsContext2D().setLineWidth( getGraphicsContext2D().getLineWidth() * FONT_POINT_SIZE );
		if( getGraphicsContext2D().getLineDashes() != null ) getGraphicsContext2D().setLineDashes( Arrays.stream( getGraphicsContext2D().getLineDashes() ).map( d -> d * FONT_POINT_SIZE ).toArray() );
		getGraphicsContext2D().setLineDashOffset( getGraphicsContext2D().getLineDashOffset() * FONT_POINT_SIZE );
		getGraphicsContext2D().setTransform( rotate( worldTextTransform, -rotate, Vector.scale( Vector.of( x, y ), FONT_POINT_SIZE, -FONT_POINT_SIZE ) ) );
		getGraphicsContext2D().setFont( Font.toFxFont( (font == null ? new Font() : font).derive( height * FONT_POINT_SIZE ) ) );
	}

	private void updateWorldTransforms( RenderUnit unit, double dpiX, double dpiY, double zoomX, double zoomY, double viewpointX, double viewpointY, double rotate, double width, double height ) {
		worldTransform = createWorldTransform( unit, dpiX, dpiY, zoomX, zoomY, viewpointX, viewpointY, rotate, width, height, false );
		worldTextTransform = createWorldTransform( unit, dpiX, dpiY, zoomX, zoomY, viewpointX, viewpointY, rotate, width, height, true );
	}

	private static Affine createWorldTransform(
		RenderUnit unit, double dpiX, double dpiY, double zoomX, double zoomY, double viewpointX, double viewpointY, double rotate, double width, double height, boolean isFontTransform
	) {
		double scale = 1.0;
		if( isFontTransform ) {
			scale = FONT_POINT_SIZE;
			rotate = -rotate;
		} else {
			zoomY = -zoomY;
			viewpointY = -viewpointY;
		}

		Affine affine = new Affine();

		// Center the origin before applying scale and zoom
		affine.append( Transform.translate( 0.5 * width, 0.5 * height ) );

		// Scale for screen DPI
		affine.append( Transform.scale( unit.convert( dpiX ), unit.convert( dpiY ) ) );

		// Apply the zoom factor
		affine.append( Transform.scale( zoomX / scale, zoomY / scale ) );

		// Rotate the view
		affine.append( Transform.rotate( rotate, -viewpointX * scale, viewpointY * scale ) );

		// Center the viewpoint. The viewpoint is given in world coordinates.
		affine.append( Transform.translate( -viewpointX * scale, viewpointY * scale ) );

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

	//	private void worldSetup() {
	//		// set transform to screen
	//		getGraphicsContext2D().setTransform( worldTransform );
	//	}

	private void shapeSetup( double x, double y, double r ) {
		shapeSetup( new double[]{ x, y }, r );
	}

	private void shapeSetup( double[] anchor, double rotate ) {
		// set transform to screen
		getGraphicsContext2D().setTransform( rotate( worldTransform, rotate, anchor ) );
	}

	@Deprecated
	private void shapeSetup( Shape2d shape ) {
		// set transform to screen
		shapeSetup( shape.getAnchor(), shape.getRotate() );
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
		double dx = (x - dragAnchor.getX()) / getLengthUnit().convert( getPpiX() * getZoomX() );
		double dy = (y - dragAnchor.getY()) / getLengthUnit().convert( getPpiY() * getZoomY() );
		setViewpoint( dragViewpoint.getX() - dx, dragViewpoint.getY() + dy );
	}

	private void doOnScroll( ScrollEvent e ) {
		if( e.getDeltaY() != 0.0 ) {
			double zoomX = getZoom().getX();
			double zoomY = getZoom().getY();

			double scale = Math.signum( e.getDeltaY() ) < 0 ? negativeZoomStep : positiveZoomStep;

			zoomX = scale * zoomX;
			zoomY = scale * zoomY;

			Point2D mouse = parentToLocal( e.getX(), e.getY() );
			setZoomAt( mouse.getX(), mouse.getY(), zoomX, zoomY );
		}
	}

	private void runPath( List<Path.Element> elements ) {
		getGraphicsContext2D().beginPath();
		elements.forEach( e -> {
			double[] data = e.data();
			switch( e.command() ) {
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
