package com.blindtigergames.werescrewed.camera;

import java.util.Iterator;
import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.blindtigergames.werescrewed.entity.Player;
//import com.blindtigergames.werescrewed.screens.GameScreen;
//import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;

/*******************************************************************************
 * Camera class. Zooms and translates based on anchors. Max 30 anchors.
 * @author Edward Ramirez
 ******************************************************************************/
public class Camera {
	public OrthographicCamera camera;
	public float viewportHeight;
	public float viewportWidth;
	public Vector3 position;
	public Vector2 center2D;
	
	private static final int LISTEN_BUFFER = 300;
	private Player player1;
	private Player player2;
	private Rectangle anchorListenRectangle;
	private Vector2 focus;
	private Vector3 focus3D;
	private Vector2 prevFocus;
		
	private void initializeVars (float viewportWidth, float viewportHeight) {
		camera = new OrthographicCamera(1, viewportHeight/viewportWidth);
		this.viewportHeight = Gdx.graphics.getHeight();
		this.viewportWidth = Gdx.graphics.getWidth();
		camera.viewportWidth = this.viewportWidth;
		camera.viewportHeight = this.viewportHeight;
		camera.position.set(this.viewportWidth * .5f, this.viewportHeight * .5f, 0f);  
		position = camera.position;
		center2D = new Vector2(position.x, position.y); 
		
		this.anchorListenRectangle = new Rectangle((camera.position.x - this.viewportWidth * .5f) - LISTEN_BUFFER,
													(camera.position.y - this.viewportHeight * .5f) - LISTEN_BUFFER,
													this.viewportWidth * .5f + LISTEN_BUFFER,
													this.viewportHeight * .5f + LISTEN_BUFFER);
		
		this.focus = new Vector2(center2D);
		this.focus3D = new Vector3(focus.x, focus.y, 0f);
		this.prevFocus = new Vector2(focus);
	}
	
	public Camera(float viewportWidth, float viewportHeight)
	{
		initializeVars(viewportWidth, viewportHeight);
        camera.update();  
	}
	
	public Camera(float viewportWidth, float viewportHeight, Player player)
	{
		initializeVars(viewportWidth, viewportHeight);
		player1 = player;
		AnchorList.addAnchor(player.position);
        camera.update();  
	}
	
	public Camera(float viewportWidth, float viewportHeight, Player player1, Player player2)
	{
		initializeVars(viewportWidth, viewportHeight);
		this.player1 = player1;
		this.player2 = player2;
		AnchorList.addAnchor(player1.position);
		AnchorList.addAnchor(player2.position);
        camera.update();  
	}
	
	public Matrix4 combined()
	{
		return camera.combined;
	}
	
	public void update()
	{
		handleInput();
		setFocus();
		focus3D.x = focus.x;
		focus3D.y = focus.y;
//		camera.translate(prevFocus.sub(focus));
		camera.position.set(focus3D);
		camera.update();
		position = camera.position;
		center2D.x = position.x;
		center2D.y = position.y;
		prevFocus = focus;
		
		if (player1 != null) {
			
		}
		
		/*float lerp = 0.1f;
		Vector3 position = camera.position;
		position.x += (player.positionX - position.x) * lerp;
		position.y += (player.positionY - position.y) * lerp;
		camera.position.add(position);
		*/
	}

    private void handleInput() {
            if(Gdx.input.isKeyPressed(Input.Keys.E)) {
                    camera.zoom += 0.02;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
                    camera.zoom -= 0.02;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    if (camera.position.x > 0)
                            camera.translate(-3, 0, 0);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    if (camera.position.x < 1024)
                            camera.translate(3, 0, 0);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                    if (camera.position.y > 0)
                            camera.translate(0, -3, 0);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
                    if (camera.position.y < 1024)
                            camera.translate(0, 3, 0);
            }

    }

    
    /*
     * set focus of camera to the midpoint of all anchors
     */
    private Vector2 setFocus(){
    	// TO DO: discriminate based on distance
    	
    	int count = 0;
    	Vector2 vMid = new Vector2(0f, 0f);
    	Iterator<Anchor> it = this.anchorList.listIterator(0);
    	while (it.hasNext()) {
    		vMid.add((it.next()).position);
    		count++;
    	}
    	this.focus = vMid.div((float) count);
    	return this.focus;
    }
    
    /*
     * set the focus of camera to the weighted midpoint of all anchors
     */
    private Vector2 setFocusWeighted(){
    	return this.focus;
    }
}