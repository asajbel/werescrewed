package com.blindtigergames.werescrewed.entity.animator;

import com.blindtigergames.werescrewed.entity.animator.SimpleFrameAnimator.LoopBehavior;

public enum PlayerAnim{
	IDLE("idle")
	,RUN("run")
	,HANG("hang")
	,JUMP_UP("jump_up", LoopBehavior.STOP)
	,JUMP_DOWN("jump_down", LoopBehavior.STOP)
	,DEATH_BEGIN("death_begin", LoopBehavior.STOP )
	,DEATH("death_idle")
	;
	String text;
	LoopBehavior loop;
	PlayerAnim(String t){
		this(t, LoopBehavior.LOOP);
	}
	PlayerAnim(String t, LoopBehavior l){
		text = t;
		loop = l;
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
