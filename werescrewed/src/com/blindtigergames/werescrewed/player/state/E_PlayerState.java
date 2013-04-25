package com.blindtigergames.werescrewed.player.state;


public enum E_PlayerState {
	Standing(new DummyState()), 
	Running(new DummyState()), 
	Jumping(new DummyState()), 
	Falling(new DummyState()), 
	Screwing(new DummyState()), 
	Dead(new DummyState()), 
	GrabMode(new DummyState()), 
	HeadStand(new DummyState()), 
	Landing(new DummyState()), 
	RespawnMode(new DummyState());
	protected I_PlayerState state;
	E_PlayerState(I_PlayerState s){
		state = s;
	}
	public I_PlayerState state(){return state;}
}
