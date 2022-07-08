package com.avereon.marea.fx;

import com.avereon.curve.math.Point;
import com.avereon.curve.math.Vector;
import com.avereon.marea.*;
import com.avereon.marea.geom.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import lombok.CustomLog;

import java.util.Arrays;

@CustomLog
public class FxRenderer2d extends Canvas implements Renderer2d {

	/**
	 * This value needs to be large enough to allow small font heights to be
	 * rendered correctly. This is done by choosing a value that ensures small
	 * font heights multiplied by the FONT_POINT_SIZE is greater than 1.0. This
	 * is because the font engine does not allow font sizes smaller than 1.0. A
	 * value between 1e2 and 1e6 is recommended. The font engine also does not
	 * like really large values.
	 */
	private static final double FONT_POINT_SIZE = 1e4;

	private final Affine screenTransform = new Affine();

	private Affine worldTransform = new Affine( Transform.scale( 1, -1 ) );

	private Affine worldTextTransform = new Affine( Transform.scale( 1, 1 ) );

	private double[] dpi = new double[]{ 96, 96 };

	private double[] viewpoint = new double[]{ 0, 0 };

	private double[] zoom = new double[]{ 1, 1 };

	public FxRenderer2d( double width, double height ) {
		widthProperty().addListener( ( p, o, n ) -> updateWorldTransforms() );
		heightProperty().addListener( ( p, o, n ) -> updateWorldTransforms() );
		resize( width, height );
	}

	@Override
	public void clear() {
		getGraphicsContext2D().setTransform( screenTransform );
		getGraphicsContext2D().clearRect( 0, 0, getWidth(), getHeight() );
	}

	@Override
	public void setDpi( double dpiX, double dpiY ) {
		dpi = new double[]{ dpiX, dpiY };
		updateWorldTransforms();
	}

	@Override
	public void setZoom( double zoomX, double zoomY ) {
		zoom = new double[]{ zoomX, zoomY };
		updateWorldTransforms();
	}

	@Override
	public void setViewpoint( double x, double y ) {
		viewpoint = new double[]{ x, y };
		updateWorldTransforms();
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

	private void updateWorldTransforms() {
		worldTextTransform = updateWorldTextTransform();
		worldTransform = updateWorldTransform();
	}

	private Affine updateWorldTransform() {
		Affine affine = new Affine();

		// Center the origin
		affine.append( Transform.translate( 0.5 * getWidth(), 0.5 * getHeight() ) );

		// Invert the y-axis
		affine.append( Transform.scale( 1, -1 ) );

		// Scale for screen DPI
		affine.append( Transform.scale( dpi[ 0 ], dpi[ 1 ] ) );

		// Apply the zoom factor
		affine.append( Transform.scale( zoom[ 0 ], zoom[ 1 ] ) );

		// Center the viewpoint. The viewpoint is given in world coordinates
		affine.append( Transform.translate( -viewpoint[ 0 ], -viewpoint[ 1 ] ) );

		return affine;
	}

	private Affine updateWorldTextTransform() {
		Affine affine = new Affine();

		// Center the origin
		affine.append( Transform.translate( 0.5 * getWidth(), 0.5 * getHeight() ) );

		// Do NOT invert the y-axis

		// Scale for screen DPI
		affine.append( Transform.scale( dpi[ 0 ], dpi[ 1 ] ) );

		// Apply the zoom factor
		affine.append( Transform.scale( zoom[ 0 ] / FONT_POINT_SIZE, zoom[ 1 ] / FONT_POINT_SIZE ) );

		// Center the viewpoint. The viewpoint is given in world coordinates
		affine.append( Transform.translate( -viewpoint[ 0 ] * FONT_POINT_SIZE, viewpoint[ 1 ] * FONT_POINT_SIZE ) );

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
