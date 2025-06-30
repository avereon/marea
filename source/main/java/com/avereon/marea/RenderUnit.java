package com.avereon.marea;

public enum RenderUnit {
	// Imperial units
	IN( 1 ),
	FT( 12 ),
	MI( 5280 * 12 ),

	// Metric units
	M( 1 / 0.0254 ),
	CM( 0.01 * M.conversion ),
	MM( 0.001 * M.conversion ),
	KM( 1000 * M.conversion );

	private final double conversion;

	RenderUnit( double conversion ) {
		this.conversion = conversion;
	}

	// To be used internally by the renderer
	public double convert( double value ) {
		return value * conversion;
	}

}
