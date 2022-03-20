package com.avereon.marea.fx;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FxRendererMain extends Application {

	@Override
	public void start( Stage stage ) throws Exception {
		FxRenderer renderer = new FxRenderer();
		renderer.resize( 200, 200 );
		//renderer.setOutputScale( Screen.getPrimary().getOutputScaleX(), Screen.getPrimary().getOutputScaleY() );
		renderer.drawHRule( 10.5, Color.YELLOW, 1 );
		renderer.drawHRule( renderer.getHeight() - 10.5, Color.YELLOW, 1 );
		renderer.drawVRule( 10.5, Color.YELLOW, 1 );
		renderer.drawVRule( renderer.getWidth() - 10.5, Color.YELLOW, 1 );

		Parent container = new BorderPane( renderer );
		Scene scene = new Scene( container, Color.NAVY );
		stage.setScene( scene );

		stage.show();
	}

}
