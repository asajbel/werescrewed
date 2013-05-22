package com.blindtigergames.werescrewed.entity.mover;

import com.badlogic.gdx.utils.Array;

public enum MoverType {
	ROCKING( "rocking" ), LINEAR_AXIS( "linear" ), ROTATETWEEN( "rotatetween" ), ROTATEBYDEGREE(
			"rotatebydegree" ), LERP( "lerpmover" ), PUZZLEROTATETWEEN(
			"puzzlerotatetween" ), ANALOG_ROTATE( "analogrotate" )
	// PuzzleRotateTweenMover

	;
	/**
	 * @param text
	 */
	private MoverType( final String text ) {
		this.text = text;

	}

	private final String text;

	@Override
	public String toString( ) {
		return text;
	}

	public static MoverType fromString( String s ) {
		// We only want the first word. The rest could be parameters.
		Array< String > tokens = new Array< String >( s.split( "\\w+" ) );
		String front;
		if ( tokens.size > 0 ) {
			front = tokens.get( 0 );
		} else {
			front = s;
		}
		for ( MoverType tag : MoverType.values( ) ) {
			if ( tag.text.equals( front ) )
				return tag;
		}
		return null;
	}

	public static final String tag = "Type";

}
