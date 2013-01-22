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
	private Vector2 prevMidpoint;
	private Vector3 midpoint3;
	private Vector2 midpointVelocity;
	private Vector2 specialMidpoint;
//	private int stepNum;
//	private int prevStepNum;
//	private static final int MAX_STEP_NUM;
	private static AnchorList instance;
	
	private AnchorList() {
		anchorList = new ArrayList<Anchor>();
		sum = new Vector2(0f, 0f);
		midpoint2 = new Vector2(0f, 0f);
		prevMidpoint = new Vector2(0f, 0f);
		midpoint3 = new Vector3(0f, 0f, 0f);
		midpointVelocity = new Vector2(0f, 0f);
		specialMidpoint = new Vector2(0f, 0f);
//		stepNum = 0;
//		prevStepNum = 0;
	}
	
	public static AnchorList getInstance() {
		if (instance ==null) {
			instance = new AnchorList();
		}
		return instance;
	}
	
	public void updateVelocity() {
//		stepNum += 1;
//		if (stepNum > MAX_STEP_NUM) {
//			stepNum = 0;
//		}
		
		// update velocity of midpoint
		midpointVelocity.x = midpoint2.x;
		midpointVelocity.y = midpoint2.y;
		midpointVelocity.sub(prevMidpoint);
		
		prevMidpoint.x = midpoint2.x;
		prevMidpoint.y = midpoint2.y;
	}
	
	/**
	 * 
	 * @param special Set true if creating a player anchor.
	 * @param position Position of the current anchor
	 * @return The id of the current anchor. Don't forget to update it!
	 */
	public int addAnchor(boolean special, Vector2 position) {
		return addAnchor(special, position, Anchor.DEFAULT_WEIGHT, Anchor.DEFAULT_BUFFER);
	}
	
	/**
	 * 
	 * @param special Set true when creating a player anchor.
	 * @param position
	 * @param weight
	 * @return The id of the current anchor. Don't forget to update it!
	 */
	public int addAnchor(boolean special, Vector2 position, int weight) {
		return addAnchor(special, position, weight, Anchor.DEFAULT_BUFFER);
	}
	
	/**
	 * 
	 * @param special Set true when creating a player anchor.
	 * @param position
	 * @param weight
	 * @param bufferWidth
	 * @return The id of the current anchor. Don't forget to update it!
	 */
	public int addAnchor(boolean special, Vector2 position, int weight, int bufferWidth) {
		return addAnchor(special, position, weight, new Vector2(bufferWidth, bufferWidth));
	}
	
	/**
	 * 
	 * @param special Set true when creating a player anchor.
	 * @param position
	 * @param weight
	 * @param buffer
	 * @return The id of the current anchor. Don't forget to update it!
	 */
	
	public int addAnchor(boolean special, Vector2 position, int weight, Vector2 buffer) {
		int id = anchorList.size();
		anchorList.add(new Anchor(special, position, weight, buffer));
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
    	for(Anchor curAnchor:anchorList) {
    		sum.add(curAnchor.position);
    		count++;
    	}
    	midpoint2 = sum.div((float) count);
    	midpoint3.x = midpoint2.x;
    	midpoint3.y = midpoint2.y;
	}
	
	public Vector2 sepcialMidpoint() {
    	int count = 0;
    	sum.x = 0f;
    	sum.y = 0f;
    	
    	for(Anchor curAnchor:anchorList) {
    		if (curAnchor.special) {
        		sum.add(curAnchor.position);
        		count++;
    		}
    	}
    	
    	midpoint2 = sum.div((float) count);
     	return specialMidpoint;
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
	
	public Vector2 getMidpointVelocity() {
		return midpointVelocity;
	}
}