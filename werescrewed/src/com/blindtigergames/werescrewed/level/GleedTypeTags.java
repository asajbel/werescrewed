package com.blindtigergames.werescrewed.level;

import java.util.HashMap;

public enum GleedTypeTags {
    ENTITY("Entity"),
    MOVER("Mover")
    ;
    /**
     * @param text
     */
    private GleedTypeTags(final String text) {
        this.text = text;
        
    }

    private final String text;
    @Override
    public String toString() {
        return text;
    }
    
    public static GleedTypeTags fromString(String s){
    	for (GleedTypeTags tag : GleedTypeTags.values()) {
    		  if (tag.text.equals( s ))
    			  return tag;
    	}
    	return null;
    }
    
    public static final String tag = "Type";

}
