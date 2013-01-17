package com.blindtigergames.werescrewed.camera;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/*******************************************************************************
 * Stores a list of all current anchors in the world
 * @author Edward Ramirez
 ******************************************************************************/
public class AnchorList {
	private static ArrayList<Anchor> anchorList = new ArrayList<Anchor>();
	private static Vector2 sum = new Vector2(0f, 0f);
	private static Vector2 midpoint2 = new Vector2(0f, 0f);
	private static Vector3 midpoint3 = new Vector3(0f, 0f, 0f);
	
	public static int addAnchor(Vector2 position) {
		int id = anchorList.size();
		anchorList.add(new Anchor(position));
		return id;
	}
	
	public static int addAnchor(Vector2 position, int weight) {
		int id = anchorList.size();
		anchorList.add(new Anchor(position, weight));
		return id;
	}
	
	public static int addAnchor(Vector2 position, int weight, int bufferWidth) {
		int id = anchorList.size();
		anchorList.add(new Anchor(position, weight, bufferWidth));
		return id;
	}
	
	public static int addAnchor(Vector2 position, int weight, Vector2 buffer) {
		int id = anchorList.size();
		anchorList.add(new Anchor(position, weight, buffer));
		return id;
	}
	
	public static void clear() {
		anchorList.clear();
	}
	
	public static void setAnchorPos (int id, Vector2 pos) {
		// assuming pass by value, try pass by reference later
		Anchor temp = anchorList.get(id);
		temp.setPosition(pos);
		anchorList.set(id, temp);
	}
	
	public static void setAnchorWeight (int id, int weight) {
		Anchor temp = anchorList.get(id);
		temp.setWeight(weight);
		anchorList.set(id, temp);
	}
	
	public static void setAnchorBuffer (int id, Vector2 buffer) {
		Anchor temp = anchorList.get(id);
		temp.setBuffer(buffer);
		anchorList.set(id, temp);
	}
	
	public static void setMidpoint () {
    	int count = 0;
    	sum.x = 0f;
    	sum.y = 0f;
    	Iterator<Anchor> it = anchorList.listIterator(0);
    	while (it.hasNext()) {
    		sum.add((it.next()).position);
    		count++;
    	}
    	midpoint2 = sum.div((float) count);
    	midpoint3.x = midpoint2.x;
    	midpoint3.y = midpoint2.y;
	}
	
	public static void setWeightedMidpoint () {
		// TO DO: do this
	}
	
	public static Vector2 getMidpoint () {
		return midpoint2;
	}
	
	public static Vector3 getMidpoint3 () {
		return midpoint3;
	}
}