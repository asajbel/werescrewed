package com.blindtigergames.werescrewed.camera;

import java.util.ArrayList;
import java.util.PriorityQueue;


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/*******************************************************************************
 * Stores a list of all current anchors in the world
 * @author Edward Ramirez
 ******************************************************************************/
public class AnchorList {
	private class AnchorPair {
		public int firstAnchorID;
		public int secondAnchorID;
	}
	
	private ArrayList<Anchor> anchorList;
	private Vector2 sum;
	private Vector2 midpoint2;
	private Vector2 prevMidpoint;
	private Vector3 midpoint3;
	private Vector2 midpointVelocity;
	private Vector2 specialMidpoint;
	private static AnchorList instance;
	private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;
	private PriorityQueue anchorDistanceQue;
	
	private AnchorList() {
		this(null);
	}
	
	private AnchorList(OrthographicCamera camera) {
		anchorList = new ArrayList<Anchor>();
		sum = new Vector2(0f, 0f);
		midpoint2 = new Vector2(0f, 0f);
		prevMidpoint = new Vector2(0f, 0f);
		midpoint3 = new Vector3(0f, 0f, 0f);
		midpointVelocity = new Vector2(0f, 0f);
		specialMidpoint = new Vector2(0f, 0f);
		shapeRenderer = new ShapeRenderer();
		this.camera = camera;
	}
	
	public static AnchorList getInstance() {
		if (instance ==null) {
			instance = new AnchorList();
		}
		return instance;
	}
	
	public static AnchorList getInstance(OrthographicCamera camera) {
		if (instance ==null) {
			instance = new AnchorList(camera);
		}
		return instance;
	}
	
	public void update() {
		update(false);
	}
	
	public void update(boolean debugRender) {
		
		// update velocity of midpoint
		midpointVelocity.x = midpoint2.x;
		midpointVelocity.y = midpoint2.y;
		midpointVelocity.sub(prevMidpoint);
		
		prevMidpoint.x = midpoint2.x;
		prevMidpoint.y = midpoint2.y;
		
		// render anchor points + buffer
		if (camera != null && debugRender) {
			for (Anchor curAnchor:anchorList) {
				shapeRenderer.setProjectionMatrix(camera.combined);
				shapeRenderer.begin(ShapeType.Rectangle);
				shapeRenderer.identity();
				shapeRenderer.rect(curAnchor.position.x - curAnchor.buffer.x,
									curAnchor.position.y - curAnchor.buffer.y,
									curAnchor.buffer.x * 2, curAnchor.buffer.y * 2);
				shapeRenderer.end();
				
				if (curAnchor.special) {

					shapeRenderer.begin(ShapeType.Line);
					shapeRenderer.line(curAnchor.position.x - curAnchor.buffer.x,
										curAnchor.position.y - curAnchor.buffer.y,
										curAnchor.position.x + curAnchor.buffer.x,
										curAnchor.position.y + curAnchor.buffer.y);
					shapeRenderer.line(curAnchor.position.x - curAnchor.buffer.x,
							curAnchor.position.y + curAnchor.buffer.y,
							curAnchor.position.x + curAnchor.buffer.x,
							curAnchor.position.y - curAnchor.buffer.y);
					shapeRenderer.end();
				}
			}
		}
		
		setMidpoint();
	}
	
	/**
	 * 
	 * @param special Set true if creating a player anchor.
	 * @param position Position of the current anchor
	 * @return The id of the current anchor. Don't forget to update it!
	 */
	public int addAnchor(boolean special, Vector2 position) {
		return addAnchor(special, position, Anchor.DEFAULT_BUFFER);
	}
	
	/**
	 * 
	 * @param special Set true when creating a player anchor.
	 * @param position Position of the current anchor
	 * @param bufferWidth Width of a "buffer" square around anchor to keep within screen. Ex: jump height.
	 * @return The id of the current anchor. Don't forget to update it!
	 */
	public int addAnchor(boolean special, Vector2 position, int bufferWidth) {
		return addAnchor(special, position, new Vector2(bufferWidth, bufferWidth));
	}
	
	/**
	 * 
	 * @param special Set true when creating a player anchor.
	 * @param position Position of the current anchor
	 * @param Width and height of "buffer" around anchor to keep within screen. Ex: width/height of boss head.
	 * @return The id of the current anchor. Don't forget to update it!
	 */
	
	public int addAnchor(boolean special, Vector2 position, Vector2 buffer) {
		int id = anchorList.size();
		addAnchor(new Anchor(special, position, buffer));
		return id;
	}
	
	public void addAnchor(Anchor newAnchor) {
		anchorList.add( newAnchor );
	}
	
	public void clear() {
		anchorList.clear();
	}
	
	/**
	 * set an anchor's position  in pixels
	 * @param id the int ID of the anchor
	 * @param position the new position  in pixels for the anchor
	 */
	public void setAnchorPos (int id, Vector2 position) {
		// assuming pass by value, try pass by reference later
		Anchor temp = anchorList.get(id);
		temp.setPosition(position);
		anchorList.set(id, temp);
	}
	
	/**
	 * set the anchor's position in box2D units
	 * @param id the int ID of the anchor
	 * @param position the new position in box2D units for the anchor
	 */
	public void setAnchorPosBox (int id, Vector2 position) {
		// assuming pass by value, try pass by reference later
		Anchor temp = anchorList.get(id);
		temp.setPositionBox(position);
		anchorList.set(id, temp);
	}
	
	/**
	 * set anchor's buffer 
	 * @param id the int ID of the anchor
	 * @param buffer vecter2(width, height) of new buffer
	 */
	public void setAnchorBuffer (int id, Vector2 buffer) {
		Anchor temp = anchorList.get(id);
		temp.setBuffer(buffer);
		anchorList.set(id, temp);
	}

	/**
	 * get an anchor's position in pixels
	 * @param id the int ID of the anchor
	 * @return anchor's position in pixels
	 */
	public Vector2 getAnchorPos (int id) {
		return anchorList.get( id ).position;
	}
	
	/**
	 * get the anchor's position in box2D units
	 * @param id the int ID of the anchor
	 */
	public Vector2 getAnchorPosBox (int id) {
		return anchorList.get( id ).positionBox;
	}
	
	/**
	 * get anchor's buffer 
	 * @param id the int ID of the anchor
	 */
	public Vector2 getAnchorBuffer (int id) {
		return anchorList.get( id ).buffer;
	}
	
	public Vector2 getSepcialMidpoint() {
     	return specialMidpoint;
	}
	
	public Vector2 getMidpoint () {
		return midpoint2;
	}
	
	public Vector2 getMidpointVelocity() {
		return midpointVelocity;
	}
	
	private void setMidpoint () {
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
    	
    	// set special midpoint
    	count = 0;
    	sum.x = 0f;
    	sum.y = 0f;
    	
    	for(Anchor curAnchor:anchorList) {
    		if (curAnchor.special) {
        		sum.add(curAnchor.position);
        		count++;
    		}
    	}
    	
    	midpoint2 = sum.div((float) count);
	}
}