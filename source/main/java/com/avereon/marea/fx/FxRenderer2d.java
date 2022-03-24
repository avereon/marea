package com.avereon.marea.fx;

import com.avereon.curve.math.Point;
import com.avereon.curve.math.Vector;
import com.avereon.marea.Renderer2d;
import com.avereon.marea.Shape2d;
import com.avereon.marea.geom.Arc;
import com.avereon.marea.geom.Ellipse;
import com.avereon.marea.geom.Line;
import com.avereon.marea.geom.Text;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import lombok.CustomLog;

@CustomLog
public class FxRenderer2d extends Canvas implements Renderer2d {

	/**
	 * This value needs to be large enough to allow small font heights to be
	 * rendered correctly. This is done by choosing a value that ensure the small
	 * font height multiplied by the FONT_POINT_SIZE to be greater than 1.0. This
	 * is because the font engine does not allow font sizes smaller than 1.0. A
	 * value between 1e2 and 1e6 recommended. The font engine also does not like
	 * really large values.
	 */
	private static final double FONT_POINT_SIZE = 1e4;

	private final Affine screenTransform = new Affine();

	private Affine worldTransform = new Affine( Transform.scale( 1, -1 ) );

	private Affine worldTextTransform = new Affine( Transform.scale( 1, 1 ) );

	private double[] dpi = new double[]{ 96, 96 };

	private double[] viewpoint = new double[]{ 0, 0 };

	private double[] zoom = new double[]{ 1, 1 };

	public FxRenderer2d( double width, double height ) {
		setWidth( width );
		setHeight( height );
		updateWorldTransforms();
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
	public void drawHRule( double position, Paint paint, double width ) {
		screenSetup( paint, width );
		getGraphicsContext2D().strokeLine( 0, position, getWidth(), position );
	}

	@Override
	public void drawVRule( double position, Paint paint, double width ) {
		screenSetup( paint, width );
		getGraphicsContext2D().strokeLine( position, 0, position, getHeight() );
	}

	@Override
	public void draw( Shape2d shape, Paint paint, double width ) {
		switch( shape.type() ) {
			case ARC -> drawArc( (Arc)shape, paint, width );
			case LINE -> drawLine( (Line)shape, paint, width );
			case ELLIPSE -> drawEllipse( (Ellipse)shape, paint, width );
			case TEXT -> drawText( (Text)shape, paint, width );
		}
	}

	private void drawArc( Arc arc, Paint paint, double width ) {
		double[] anchor = Vector.add( arc.getAnchor(), Point.of( -arc.getRadius()[ 0 ], -arc.getRadius()[ 1 ] ) );
		double[] radius = Vector.scale( arc.getRadius(), 2 );

		worldSetup( arc, paint, width );
		getGraphicsContext2D().strokeArc( anchor[ 0 ], anchor[ 1 ], radius[ 0 ], radius[ 1 ], arc.getStart(), -arc.getExtent(), ArcType.OPEN );
	}

	private void drawEllipse( Ellipse ellipse, Paint paint, double width ) {
		double[] anchor = Vector.add( ellipse.getAnchor(), Point.of( -ellipse.getRadius()[ 0 ], -ellipse.getRadius()[ 1 ] ) );
		double[] radius = Vector.scale( ellipse.getRadius(), 2 );

		worldSetup( ellipse, paint, width );
		getGraphicsContext2D().strokeOval( anchor[ 0 ], anchor[ 1 ], radius[ 0 ], radius[ 1 ] );
	}

	private void drawLine( Line line, Paint paint, double width ) {
		double[] anchor = line.getAnchor();
		double[] vector = line.getVector();

		worldSetup( line, paint, width );
		getGraphicsContext2D().strokeLine( anchor[ 0 ], anchor[ 1 ], vector[ 0 ], vector[ 1 ] );
	}

	private void drawText( Text text, Paint paint, double width ) {
		double[] anchor = text.getAnchor();

		getGraphicsContext2D().setTransform( rotate( worldTextTransform, -text.getRotate(), Vector.scale( anchor, FONT_POINT_SIZE, -FONT_POINT_SIZE ) ) );
		getGraphicsContext2D().setStroke( paint );
		getGraphicsContext2D().setLineWidth( width * FONT_POINT_SIZE );
		//getGraphicsContext2D().setTextBaseline( VPos.BASELINE );
		getGraphicsContext2D().setFont( new Font( text.getHeight() * FONT_POINT_SIZE ) );
		getGraphicsContext2D().strokeText( text.getText(), anchor[ 0 ] * FONT_POINT_SIZE, -anchor[ 1 ] * FONT_POINT_SIZE );

		// A reference line at the beginning of the text
		//getGraphicsContext2D().strokeLine( anchor[ 0 ] * FONT_POINT_SIZE, -anchor[ 1 ] * FONT_POINT_SIZE, anchor[ 0 ] * FONT_POINT_SIZE, (-anchor[ 1 ] - text.getHeight()) * FONT_POINT_SIZE );
	}

	@Override
	public void fill( Shape2d shape, Paint paint ) {
		switch( shape.type() ) {
			case LINE -> {
				// Lines cannot be filled
			}
			case ELLIPSE -> fillEllipse( (Ellipse)shape, paint );
			case TEXT -> fillText( (Text)shape, paint );
		}
	}

	private void fillEllipse( Ellipse ellipse, Paint paint ) {
		double[] anchor = Vector.add( ellipse.getAnchor(), Point.of( -ellipse.getRadius()[ 0 ], -ellipse.getRadius()[ 1 ] ) );
		double[] radius = Vector.scale( ellipse.getRadius(), 2 );

		worldSetup( ellipse, paint, 0.0 );
		getGraphicsContext2D().setFill( paint );
		getGraphicsContext2D().fillOval( anchor[ 0 ], anchor[ 1 ], radius[ 0 ], radius[ 1 ] );
	}

	private void fillText( Text text, Paint paint ) {
		double[] anchor = text.getAnchor();

		getGraphicsContext2D().setTransform( rotate( worldTextTransform, -text.getRotate(), Vector.scale( anchor, FONT_POINT_SIZE, -FONT_POINT_SIZE ) ) );
		getGraphicsContext2D().setFill( paint );
		//getGraphicsContext2D().setTextBaseline( VPos.BASELINE );
		getGraphicsContext2D().setFont( new Font( text.getHeight() * FONT_POINT_SIZE ) );
		getGraphicsContext2D().fillText( text.getText(), anchor[ 0 ] * FONT_POINT_SIZE, -anchor[ 1 ] * FONT_POINT_SIZE );

		// A reference line at the beginning of the text
		//getGraphicsContext2D().strokeLine( anchor[ 0 ] * FONT_POINT_SIZE, -anchor[ 1 ] * FONT_POINT_SIZE, anchor[ 0 ] * FONT_POINT_SIZE, (-anchor[ 1 ] - text.getHeight()) * FONT_POINT_SIZE );
	}

	private void updateWorldTransforms() {
		updateWorldTransform();
		updateWorldTextTransform();
	}

	private void updateWorldTransform() {
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

		worldTransform = affine;
	}

	private void updateWorldTextTransform() {
		Affine affine = new Affine();

		// Center the origin
		affine.append( Transform.translate( 0.5 * getWidth(), 0.5 * getHeight() ) );

		// Do NOT invert the y-axis
		//textAffine.append( Transform.scale( 1.0, 1.0 ) );

		// Scale for screen DPI
		affine.append( Transform.scale( dpi[ 0 ], dpi[ 1 ] ) );

		//		// Apply the zoom factor
		affine.append( Transform.scale( zoom[ 0 ] / FONT_POINT_SIZE, zoom[ 1 ] / FONT_POINT_SIZE ) );

		// Center the viewpoint. The viewpoint is given in world coordinates
		affine.append( Transform.translate( -viewpoint[ 0 ] * FONT_POINT_SIZE, viewpoint[ 1 ] * FONT_POINT_SIZE ) );

		worldTextTransform = affine;
	}

	private Affine rotate( Affine transform, double rotate, double[] anchor ) {
		Affine affine = new Affine();
		affine.append( transform );
		affine.appendRotation( rotate, anchor[ 0 ], anchor[ 1 ] );
		return affine;
	}

	private void screenSetup( Paint paint, double width ) {
		// set transform to screen
		getGraphicsContext2D().setTransform( screenTransform );
		// set paint
		getGraphicsContext2D().setStroke( paint );
		// set width
		getGraphicsContext2D().setLineWidth( width );
	}

	private void worldSetup( Shape2d shape, Paint paint, double width ) {
		// set transform to screen
		getGraphicsContext2D().setTransform( rotate( worldTransform, shape.getRotate(), shape.getAnchor() ) );
		// set paint
		getGraphicsContext2D().setStroke( paint );
		// set width
		getGraphicsContext2D().setLineWidth( width );
	}

}
