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

	private final List<Step> steps;

	private final boolean closed;

	public Path() {
		this( null, 0.0, false );
	}

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
		this.closed = closed;
		this.steps = new ArrayList<>();
		if( anchor != null ) move( anchor[ 0 ], anchor[ 1 ] );
	}

	@Override
	public ShapeType type() {
		return ShapeType.PATH;
	}

	public Path move( double bx, double by ) {
		steps.add( new Step( Command.MOVE, new double[]{ bx, by } ) );
		return this;
	}

	public Path line( double x, double y ) {
		return line( Point.of( x, y ) );
	}

	public Path line( double[] point ) {
		steps.add( new Step( Command.LINE, point ) );
		return this;
	}

	@Deprecated
	public Path arc( double x, double y, double rx, double ry, double start, double extent ) {
		steps.add( new Step( Command.ARC, new double[]{ x, y, rx, ry, start, extent } ) );
		return this;
	}

	public Path arc( double rx, double ry, double rotate, double x, double y, boolean largeArc, boolean sweep ) {
		//elements.add( new Element( Command.ARC, new double[]{ x, y, rx, ry, start, extent } ) );
		return this;
	}

	public Path curve( double bx, double by, double cx, double cy, double dx, double dy ) {
		steps.add( new Step( Command.CURVE, new double[]{ bx, by, cx, cy, dx, dy } ) );
		return this;
	}

	public Path quad( double bx, double by, double cx, double cy ) {
		steps.add( new Step( Command.QUAD, new double[]{ bx, by, cx, cy } ) );
		return this;
	}

	public Path close() {
		steps.add( new Step( Command.CLOSE, new double[]{} ) );
		return this;
	}

	// FIXME Do I need to add the largeArc and sweep parameters to Element for the arc command?
	public record Step(Command command, double[] data) {}

}
