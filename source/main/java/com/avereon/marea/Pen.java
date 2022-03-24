package com.avereon.marea;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors( fluent = true )
public class Pen {

	private Paint paint = Color.BLACK;

	private double width = 1.0;

	private LineCap cap = LineCap.ROUND;

	private LineJoin join = LineJoin.ROUND;

	private double[] dashes = new double[]{};

	private double offset = 0.0;

	public Pen( Paint paint ) {
		this.paint = paint;
	}

	public Pen( double width ) {
		this.width = width;
	}

	public Pen( Paint paint, double width ) {
		this.paint = paint;
		this.width = width;
	}
}
