package com.avereon.marea;

import com.avereon.marea.fx.FxRenderer2d;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FxRenderer2dTest {

	private FxRenderer2d renderer;

	@BeforeEach
	void setup() {
		renderer = new FxRenderer2d( 200, 200 );
		// These unit tests are simpler using the INCH length unit
		renderer.setLengthUnit( LengthUnit.INCH );
	}

	@Test
	void testCreate() {
		assertThat( renderer ).isNotNull();
	}

	@Test
	void testDefaultWorldTransform() {
		assertThat( renderer.localToParent( 1, 1 ) ).isEqualTo( new Point2D( 196, 4 ) );
	}

	@Test
	void testLengthUnitChangeUpdatesWorldTransform() {
		double p = LengthUnit.CENTIMETER.convert( 96.0 );
		renderer.setLengthUnit( LengthUnit.CENTIMETER );
		assertThat( renderer.localToParent( 1, 1 ) ).isEqualTo( new Point2D( 100 + p, 100 - p ) );
	}

	@Test
	void testDpiChangeUpdatesWorldTransform() {
		renderer.setDpi( 72, 72 );
		assertThat( renderer.localToParent( 1, 1 ) ).isEqualTo( new Point2D( 172, 28 ) );
	}

	@Test
	void testZoomChangeUpdatesWorldTransform() {
		renderer.setZoom( 2, 2 );
		assertThat( renderer.localToParent( 1, 1 ) ).isEqualTo( new Point2D( 292, -92 ) );
	}

	@Test
	void testViewpointChangeUpdatesWorldTransform() {
		renderer.setViewpoint( -1, 1 );
		assertThat( renderer.localToParent( 1, 1 ) ).isEqualTo( new Point2D( 292, 100 ) );
	}

}
