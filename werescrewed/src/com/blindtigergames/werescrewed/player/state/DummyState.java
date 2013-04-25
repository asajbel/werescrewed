package com.blindtigergames.werescrewed.player.state;

import com.badlogic.gdx.physics.box2d.Contact;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.player.Player;

public class DummyState implements I_PlayerState {

	@Override
	public void left( Player player, float amount ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void right( Player player, float amount ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void jump( Player player ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void screw( Player player, float amount ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unscrew( Player player, float amount ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void collide( Player player, Object that, Class thatClass,
			Contact conact ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update( Player player, float dT ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void draw( Player player, SpriteBatch batch ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnterState( Player player ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLeaveState( Player player ) {
		// TODO Auto-generated method stub
		
	}

}
