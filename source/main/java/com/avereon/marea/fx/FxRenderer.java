package com.avereon.marea.fx;

import com.avereon.marea.Renderer;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

//@CustomLog
public class FxRenderer extends Canvas implements Renderer {

	private Affine screenTransform;

	public FxRenderer() {
		setOutputScale( 1, 1 );
	}

	@Override
	public void setOutputScale( double scaleX, double scaleY ) {
		Affine affine = new Affine();
		affine.append( Transform.scale( 1 / scaleX, 1 / scaleX ) );
		screenTransform = affine;
	}

	@Override
	public void resize( double width, double height ) {
		setWidth( width );
		setHeight( height );
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



	private void screenSetup( Paint paint, double width ) {
		// set transform to screen
		getGraphicsContext2D().setTransform( screenTransform );
		// set paint
		getGraphicsContext2D().setStroke( paint );
		// set width
		getGraphicsContext2D().setLineWidth( width );
	}

}
