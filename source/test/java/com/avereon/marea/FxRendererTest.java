package com.avereon.marea;

import com.avereon.marea.fx.FxRenderer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FxRendererTest {

	@Test
	void testCreate() {
		FxRenderer renderer = new FxRenderer();

		assertThat( renderer ).isNotNull();
	}

}
