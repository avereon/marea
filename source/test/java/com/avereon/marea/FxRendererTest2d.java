package com.avereon.marea;

import com.avereon.marea.fx.FxRenderer2d;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FxRendererTest2d {

	@Test
	void testCreate() {
		FxRenderer2d renderer = new FxRenderer2d( 200, 200 );

		assertThat( renderer ).isNotNull();
	}

}
