package com.avereon.marea;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RenderUnitTest {

	@Test
	void testMeter() {
		assertThat( RenderUnit.METER.convert( 1 ) ).isEqualTo( 39.37007874015748 );
	}

	@Test
	void testCentimeter() {
		assertThat( RenderUnit.CENTIMETER.convert( 1 ) ).isEqualTo( 0.3937007874015748 );
	}

	@Test
	void testMillimeter() {
		assertThat( RenderUnit.MILLIMETER.convert( 1 ) ).isEqualTo( 0.03937007874015748 );
	}

	@Test
	void testInch() {
		assertThat( RenderUnit.INCH.convert( 1 ) ).isEqualTo( 1.0 );
	}

}
