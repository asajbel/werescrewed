package com.blindtigergames.werescrewed.level;


public enum GleedTypeTag {
    ENTITY("Entity"),
    MOVER("Mover"),
    SKELETON("Skeleton")
    ;
    /**
     * @param text
     */
    private GleedTypeTag(final String text) {
        this.text = text;
        
    }

    private final String text;
    @Override
    public String toString() {
        return text;
    }
    
    public static GleedTypeTag fromString(String s){
    	for (GleedTypeTag tag : GleedTypeTag.values()) {
    		  if (tag.text.equals( s ))
    			  return tag;
    	}
    	return null;
    }
    
    public static final String tag = "Type";

}
