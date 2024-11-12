package com.avereon.marea.fx;

import com.avereon.marea.RenderUnit;
import com.avereon.marea.test.Point2DAssert;
import javafx.geometry.Point2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class FxRenderer2dTest {

	private FxRenderer2d renderer;

	@BeforeEach
	void setup() {
		FxRenderer2d.logEnabled = true;
		renderer = new FxRenderer2d();
		// These unit tests are simpler using the INCH length unit
		renderer.setLengthUnit( RenderUnit.INCH );
		renderer.setSize( 144, 144 );
	}

	@Test
	void testCreate() {
		assertThat( renderer ).isNotNull();
	}

	@Test
	void testLocalToParentWithDefaultWorldTransform() {
		assertThat( renderer.localToParent( 0, 0 ) ).isEqualTo( new Point2D( 72, 72 ) );
		assertThat( renderer.localToParent( 1, 1 ) ).isEqualTo( new Point2D( 144, 0 ) );
	}

	@Test
	void testParentToLocalWithDefaultWorldTransform() {
		assertThat( renderer.parentToLocal( 72, 72 ) ).isEqualTo( new Point2D( 0, 0 ) );
		assertThat( renderer.parentToLocal( 144, 0 ) ).isEqualTo( new Point2D( 1, 1 ) );
	}

	@ParameterizedTest
	@MethodSource( "localToParent" )
	void testLocalToParentWithDefaultWorldTransformAndRotate(
		double width, double height, double dpiX, double dpiY, double viewpointX, double viewpointY, double zoomX, double zoomY, double rotate, double x, double y, double expectedX, double expectedY
	) {
		renderer.setDpi( dpiX, dpiY );
		renderer.setSize( width, height );
		renderer.setView( viewpointX, viewpointY, rotate, zoomX, zoomY );
		assertThat( renderer.localToParent( x, y ) ).isEqualTo( new Point2D( expectedX, expectedY ) );
	}

	@Test
	void testLengthUnitChangeUpdatesWorldTransform() {
		renderer.setLengthUnit( RenderUnit.CENTIMETER );
		assertThat( renderer.localToParent( 0, 0 ) ).isEqualTo( new Point2D( 72, 72 ) );
		Point2DAssert.assertThat( renderer.localToParent( 2.54, 2.54 ) ).isCloseTo( new Point2D( 144, 0 ) );
	}

	@Test
	void testDpiChangeUpdatesWorldTransform() {
		renderer.setDpi( 144, 144 );
		assertThat( renderer.localToParent( 0, 0 ) ).isEqualTo( new Point2D( 72, 72 ) );
		assertThat( renderer.localToParent( 1, 1 ) ).isEqualTo( new Point2D( 216, -72 ) );
	}

	@Test
	void testZoomChangeUpdatesWorldTransform() {
		renderer.setZoom( 2, 2 );
		assertThat( renderer.localToParent( 0, 0 ) ).isEqualTo( new Point2D( 72, 72 ) );
		assertThat( renderer.localToParent( 1, 1 ) ).isEqualTo( new Point2D( 216, -72 ) );
	}

	@Test
	void testViewpointChangeUpdatesWorldTransform() {
		FxRenderer2d.logEnabled = true;
		renderer.setViewpoint( 2, 2 );

		assertThat( renderer.getWorldToScreenTransform().transform( 2, 2 ) ).isEqualTo( new Point2D( 72, 72 ) );

		// The viewpoint should be in the center of the node
		assertThat( renderer.localToParent( 2, 2 ) ).isEqualTo( new Point2D( 72, 72 ) );
		assertThat( renderer.localToParent( 1, 1 ) ).isEqualTo( new Point2D( 0, 144 ) );
	}

	private static Stream<Arguments> localToParent() {
		double a = 21.08831175456858;
		double b = 122.91168824543142;

		// w, h, dpiX, dpiY, vpX, vpY, zX, zY, rotate, x, y, expectedX, expectedY
		return Stream.of(
			Arguments.of( 144, 144, 72, 72, 2, 2, 1, 1, 0, 2, 2, 72, 72 ),
			Arguments.of( 144, 144, 72, 72, 2, 2, 1, 1, 0, 1, 1, 0, 144 ),
			Arguments.of( 144, 144, 72, 72, 2, 2, 1, 1, 0, 0, 0, -72, 216 ),

			Arguments.of( 144, 144, 72, 72, 1, 1, 1, 1, 0, 2, 2, 144, 0 ),
			Arguments.of( 144, 144, 72, 72, 1, 1, 1, 1, 0, 1, 1, 72, 72 ),
			Arguments.of( 144, 144, 72, 72, 1, 1, 1, 1, 0, 0, 0, 0, 144 ),

			Arguments.of( 144, 144, 72, 72, 0, 0, 1, 1, 0, 0, 0, 72, 72 ),
			Arguments.of( 144, 144, 72, 72, 0, 0, 1, 1, 0, -0.5, -0.5, 36, 108 ),
			Arguments.of( 144, 144, 72, 72, 0, 0, 1, 1, 0, -0.5, 0.5, 36, 36 ),
			Arguments.of( 144, 144, 72, 72, 0, 0, 1, 1, 0, 0.5, 0.5, 108, 36 ),
			Arguments.of( 144, 144, 72, 72, 0, 0, 1, 1, 0, 0.5, -0.5, 108, 108 ),

			Arguments.of( 144, 144, 72, 72, 0, 0, 1, 1, 45, 0, 0, 72, 72 ),
			Arguments.of( 144, 144, 72, 72, 0, 0, 1, 1, 45, -0.5, -0.5, 72, b ),
			Arguments.of( 144, 144, 72, 72, 0, 0, 1, 1, 45, -0.5, 0.5, a, 72 ),
			Arguments.of( 144, 144, 72, 72, 0, 0, 1, 1, 45, 0.5, 0.5, 72, a ),
			Arguments.of( 144, 144, 72, 72, 0, 0, 1, 1, 45, 0.5, -0.5, b, 72 )
		);
	}

}
