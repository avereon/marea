package com.avereon.marea.fx;

import com.avereon.marea.RenderUnit;
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
		renderer.setLengthUnit( RenderUnit.INCH );
	}

	@Test
	void testCreate() {
		assertThat( renderer ).isNotNull();
	}

	@Test
	void testLocalToParentWithDefaultWorldTransform() {
		assertThat( renderer.localToParent( 1, 1 ) ).isEqualTo( new Point2D( 172, 28 ) );
	}

	@Test
	void testParentToLocalWithDefaultWorldTransform() {
		assertThat( renderer.parentToLocal( 196, 4 ) ).isEqualTo( new Point2D( 1.333333333333333, 1.3333333333333333 ) );
	}

	@Test
	void testLengthUnitChangeUpdatesWorldTransform() {
		double p = RenderUnit.CENTIMETER.convert( FxRenderer2d.DEFAULT_DPI );
		renderer.setLengthUnit( RenderUnit.CENTIMETER );
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
		assertThat( renderer.localToParent( 1, 1 ) ).isEqualTo( new Point2D( 244, -44 ) );
	}

	@Test
	void testViewpointChangeUpdatesWorldTransform() {
		renderer.setViewpoint( -1, 1 );
		assertThat( renderer.localToParent( 1, 1 ) ).isEqualTo( new Point2D( 244, 100 ) );
	}

}
