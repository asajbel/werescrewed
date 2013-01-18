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
	private ArrayList<Anchor> anchorList;
	private Vector2 sum;
	private Vector2 midpoint2;
	private Vector3 midpoint3;
	private static AnchorList instance;
	
	private AnchorList() {
		anchorList = new ArrayList<Anchor>();
		sum = new Vector2(0f, 0f);
		midpoint2 = new Vector2(0f, 0f);
		midpoint3 = new Vector3(0f, 0f, 0f);
	}
	
	public static AnchorList getInstance() {
		if (instance ==null) {
			instance = new AnchorList();
		}
		return instance;
	}
	
	public int addAnchor(Vector2 position) {
		int id = anchorList.size();
		anchorList.add(new Anchor(position));
		return id;
	}
	
	public int addAnchor(Vector2 position, int weight) {
		int id = anchorList.size();
		anchorList.add(new Anchor(position, weight));
		return id;
	}
	
	public int addAnchor(Vector2 position, int weight, int bufferWidth) {
		int id = anchorList.size();
		anchorList.add(new Anchor(position, weight, bufferWidth));
		return id;
	}
	
	public int addAnchor(Vector2 position, int weight, Vector2 buffer) {
		int id = anchorList.size();
		anchorList.add(new Anchor(position, weight, buffer));
		return id;
	}
	
	public void clear() {
		anchorList.clear();
	}
	
	public void setAnchorPos (int id, Vector2 position) {
		// assuming pass by value, try pass by reference later
		Anchor temp = anchorList.get(id);
		temp.setPosition(position);
		anchorList.set(id, temp);
	}
	
	public void setAnchorWeight (int id, int weight) {
		Anchor temp = anchorList.get(id);
		temp.setWeight(weight);
		anchorList.set(id, temp);
	}
	
	public void setAnchorBuffer (int id, Vector2 buffer) {
		Anchor temp = anchorList.get(id);
		temp.setBuffer(buffer);
		anchorList.set(id, temp);
	}
	
	public void setMidpoint () {
		//TO DO: discriminate by distance
		
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
	
	public void setWeightedMidpoint () {
		// TO DO: do this
	}
	
	public Vector2 getMidpoint () {
		return midpoint2;
	}
	
	public Vector3 getMidpoint3 () {
		return midpoint3;
	}
	
	public Vector2 midpoint() {
		setMidpoint();
		return getMidpoint();
	}
	
	public Vector3 midpoint3() {
		setMidpoint();
		return getMidpoint3();
	}
}