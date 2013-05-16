package com.blindtigergames.werescrewed.level;

/**
 * Used to split up the level.xml into different types so we can call the
 * correct function to make the object or mover
 * 
 * as of 3-5-2013, gleedloader only uses entity tag
 * 
 */
public enum GleedTypeTag {
	ENTITY( "Entity" ), MOVER( "Mover" ), SKELETON( "Skeleton" ), PUZZLE(
			"Puzzle" );
	/**
	 * @param text
	 */
	private GleedTypeTag( final String text ) {
		this.text = text;

	}

	private final String text;

	@Override
	public String toString( ) {
		return text;
	}

	public static GleedTypeTag fromString( String s ) {
		for ( GleedTypeTag tag : GleedTypeTag.values( ) ) {
			if ( tag.text.equals( s ) )
				return tag;
		}
		return null;
	}

	public static final String tag = "type";

}
