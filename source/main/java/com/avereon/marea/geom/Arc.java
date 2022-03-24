package com.avereon.marea.geom;

import com.avereon.curve.math.Point;
import com.avereon.marea.Shape2d;
import com.avereon.marea.Shape3d;
import com.avereon.marea.ShapeType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * An immutable arc.
 */
@Data
@RequiredArgsConstructor
public class Arc implements Shape2d, Shape3d {

	/**
	 * The anchor point.
	 */
	private final double[] anchor;

	/**
	 * The radius point.
	 */
	private final double[] radius;

	/**
	 * The rotation in degrees.
	 */
	private final double rotate;

	/**
	 * The start angle in degrees.
	 */
	private final double start;

	/**
	 * The extent angle in degrees.
	 */
	private final double extent;

	public Arc( double x1, double y1, double x2, double y2, double rotate, double start, double extent ) {
		this( Point.of( x1, y1 ), Point.of( x2, y2 ), rotate, start, extent );
	}

	@Override
	public ShapeType type() {
		return ShapeType.ARC;
	}

}
