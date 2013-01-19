package com.blindtigergames.werescrewed.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.blindtigergames.werescrewed.entity.Player;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
//import com.blindtigergames.werescrewed.screens.GameScreen;
//import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/*******************************************************************************
 * Camera class. Zooms and translates based on anchors. Max 30 anchors.
 * @author Edward Ramirez
 ******************************************************************************/
public class Camera {
	public static final float BOX_TO_PIXEL = 256f;
	public static final float PIXEL_TO_BOX = 1/BOX_TO_PIXEL;
	public static final float DEGTORAD = 0.0174532925199432957f;
	public static final float RADTODEG = 57.295779513082320876f;
	public OrthographicCamera camera;
	public float viewportHeight;
	public float viewportWidth;
	public Vector3 position;
	public Vector2 center2D;
	
	// translation
	private Vector2  translateVelocity; // (magnitude, direction);
	private Rectangle translateBuffer;
	private static final int LISTEN_BUFFER = 300;
	private Vector2 translateTarget;
	private Vector3 translateTarget3D;
	
	// might take these out when no longer required
	private Player player1;
	private Player player2;
	private AnchorList anchorList;
	private int player1Anchor;
	private int player2Anchor;
	private boolean debugMode;
	
	// debug
	private ShapeRenderer shapeRenderer;
	
	private void initializeVars (float viewportWidth, float viewportHeight) {
		camera = new OrthographicCamera(1, viewportHeight/viewportWidth);
		this.viewportHeight = Gdx.graphics.getHeight();
		this.viewportWidth = Gdx.graphics.getWidth();
		camera.viewportWidth = this.viewportWidth;
		camera.viewportHeight = this.viewportHeight;
		camera.position.set(this.viewportWidth * .5f, this.viewportHeight * .5f, 0f);  
		position = camera.position;
		center2D = new Vector2(position.x, position.y); 
		
		this.translateBuffer = new Rectangle(camera.position.x,
											camera.position.y,
											this.viewportWidth * .5f,
											this.viewportHeight * .5f);
		
		this.translateTarget = new Vector2(center2D);
		this.translateTarget3D = new Vector3(translateTarget.x, translateTarget.y, 0f);
		
		player1Anchor = -1;
		player2Anchor = -1;
		debugMode = false;
		anchorList = AnchorList.getInstance();
		
		// debug
		shapeRenderer = new ShapeRenderer();
	}
	
	public Camera(float viewportWidth, float viewportHeight)
	{
		this(viewportWidth, viewportHeight, null, null);  
	}
	
	public Camera(float viewportWidth, float viewportHeight, Player player)
	{
		this(viewportWidth, viewportHeight, player, null);
	}
	
	public Camera(float viewportWidth, float viewportHeight, Player player1, Player player2)
	{
		initializeVars(viewportWidth, viewportHeight);
		this.player1 = player1;
		this.player2 = player2;
		
		if (player1 != null) {
			player1Anchor = anchorList.addAnchor(player1.getPosition());
		}
		
		if (player2 != null) {
			player2Anchor = anchorList.addAnchor(player2.getPosition());
		}
        camera.update();  
	}
	
	public Matrix4 combined()
	{
		return camera.combined;
	}
	
	public void update()
	{
		debugMode = false;
		// check debug
		if (Gdx.input.isKeyPressed(Keys.Z)) {
			debugMode = true;
		}
		
		// update player anchors
		if (player1Anchor > -1) {
			anchorList.setAnchorPos(player1Anchor, player1.getPosition().mul(BOX_TO_PIXEL));
		}
		if (player2Anchor > -1) {
			anchorList.setAnchorPos(player2Anchor, player2.getPosition().mul(BOX_TO_PIXEL));
		}
		
		translate();
		position = camera.position;
		center2D.x = position.x;
		center2D.y = position.y;
		
		camera.update();
		
//		float width = 512;
//		float height = 256;
		translateBuffer.x = position.x - translateBuffer.width * .5f;
		translateBuffer.y = position.y - translateBuffer.height * .5f;
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Rectangle);
		shapeRenderer.identity();
		shapeRenderer.rect(translateBuffer.x, translateBuffer.y, translateBuffer.width, translateBuffer.height);
		shapeRenderer.end();
		
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
            		translateBuffer.width *= camera.zoom;
            		translateBuffer.height *= camera.zoom;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
                    camera.zoom -= 0.02;
            		translateBuffer.width *= camera.zoom;
            		translateBuffer.height *= camera.zoom;
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
    
    /**
     * set focus of camera to the midpoint of all anchors
     */
    private Vector2 setTranslateTarget(){
    	this.translateTarget = anchorList.midpoint();
		translateTarget3D.x = translateTarget.x;
		translateTarget3D.y = translateTarget.y;
		translateTarget3D.z = 0f;
    	return this.translateTarget;
    }
    
    /**
     * set the focus of camera to the weighted midpoint of all anchors
     */
    private Vector2 setFocusWeighted() {
    	return this.translateTarget;
    }
    
    private void translate() {
    	setTranslateTarget();
		if (debugMode) {
			handleInput();
		} else {
			camera.position.set(translateTarget3D);
		}
    }
}