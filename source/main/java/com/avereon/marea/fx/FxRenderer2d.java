package com.avereon.marea.fx;

import com.avereon.marea.Shape2d;
import com.avereon.marea.Renderer2d;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;
import lombok.CustomLog;

@CustomLog
public class FxRenderer2d extends Canvas implements Renderer2d {

	private final Affine screenTransform = new Affine();

	private Affine worldTransform = new Affine();

	public FxRenderer2d( double width, double height ) {
		setOutputScale( 1, 1 );
		setWidth( width );
		setHeight( height );
	}

	@Override
	public void setOutputScale( double scaleX, double scaleY ) {
		Affine affine = new Affine();
		affine.append( Transform.scale( 1 / scaleX, 1 / scaleX ) );
		worldTransform = affine;
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
	}

	@Override
	public void fill( Shape2d shape, Paint paint ) {
		worldSetup( paint, 0.0 );
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
