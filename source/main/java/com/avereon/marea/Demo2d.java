package com.avereon.marea;

import com.avereon.marea.fx.FxRenderer2d;
import com.avereon.marea.geom.Arc;
import com.avereon.marea.geom.Ellipse;
import com.avereon.marea.geom.Line;
import com.avereon.marea.geom.Text;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Demo2d extends Application {

	@Override
	public void start( Stage stage ) throws Exception {
		Screen screen = Screen.getPrimary();
		double dpi = screen.getDpi();

		FxRenderer2d renderer = new FxRenderer2d( 960, 540 );
		renderer.setDpi( dpi, dpi );
		renderer.setZoom( 5, 5 );
		renderer.setViewpoint( 0.5, 0.1 );

		Parent container = new BorderPane( renderer );
		Scene scene = (new Scene( container, Color.NAVY ));
		stage.setScene( scene );

		stage.show();

		renderer.drawHRule( 10.5, Color.YELLOW, 1 );
		renderer.drawHRule( renderer.getHeight() - 10.5, Color.YELLOW, 1 );
		renderer.drawVRule( 10.5, Color.YELLOW, 1 );
		renderer.drawVRule( renderer.getWidth() - 10.5, Color.YELLOW, 1 );

		//		// Draw an arrow pointing up
		//		renderer.draw( new Line( Point.of( 0, -1 ), Point.of( 0, 1 ) ), Color.GREEN, 0.1 );
		//		renderer.draw( new Line( Point.of( -1, 0 ), Point.of( 0, 1 ) ), Color.GREEN, 0.1 );
		//		renderer.draw( new Line( Point.of( 1, 0 ), Point.of( 0, 1 ) ), Color.GREEN, 0.1 );

		// Draw an airfoil with line segments
		loadAirfoilLines().forEach( l -> renderer.draw( l, Color.BLACK, 0.01 ) );

		// Draw checks
		renderer.draw( new Line( -0.6, -0.1, -0.4, 0.1 ), Color.ORANGE, 0.01 );
		renderer.draw( new Ellipse( -0.5, 0.5, 0.1, 0.1 ), Color.YELLOW, 0.01 );
		renderer.draw( new Ellipse( -0.5, 0.5, 0.1, 0.05, 30 ), Color.ORANGE, 0.01 );
		renderer.draw( new Arc( 0, 0.5, 0.1, 0.1, 0, 0, 135 ), Color.ORANGE, 0.01 );
		renderer.draw( new Text( "Joukowsky", 0.4, 0.4, 0.2, -10 ), Color.ORANGE, 0.01 );

		// Fill checks
		renderer.fill( new Ellipse( -0.5, -0.4, 0.1, 0.1 ), Color.GOLDENROD );
		renderer.fill( new Ellipse( -0.5, -0.4, 0.05, 0.1, -120 ), Color.BROWN );
		renderer.fill( new Text( "Joukowsky", 0.4, -0.5, 0.2, 10 ), Color.BROWN );
	}

	private List<Line> loadAirfoilLines() throws IOException {
		List<Line> lines = new ArrayList<>();
		List<double[]> points = new AirfoilReader().loadPoints( "airfoil.txt" );
		double[] lastPoint = null;
		for( double[] point : points ) {
			if( lastPoint != null ) {
				lines.add( new Line( lastPoint, point ) );
			}
			lastPoint = point;
		}
		return lines;
	}

}

class AirfoilReader {

	public List<double[]> loadPoints( String name ) throws IOException {
		InputStream input = getClass().getClassLoader().getResourceAsStream( name );
		if( input == null ) return List.of();

		List<double[]> points = new ArrayList<>();
		BufferedReader reader = new BufferedReader( new InputStreamReader( input, StandardCharsets.UTF_8 ) );
		String line;
		while( (line = reader.readLine()) != null ) {
			String[] coords = line.trim().split( " +" );
			points.add( new double[]{ Double.parseDouble( coords[ 0 ] ), Double.parseDouble( coords[ 1 ] ) } );
		}

		return points;
	}

}
