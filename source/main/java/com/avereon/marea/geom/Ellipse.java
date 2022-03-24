package com.avereon.marea.geom;

import com.avereon.curve.math.Point;
import com.avereon.marea.Shape2d;
import com.avereon.marea.Shape3d;
import com.avereon.marea.ShapeType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * An immutable ellipse.
 */
@Data
@RequiredArgsConstructor
public class Ellipse implements Shape2d, Shape3d {

	/**
	 * The anchor point, or first point of the line. Practically the order of the
	 * points is uninteresting, however, it can be technically useful.
	 */
	private final double[] anchor;

	/**
	 * The vector point, or second point of the line. Practically the order of the
	 * points is uninteresting, however, it can be technically useful.
	 */
	private final double[] radius;

	private final double rotate;

	public Ellipse( double[] anchor, double[] radius ) {
		this( anchor, radius, 0.0 );
	}

	public Ellipse( double x1, double y1, double x2, double y2 ) {
		this( Point.of( x1, y1 ), Point.of( x2, y2 ) );
	}

	public Ellipse( double x1, double y1, double x2, double y2, double rotate ) {
		this( Point.of( x1, y1 ), Point.of( x2, y2 ), rotate );
	}

	@Override
	public ShapeType type() {
		return ShapeType.ELLIPSE;
	}

}
