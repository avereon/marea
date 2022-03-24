package com.avereon.marea.geom;

import com.avereon.curve.math.Point;
import com.avereon.marea.Shape2d;
import com.avereon.marea.Shape3d;
import com.avereon.marea.ShapeType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Immutable text.
 */
@Data
@RequiredArgsConstructor
public class Text implements Shape2d, Shape3d {

	/**
	 * The text to render.
	 */
	private final String text;

	/**
	 * The anchor point.
	 */
	private final double[] anchor;

	/**
	 * The text font height.
	 */
	private final double height;

	/**
	 * The text rotation in degrees.
	 */
	private final double rotate;

	public Text( String text, double[] anchor, double height ) {
		this( text, anchor, height, 0.0 );
	}

	public Text( String text, double x1, double y1, double height ) {
		this( text, Point.of( x1, y1 ), height );
	}

	public Text( String text, double x1, double y1, double height, double rotate ) {
		this( text, Point.of( x1, y1 ), height, rotate );
	}

	@Override
	public ShapeType type() {
		return ShapeType.TEXT;
	}

}
