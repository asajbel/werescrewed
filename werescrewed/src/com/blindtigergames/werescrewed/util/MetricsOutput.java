package com.blindtigergames.werescrewed.util;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

public class MetricsOutput {
	public ArrayList< Vector2 > death;
	public ArrayList< Vector2 > jump;
	public ArrayList< Vector2 > attach;
	public ArrayList< Vector2 > unscrewed;
	public ArrayList< Vector2 > screwed;
	public ArrayList< Float > time;
	public ArrayList< String > names; 

	public MetricsOutput( ) {
		death = null;
		jump = null;
		attach = null;
		unscrewed = null;
		screwed = null;
		time = null;
		names = null;
	}
}
