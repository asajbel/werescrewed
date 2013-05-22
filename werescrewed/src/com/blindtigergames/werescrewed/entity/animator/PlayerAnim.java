package com.blindtigergames.werescrewed.entity.animator;

import com.blindtigergames.werescrewed.entity.animator.SimpleFrameAnimator.LoopBehavior;

public enum PlayerAnim {
	SCREW_DRAW( "screw_draw", LoopBehavior.STOP ), SCREW_STOW( "screw_stow",
			LoopBehavior.STOP ), RUN_SCREW( "run_screw_ready" ), FALL_SCREW(
			"fall_idle_screw_ready" ), FALL_BEGIN_SCREW(
			"fall_begin_screw_ready", LoopBehavior.STOP ), JUMP_UP_SCREW(
			"jump_end_screw_ready" ), IDLE_SCREW( "idle_screw_ready" ), IDLE(
			"idle" ), RUN( "run" ), HANG( "hang" ), JUMP_UP( "jump_end",
			LoopBehavior.STOP ), FALL_BEGIN( "fall_begin", LoopBehavior.STOP ), FALL_IDLE(
			"fall_idle", LoopBehavior.LOOP, PlayerAnim.FALL_BEGIN ), DEATH_BEGIN(
			"death_begin", LoopBehavior.STOP ), DEATH( "death_idle",
			PlayerAnim.DEATH_BEGIN ), WAVE( "wave" ), SCREWING_HANG(
			"screwing_hang" ), SCREWING_GROUND( "screwing_ground" ), LAND_SCREW(
			"land_screw_ready", true ), LAND( "land", true ), TURN_SCREW(
			"turn_screw_ready", true ), TURN( "turn", true );
	private String text;
	PlayerAnim start = null;
	LoopBehavior loop;
	boolean loopBool;
	boolean singleTick = false;

	PlayerAnim( String t ) {
		this( t, LoopBehavior.LOOP );
		loopBool = true;
	}

	PlayerAnim( String t, PlayerAnim s ) {
		this( t, LoopBehavior.LOOP, s );
	}

	PlayerAnim( String t, LoopBehavior l ) {
		setText( t );
		loop = l;
		if ( l == LoopBehavior.STOP ) {
			loopBool = false;
		} else
			loopBool = true;
	}

	PlayerAnim( String t, LoopBehavior l, PlayerAnim s ) {
		this( t, l );
		start = s;
	}

	PlayerAnim( String t, boolean tick ) {
		this( t, LoopBehavior.STOP );
		singleTick = tick;
	}

	public String toString( ) {
		return getText();
	}

	public static PlayerAnim fromString( String t ) {
		for ( PlayerAnim a : PlayerAnim.values( ) ) {
			if ( a.getText().equals( t ) )
				return a;
		}
		return IDLE;
	}

	public String getText( ) {
		return text;
	}

	public void setText( String text ) {
		this.text = text;
	}
}
