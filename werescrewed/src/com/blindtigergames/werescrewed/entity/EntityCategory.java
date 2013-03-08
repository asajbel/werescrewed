package com.blindtigergames.werescrewed.entity;


public enum EntityCategory {
	PLAYER("Player"),
	TILED_PLATFORM("TiledPlatform"),
	COMPLEX_PLATFORM("ComplexPlatform");
	/**
     * @param text
     */
    private EntityCategory(final String text) {
        this.text = text;
        
    }

    private final String text;
    
    @Override
    public String toString() {
        return text;
    }
    
    public static EntityCategory fromString(String s){
    	for (EntityCategory tag : EntityCategory.values()) {
    		  if (tag.text.equals( s ))
    			  return tag;
    	}
    	return null;
    }
    
    public static final String tag = "category";

}
