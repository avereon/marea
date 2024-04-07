package com.avereon.marea;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors( fluent = true )
public class Pen {

	// TODO This class uses FX Paint and Color. Create and use Marea paint and color.
	private Paint paint = Color.BLACK;

	private double width = 1.0;

	private LineCap cap = LineCap.ROUND;

	private LineJoin join = LineJoin.ROUND;

	private double[] dashes = new double[]{};

	private double offset = 0.0;

	public Pen() {}

	public Pen( Paint paint ) {
		this( paint, 1.0 );
	}

	public Pen( double width ) {
		this( Color.BLACK, width );
	}

	public Pen( Paint paint, double width ) {
		this.paint = paint;
		this.width = width;
	}

}
