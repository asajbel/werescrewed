package com.blindtigergames.werescrewed.entity.screws;

public enum ScrewType {
	SCREW_COSMETIC( "ScrewCosmetic" ), SCREW_STRIPPED( "ScrewStripped" ), SCREW_STRUCTURAL(
			"ScrewStructural" ), SCREW_PUZZLE( "ScrewPuzzle" ), SCREW_RESURRECT(
			"ScrewResurrect" ), SCREW_BOSS( "ScrewBoss" ), SCREW_POWER(
			"ScrewPower" );
	/**
	 * @param text
	 */
	private ScrewType( final String text ) {
		this.text = text;
	}

	private final String text;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString( ) {
		// TODO Auto-generated method stub
		return text;
	}

	public static ScrewType fromString( String s ) {
		for ( ScrewType t : ScrewType.values( ) ) {
			if ( t.text.equals( s ) )
				return t;
		}
		return null;
	}
}
