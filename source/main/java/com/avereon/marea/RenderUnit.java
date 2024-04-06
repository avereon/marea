package com.avereon.marea;

public enum RenderUnit {
	// Imperial units
	INCH( 1 ),
	FOOT( 12 ),
	MILE( 5280 * 12 ),

	// Metric units
	METER( 1 / 0.0254 ),
	CENTIMETER( 0.01 * METER.conversion ),
	MILLIMETER( 0.001 * METER.conversion ),
	KILOMETER( 1000 * METER.conversion );

	private final double conversion;

	RenderUnit( double conversion ) {
		this.conversion = conversion;
	}

	// To be used internally by the renderer
	public double convert( double value ) {
		return value * conversion;
	}

}
