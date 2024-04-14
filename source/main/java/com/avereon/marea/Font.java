package com.avereon.marea;

import com.avereon.zarra.font.FontUtil;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import lombok.Getter;

@Getter
public class Font {

	public enum Weight {
		THIN,
		EXTRA_LIGHT,
		LIGHT,
		NORMAL,
		MEDIUM,
		SEMI_BOLD,
		BOLD,
		EXTRA_BOLD,
		BLACK
	}

	public enum Posture {
		REGULAR,
		ITALIC
	}

	private final String name;

	private final Weight weight;

	private final Posture posture;

	private final double size;

	public Font() {
		this( "", Weight.NORMAL, Posture.REGULAR, 12 );
	}

	public Font( String name ) {
		this( name, Weight.NORMAL, Posture.REGULAR, 12 );
	}

	public Font( String name, double size ) {
		this( name, Weight.NORMAL, Posture.REGULAR, size );
	}

	public Font( String name, Weight weight, Posture posture, double size ) {
		this.name = name;
		this.weight = weight;
		this.posture = posture;
		this.size = size;
	}

	public Font derive( Weight weight ) {
		return new Font( name, weight, posture, size );
	}

	public Font derive( Posture posture ) {
		return new Font( name, weight, posture, size );
	}

	public Font derive( double size ) {
		return new Font( name, weight, posture, size );
	}

	public static Font of( javafx.scene.text.Font font ) {
		String style = font.getStyle();
		FontWeight weight = FontUtil.getFontWeight( style );
		FontPosture posture = FontUtil.getFontPosture( style );
		return new Font( font.getFamily(), Weight.valueOf( weight.name() ), Posture.valueOf( posture.name() ), font.getSize() );
	}

	public static javafx.scene.text.Font toFxFont( Font font ) {
		String name = font.getName();
		FontWeight weight = FontUtil.getFontWeight( name );
		FontPosture posture = FontUtil.getFontPosture( name );
		double size = font.getSize();
		return javafx.scene.text.Font.font( name, weight, posture, size );
	}
}
