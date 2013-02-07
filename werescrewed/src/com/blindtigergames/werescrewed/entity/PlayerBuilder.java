package com.blindtigergames.werescrewed.entity;


public class PlayerBuilder extends EntityBuilder {
	protected int number;
	
	public PlayerBuilder( ) {
		super();
		type = EntityDef.getDefinition( "playerTest" );
		number = 1;
	}
	public PlayerBuilder number(int n){
		number = n;
		return this;
	}
	
	@Override
	public boolean canBuild(){
		if (world == null || pos == null)
			return false;
		return true;
	}
	
	@Override
	public Entity build(){
		return buildPlayer();
	}

	public Player buildPlayer(){
		Player out = new Player( name, world, pos );
		return out;
	}
}