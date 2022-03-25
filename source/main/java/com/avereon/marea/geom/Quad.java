package com.avereon.marea.geom;

import com.avereon.curve.math.Point;
import com.avereon.marea.Shape2d;
import com.avereon.marea.Shape3d;
import com.avereon.marea.ShapeType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * An immutable bezier cubic curve.
 */
@Data
@RequiredArgsConstructor
public class Quad implements Shape2d, Shape3d {

	/**
	 * The anchor point.
	 */
	private final double[] anchor;

	private final double[] control;

	/**
	 * The vector point.
	 */
	private final double[] vector;

	private final double rotate;

	public Quad( double ax, double ay, double bx, double by, double cx, double cy ) {
		this( Point.of( ax, ay ), Point.of( bx, by ), Point.of( cx, cy ) );
	}

	public Quad( double[] anchor, double[] control, double[] vector ) {
		this( anchor, control, vector, 0.0 );
	}

	@Override
	public ShapeType type() {
		return ShapeType.QUAD;
	}

}
