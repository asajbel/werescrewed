package com.blindtigergames.werescrewed.entity.animator;

import com.blindtigergames.werescrewed.entity.animator.SimpleFrameAnimator.LoopBehavior;

public enum PlayerAnim{
	IDLE("idle")
	,RUN("run")
	,HANG("hang")
	,JUMP_UP("jump_end", LoopBehavior.STOP )
	,FALL_BEGIN("fall_begin", LoopBehavior.STOP )
	,FALL_IDLE("fall_idle", LoopBehavior.LOOP, PlayerAnim.FALL_BEGIN )
	,DEATH_BEGIN("death_begin", LoopBehavior.STOP )
	,DEATH("death_idle", PlayerAnim.DEATH_BEGIN)
	;
	String text;
	PlayerAnim start = null; 
	LoopBehavior loop;
	boolean loopBool;
	PlayerAnim(String t){
		this(t, LoopBehavior.LOOP);
		loopBool = true;
	}
	PlayerAnim(String t, PlayerAnim s){
		this(t, LoopBehavior.LOOP, s);
	}
	PlayerAnim(String t, LoopBehavior l){
		text = t;
		loop = l;
		if (l == LoopBehavior.STOP) {
			loopBool = false;
		} else loopBool = true;
	}
	PlayerAnim(String t, LoopBehavior l, PlayerAnim s){
		this(t,l); 
		start = s; 
	}
	PlayerAnim(String t, boolean loop){
		text = t;
		loopBool = loop;
	}
	public String toString(){
		return text;
	}
	public static PlayerAnim fromString(String t){
		for (PlayerAnim a: PlayerAnim.values( )){
			if (a.text.equals( t ) )
				return a;
		}
		return IDLE;
	}
}
