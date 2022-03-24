package com.avereon.marea.geom;

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
public class Curve implements Shape2d, Shape3d {

	/**
	 * The anchor point.
	 */
	private final double[] anchor;

	private final double[] anchorControl;

	private final double[] vectorControl;

	/**
	 * The vector point.
	 */
	private final double[] vector;

	private final double rotate;

	public Curve( double[] anchor, double[] anchorControl, double[] vectorControl, double[] vector ) {
		this( anchor, anchorControl, vectorControl, vector, 0.0 );
	}

	@Override
	public ShapeType type() {
		return ShapeType.CURVE;
	}

}
