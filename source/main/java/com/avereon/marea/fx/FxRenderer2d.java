package com.avereon.marea.fx;

import com.avereon.marea.Line;
import com.avereon.marea.Renderer2d;
import com.avereon.marea.Shape2d;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import lombok.CustomLog;

@CustomLog
public class FxRenderer2d extends Canvas implements Renderer2d {

	private final Affine screenTransform = new Affine();

	private Affine worldTransform = new Affine( Transform.scale( 1, -1 ) );

	private double[] dpi = new double[]{ 96, 96 };

	private double[] viewpoint = new double[]{ 0, 0 };

	private double[] outputScale = new double[]{ 1, 1 };

	public FxRenderer2d( double width, double height ) {
		setWidth( width );
		setHeight( height );
	}

	@Override
	public void setDpi( double dpiX, double dpiY ) {
		dpi = new double[]{ dpiX, dpiY };
		updateWorldTransform();
	}

	@Override
	public void setViewpoint( double x, double y ) {
		viewpoint = new double[]{ x, y };
		updateWorldTransform();
	}

	@Override
	public void setOutputScale( double scaleX, double scaleY ) {
		outputScale = new double[]{ scaleX, scaleY };
		updateWorldTransform();
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
		worldSetup( paint, width );
		switch( shape.type() ) {
			case LINE -> {
				Line line = (Line)shape;
				double[] anchor = line.getAnchor();
				double[] vector = line.getVector();
				getGraphicsContext2D().strokeLine( anchor[ 0 ], anchor[ 1 ], vector[ 0 ], vector[ 1 ] );
			}
			case CIRCLE -> {}
		}
	}

	@Override
	public void fill( Shape2d shape, Paint paint ) {
		worldSetup( paint, 0.0 );

		switch( shape.type() ) {
			case LINE -> {
				// Lines cannot be filled
			}
			case CIRCLE -> {}
		}
	}

	private void updateWorldTransform() {
		// FIXME This assumes a viewpoint of 0,0
		Affine affine = new Affine( Transform.scale( 1 / outputScale[ 0 ], -1 / outputScale[ 1 ] ) );
		affine.append( Transform.translate( 0.5 * getWidth(), -0.5 * getHeight() ) );
		affine.append( Transform.scale( dpi[ 0 ], dpi[ 1 ] ) );
		affine.append( Transform.translate( -viewpoint[ 0 ], -viewpoint[ 1 ] ) );
		worldTransform = affine;
	}

	private void screenSetup( Paint paint, double width ) {
		// set transform to screen
		getGraphicsContext2D().setTransform( screenTransform );
		// set paint
		getGraphicsContext2D().setStroke( paint );
		// set width
		getGraphicsContext2D().setLineWidth( width );
	}

	private void worldSetup( Paint paint, double width ) {
		// set transform to screen
		getGraphicsContext2D().setTransform( worldTransform );
		// set paint
		getGraphicsContext2D().setStroke( paint );
		// set width
		getGraphicsContext2D().setLineWidth( width );
	}

}
