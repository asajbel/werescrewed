package com.blindtigergames.werescrewed.entity.action;

import com.badlogic.gdx.utils.Array;

public enum ActionType {
	FORANYENTITY( "foranyentity" ), FORPLAYER( "forplayer" ), ACT_ON_PLAYER(
			"actonplayer" );
	/**
	 * @param text
	 */
	private ActionType( final String text ) {
		this.text = text;

	}

	private final String text;

	@Override
	public String toString( ) {
		return text;
	}

	public static ActionType fromString( String s ) {
		// We only want the first word. The rest could be parameters.
		Array< String > tokens = new Array< String >( s.split( "\\w+" ) );
		String front;
		if ( tokens.size > 0 ) {
			front = tokens.get( 0 );
		} else {
			front = s;
		}
		for ( ActionType tag : ActionType.values( ) ) {
			if ( tag.text.equals( front ) )
				return tag;
		}
		return null;
	}

	public static final String tag = "Type";

}
