module com.avereon.marea {

	// Compile-time only
	requires static lombok;

	// Both compile-time and run-time
	requires com.avereon.curve;
	requires com.avereon.zevra;

	requires java.logging;
	requires javafx.base;
	requires javafx.graphics;
	requires javafx.controls;
	requires com.avereon.zarra;

	//requires org.assertj.core;
	//requires org.junit.jupiter.api;
	//requires org.testfx;
	//requires org.testfx.junit5;

	opens com.avereon.marea.fx to javafx.graphics;

	exports com.avereon.marea;
	opens com.avereon.marea to javafx.graphics;
	exports com.avereon.marea.geom;
	opens com.avereon.marea.geom to javafx.graphics;

}
