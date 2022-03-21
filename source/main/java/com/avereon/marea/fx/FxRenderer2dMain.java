package com.avereon.marea.fx;

import com.avereon.curve.math.Point;
import com.avereon.marea.Line;
import com.avereon.marea.Shape2d;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class FxRenderer2dMain extends Application {

	@Override
	public void start( Stage stage ) throws Exception {
		Screen screen = Screen.getPrimary();
		double dpi = screen.getDpi();

		FxRenderer2d renderer = new FxRenderer2d( 200, 200 );
		renderer.setDpi( dpi, dpi );
		renderer.setViewpoint( 0.5, 0.5 );
		renderer.setOutputScale( screen.getOutputScaleX(), screen.getOutputScaleY() );

		Parent container = new BorderPane( renderer );
		Scene scene = new Scene( container, Color.NAVY );
		stage.setScene( scene );

		stage.show();

		renderer.drawHRule( 10.5, Color.YELLOW, 1 );
		renderer.drawHRule( renderer.getHeight() - 10.5, Color.YELLOW, 1 );
		renderer.drawVRule( 10.5, Color.YELLOW, 1 );
		renderer.drawVRule( renderer.getWidth() - 10.5, Color.YELLOW, 1 );

		// Draw an arrow pointing up
		renderer.draw( new Line( Point.of( 0, -1 ), Point.of( 0, 1 ) ), Color.GREEN, 0.1 );
		renderer.draw( new Line( Point.of( -1, 0 ), Point.of( 0, 1 ) ), Color.GREEN, 0.1 );
		renderer.draw( new Line( Point.of( 1, 0 ), Point.of( 0, 1 ) ), Color.GREEN, 0.1 );
	}

}
