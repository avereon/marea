package com.avereon.marea.test;

import com.avereon.curve.math.Arithmetic;
import javafx.geometry.Point3D;
import org.assertj.core.data.Offset;

import static com.avereon.curve.math.Arithmetic.DEFAULT_DIGITS;

public class TestUtil {

	public static final double RESOLUTION_LENGTH = 1e-6;

	public static final double RESOLUTION_NORMAL = 1e-10;

	public static final double RESOLUTION_ANGLE = Math.atan( RESOLUTION_NORMAL );

	public static final double RESOLUTION_SMOOTH = 1e-3;

	public static final Offset<Double> TOLERANCE = Offset.offset( Arithmetic.DEFAULT_PRECISION );

	public static final Offset<Double> LOOSE_TOLERANCE = Offset.offset( 1.0 / Math.pow( 10, 0.5 * DEFAULT_DIGITS ) );

	public static double distance( double x1, double y1, double x2, double y2 ) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		return Math.sqrt( dx * dx + dy * dy );
	}

	public static double distance( Point3D a, Point3D b ) {
		return distance( a.getX(), a.getY(), b.getX(), b.getY() );
	}

}
