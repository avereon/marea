package com.avereon.marea;

import com.avereon.marea.fx.FxRenderer2d;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FxRendererTest2D {

	@Test
	void testCreate() {
		FxRenderer2d renderer = new FxRenderer2d();

		assertThat( renderer ).isNotNull();
	}

}
