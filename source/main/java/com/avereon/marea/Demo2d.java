package com.avereon.marea;

import com.avereon.curve.math.Point;
import com.avereon.marea.fx.FxRenderer2d;
import com.avereon.marea.geom.*;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.CustomLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@CustomLog
public class Demo2d extends Application {

	long FREQ_60_HZ = 1000 / 60;

	@Override
	public void start( Stage stage ) throws Exception {
		Screen screen = Screen.getPrimary();
		double dpi = screen.getDpi();

		FxRenderer2d renderer = new FxRenderer2d( 960, 540 );
		renderer.setDpi( dpi, dpi );
		renderer.setZoom( 10, 10 );
		renderer.setViewpoint( 0.5, 0.0 );
		renderer.setViewRotate( 45 );

		renderer.zoomXProperty().addListener( ( p, o, n ) -> staticRender( renderer ) );
		renderer.zoomYProperty().addListener( ( p, o, n ) -> staticRender( renderer ) );
		renderer.viewpointXProperty().addListener( ( p, o, n ) -> staticRender( renderer ) );
		renderer.viewpointYProperty().addListener( ( p, o, n ) -> staticRender( renderer ) );
		renderer.widthProperty().addListener( ( p, o, n ) -> staticRender( renderer ) );
		renderer.heightProperty().addListener( ( p, o, n ) -> staticRender( renderer ) );

		Parent container = new BorderPane( renderer );
		Scene scene = (new Scene( container, Color.NAVY ));
		stage.setScene( scene );
		stage.show();

		scene.widthProperty().addListener( ( p, o, n ) -> renderer.setWidth( n.doubleValue() ) );
		scene.heightProperty().addListener( ( p, o, n ) -> renderer.setHeight( n.doubleValue() ) );

		//Fx.run( () -> this.staticRender( renderer ) );

		renderer.clear();
		staticRender( renderer );

		//		Thread runner = new Thread( () -> {
		//			final AtomicLong counter = new AtomicLong();
		//			while( true ) {
		//				Fx.run( () -> {
		//					renderer.clear();
		//					dynamicRender( renderer, counter.getAndIncrement() );
		//					staticRender( renderer );
		//				} );
		//				try {
		//					Thread.sleep( FREQ_60_HZ );
		//				} catch( InterruptedException e ) {
		//					e.printStackTrace();
		//				}
		//			}
		//		} );
		//		runner.setDaemon( true );
		//		runner.start();
	}

	private void staticRender( Renderer2d renderer ) {
		renderer.clear();

		Pen outlinePen = new Pen( Color.YELLOW, 1 );
		renderer.drawHRule( 10.5, outlinePen );
		renderer.drawHRule( renderer.getHeight() - 10.5, outlinePen );
		renderer.drawVRule( 10.5, outlinePen );
		renderer.drawVRule( renderer.getWidth() - 10.5, outlinePen );

		//		// Draw an arrow pointing up
		//		renderer.draw( new Line( Point.of( 0, -1 ), Point.of( 0, 1 ) ), Color.GREEN, 0.1 );
		//		renderer.draw( new Line( Point.of( -1, 0 ), Point.of( 0, 1 ) ), Color.GREEN, 0.1 );
		//		renderer.draw( new Line( Point.of( 1, 0 ), Point.of( 0, 1 ) ), Color.GREEN, 0.1 );

		// Draw an airfoil with line segments
		//loadAirfoilLines().forEach( l -> renderer.draw( l, new Pen( 0.01 ) ) );

		// Draw the airfoil with a path
		Path airfoil = new Path( 1, 0 );
		try {
			loadAirfoilLines().forEach( l -> airfoil.line( l.getVector() ) );
		} catch( IOException e ) {
			e.printStackTrace();
		}

		double size = 0.01;

		Pen orangePen = new Pen( Color.ORANGE, size );
		Pen yellowPen = new Pen( Color.YELLOW, size );
		Pen goldenPen = new Pen( Color.GOLDENROD, size );
		Pen brownPen = new Pen( Color.BROWN, size );

		// Draw origin
		renderer.draw( new Line( -0.1, 0, 0.1, 0 ), yellowPen );
		renderer.draw( new Line( 0, -0.1, 0, 0.1 ), yellowPen );

		// Draw checks
		renderer.draw( new Line( -0.6, -0.1, -0.4, 0.1 ), orangePen );
		renderer.draw( new Ellipse( -0.5, 0.5, 0.1, 0.1 ), yellowPen );
		renderer.draw( new Ellipse( -0.5, 0.5, 0.1, 0.05, 30 ), orangePen );
		renderer.draw( new Arc( 0, 0.5, 0.1, 0.1, 0, 0, 135 ), orangePen );
		renderer.draw( new Text( "Joukowsky", 0.4, 0.4, 0.2, -10 ), orangePen );
		renderer.draw( new Curve( Point.of( 0.9, 0.4 ), Point.of( 0.9, 0.6 ), Point.of( 1.1, 0.4 ), Point.of( 1.1, 0.6 ) ), orangePen );
		renderer.draw( new Quad( 1.4, 0.4, 1.5, 0.6, 1.6, 0.4 ), orangePen );
		renderer.draw( airfoil, new Pen( Color.CYAN, 0.01 ) );

		// Fill checks
		renderer.fill( new Ellipse( -0.5, -0.4, 0.1, 0.1 ), goldenPen );
		renderer.fill( new Ellipse( -0.5, -0.4, 0.05, 0.1, -120 ), brownPen );
		renderer.fill( new Text( "Joukowsky", 0.4, -0.5, 0.2, 10 ), brownPen );
		renderer.fill( airfoil, new Pen( Color.BLACK ) );
	}

	private void dynamicRender( Renderer2d renderer, long counter ) {
		long start = System.nanoTime();

		Random random = new Random();
		int width = (int)renderer.getWidth();
		int height = (int)renderer.getHeight();
		Pen pen = new Pen( Color.YELLOW );

		// Random lines
		//		for( int index = 0; index < 1000000; index++ ) {
		//			//renderer.clear();
		//			boolean even = index % 2 == 0;
		//			if( even ) {
		//				renderer.drawHRule( random.nextInt( height ), pen );
		//			} else {
		//				renderer.drawVRule( random.nextInt( width ), pen );
		//			}
		//		}

		// Scrolling lines
		//		int count = (width / 3);
		//		while( true ) {
		//			for( int index = 0; index < 3; index++ ) {
		//				renderer.clear();
		//				for( int colIndex = 0; colIndex < count; colIndex++ ) {
		//					renderer.drawVRule( 6 * colIndex + 2*index, pen );
		//				}
		//				ThreadUtil.pause( 1000 / 30 );
		//			}
		//		}

		// Scrolling line
		renderer.drawVRule( counter % width, pen );

		//		long end = System.nanoTime();
		//
		//		System.out.println( "duration=" + (end - start) + " nanos" );
	}

	private List<Line> loadAirfoilLines() throws IOException {
		List<Line> lines = new ArrayList<>();
		List<double[]> points = new PathReader().loadPoints( "path.txt" );
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

class PathReader {

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
