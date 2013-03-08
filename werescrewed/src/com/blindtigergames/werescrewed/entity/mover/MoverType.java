package com.blindtigergames.werescrewed.entity.mover;


public enum MoverType {
		ROCKING("rocking"),
		LINEAR_AXIS("linear")
	    ;
	    /**
	     * @param text
	     */
	    private MoverType(final String text) {
	        this.text = text;
	        
	    }

	    private final String text;
	    
	    @Override
	    public String toString() {
	        return text;
	    }
	    
	    public static MoverType fromString(String s){
	    	for (MoverType tag : MoverType.values()) {
	    		  if (tag.text.equals( s ))
	    			  return tag;
	    	}
	    	return null;
	    }
	    
	    public static final String tag = "Type";

}
