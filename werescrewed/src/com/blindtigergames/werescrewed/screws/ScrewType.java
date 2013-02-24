package com.blindtigergames.werescrewed.screws;

public enum ScrewType {
	SCREW_STRIPPED("ScrewStripped"),
	SCREW_STRUCTURAL("ScrewStructural"),
	SCREW_PUZZLE("ScrewPuzzle"),
	SCREW_BOSS("ScrewBoss");
    /**
     * @param text
     */
    private ScrewType (final String text) {
        this.text = text;
    }

    private final String text;

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return text;
    }
    
    public static ScrewType fromString(String s){
    	for( ScrewType t : ScrewType.values( )){
    		if (t.text.equals( s ))
    			return t;
    	}
    	return null;
    }
}
