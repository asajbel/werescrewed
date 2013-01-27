package com.blindtigergames.werescrewed.entity;

public class PlayerBuilder extends EntityBuilder {
	protected int number;
	
	public PlayerBuilder( ) {
		super();
		number = 1;
	}
	public PlayerBuilder number(int n){
		number = n;
		return this;
	}
	
	@Override
	public boolean canBuild(){
		if (world == null)
			return false;
		return true;
	}
	@Override
	public Player build(){
		Player out = new Player(world, pos, name, tex);
		return out;
	}
}
