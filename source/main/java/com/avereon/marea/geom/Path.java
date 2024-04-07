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

	public enum Command {
		ARC,
		CLOSE,
		CURVE,
		LINE,
		MOVE,
		QUAD,
	}

	/**
	 * The anchor point.
	 */
	private final double[] anchor;

	private final double rotate;

	private final List<Element> elements;

	private final boolean closed;

	public Path( double x, double y ) {
		this( x, y, 0.0 );
	}

	public Path( double x, double y, double rotate ) {
		this( Point.of( x, y ), rotate );
	}

	public Path( double[] anchor, double rotate ) {
		this( anchor, rotate, false );
	}

	public Path( double[] anchor, double rotate, boolean closed ) {
		this.anchor = anchor;
		this.rotate = rotate;
		this.elements = new ArrayList<>();
		this.closed = closed;
		move( anchor[ 0 ], anchor[ 1 ] );
	}

	@Override
	public ShapeType type() {
		return ShapeType.PATH;
	}

	public Path line( double x, double y ) {
		return line( Point.of( x, y ) );
	}

	public Path line( double[] point ) {
		elements.add( new Element( Command.LINE, point ) );
		return this;
	}

	public Path arc( double x, double y, double rx, double ry, double start, double extent ) {
		elements.add( new Element( Command.ARC, new double[]{ x, y, rx, ry, start, extent } ) );
		return this;
	}

	public Path quad( double bx, double by, double cx, double cy ) {
		elements.add( new Element( Command.QUAD, new double[]{ bx, by, cx, cy } ) );
		return this;
	}

	public Path close() {
		elements.add( new Element( Command.CLOSE, new double[]{} ) );
		return this;
	}

	public Path curve( double bx, double by, double cx, double cy, double dx, double dy ) {
		elements.add( new Element( Command.CURVE, new double[]{ bx, by, cx, cy, dx, dy } ) );
		return this;
	}

	public Path move( double bx, double by ) {
		elements.add( new Element( Command.MOVE, new double[]{ bx, by } ) );
		return this;
	}

	public record Element(Command command, double[] data) {}

}
