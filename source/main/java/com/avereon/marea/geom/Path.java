package com.avereon.marea.geom;

import com.avereon.curve.math.Point;
import com.avereon.marea.Shape2d;
import com.avereon.marea.Shape3d;
import com.avereon.marea.ShapeType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * An immutable path.
 */
@Data
@RequiredArgsConstructor
public class Path implements Shape2d, Shape3d {

	/**
	 * The anchor point.
	 */
	private final double[] anchor;

	private final double rotate;

	private final List<Element> elements = new ArrayList<>();

	public Path( double x, double y ) {
		this( x, y, 0.0 );
	}

	public Path( double x, double y, double rotate ) {
		this( Point.of( x, y ), rotate );
	}

	@Override
	public ShapeType type() {
		return ShapeType.PATH;
	}

	public Path line( double x, double y ) {
		return line( Point.of( x, y ) );
	}

	public Path line( double[] point ) {
		elements.add( new Element( ShapeType.LINE, point ) );
		return this;
	}

	public Path arc( double x, double y, double rx, double ry, double start, double extent ) {
		elements.add( new Element( ShapeType.ARC, new double[]{ x, y, rx, ry, start, extent } ) );
		return this;
	}

	public Path quad( double bx, double by, double cx, double cy ) {
		elements.add( new Element( ShapeType.QUAD, new double[]{ bx, by, cx, cy } ) );
		return this;
	}

	public Path curve( double bx, double by, double cx, double cy, double dx, double dy ) {
		elements.add( new Element( ShapeType.CURVE, new double[]{ bx, by, cx, cy, dx, dy } ) );
		return this;
	}

	@Data
	@RequiredArgsConstructor
	public static class Element {

		private final ShapeType type;

		private final double[] data;

	}

}
