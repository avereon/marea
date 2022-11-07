package com.avereon.marea;

public enum LengthUnit {
	MILLIMETER( 1/25.4 ),
	CENTIMETER( 1/2.54 ),
	METER( CENTIMETER.conversion* 0.01 ),
	KILOMETER( METER.conversion * 0.001 ),
	INCH( 1 ),
	FOOT( 12 ),
	MILE( 5280 * 12 );

	private double conversion;

	LengthUnit( double conversion ) {
		this.conversion = conversion;
	}

	public double convert( double value ) {
		return value * conversion;
	}

}
