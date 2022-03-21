package com.avereon.marea;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * An immutable line.
 */
@Data
@RequiredArgsConstructor
public class Line implements Shape2d, Shape3d {

	/**
	 * The anchor point, or first point of the line. Practically the order of the
	 * points is uninteresting, however, it can be technically useful.
	 */
	private final double[] anchor;

	/**
	 * The vector point, or second point of the line. Practically the order of the
	 * points is uninteresting, however, it can be technically useful.
	 */
	private final double[] vector;

	@Override
	public ShapeType type() {
		return ShapeType.LINE;
	}

}
