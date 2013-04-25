package com.blindtigergames.werescrewed.player.state;

import com.badlogic.gdx.physics.box2d.Contact;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.player.Player;

public interface I_PlayerState {
	public void left(Player player, float amount);
	public void right(Player player, float amount);
	public void jump(Player player);
	public void screw(Player player, float amount);
	public void unscrew(Player player, float amount);
	public void collide(Player player, Object that, Class thatClass, Contact conact);
	public void update(Player player, float dT);
	public void draw(Player player, SpriteBatch batch);
	public void onEnterState(Player player);
	public void onLeaveState(Player player);
}
