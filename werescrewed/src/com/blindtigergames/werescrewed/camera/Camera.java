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
//	private static final float SPEED_TARGET_MODIFIER = 5f;
	private static final float BUFFER_RATIO = .33f;
//	private static final int LISTEN_BUFFER = 300;
	private static final float ACCELERATION_RATIO = .005f;
	private static final float TARGET_BUFFER_RATIO = .05f;
	private static final float MINIMUM_FOLLOW_SPEED = 1f;
	private static final float MAX_ANGLE_DIFF = 45f;
	private Vector2  translateVelocity;
	private float translateSpeed;
	private float translateAcceleration;
	private Rectangle translateBuffer;
	private Vector2 translateTarget;
	private Vector3 translateTarget3D;
	private float targetBuffer;
	private boolean translateState;
	
	// might take these out when no longer required
	private Player player1;
	private Player player2;
	private AnchorList anchorList;
	private int player1Anchor;
	private int player2Anchor;
	
	// debug
	private boolean debugInput;
	private boolean debugRender;
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
											this.viewportWidth * BUFFER_RATIO,
											this.viewportHeight * BUFFER_RATIO);
		
		this.translateTarget = new Vector2(center2D);
		this.translateTarget3D = new Vector3(translateTarget.x, translateTarget.y, 0f);
		translateVelocity = new Vector2(0f, 0f);
		translateSpeed = 0f;
		translateAcceleration = 0f;
		targetBuffer =  ( ( translateBuffer.width + translateBuffer.height ) / 2 ) * TARGET_BUFFER_RATIO;
		translateState = true;
				
		player1Anchor = -1;
		player2Anchor = -1;
		anchorList = AnchorList.getInstance();
		anchorList.clear( );
		
		// debug
		debugInput = false;
		debugRender = false;
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
		debugInput = false;
		debugRender = false;
//		debugMode = true;
		//check debug
		if (Gdx.input.isKeyPressed(Keys.B)) {
			debugInput = true;
		}
		if (Gdx.input.isKeyPressed(Keys.N)) {
			debugRender = true;
		}
		if (debugInput)
			handleInput();
		
		// update player anchors
		if (player1Anchor > -1) {
			anchorList.setAnchorPos(player1Anchor, player1.getPosition().mul(BOX_TO_PIXEL));
		}
		if (player2Anchor > -1) {
			anchorList.setAnchorPos(player2Anchor, player2.getPosition().mul(BOX_TO_PIXEL));
		}
				
		position = camera.position;
		center2D.x = position.x;
		center2D.y = position.y;
		
		translateBuffer.x = position.x - translateBuffer.width * .5f;
		translateBuffer.y = position.y - translateBuffer.height * .5f;
    	setTranslateTarget();
		
//    	player1.moveRight();
		if (!debugInput && translateState) {
			if (center2D.dst(translateTarget) < targetBuffer) {
				float tempAngle = 0f;
				tempAngle = anchorList.getMidpointVelocity().angle() - translateVelocity.angle();
				tempAngle = Math.abs(tempAngle);
				if (anchorList.getMidpointVelocity().len() > MINIMUM_FOLLOW_SPEED &&
						tempAngle < MAX_ANGLE_DIFF )
					camera.position.set(translateTarget3D);
				else
					translateState = false;
			}
			else
				translate();
		} else if(!translateState) {
			translateVelocity.x = 0f;
			translateVelocity.y = 0f;
			translateAcceleration = 0f;
			translateSpeed = 0f;
			if (!translateBuffer.contains(translateTarget.x, translateTarget.y))
				translateState = true;
		}
		
		if (debugRender) {
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin(ShapeType.Rectangle);
			shapeRenderer.identity();
			shapeRenderer.rect(translateBuffer.x, translateBuffer.y, translateBuffer.width, translateBuffer.height);
			shapeRenderer.end();
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin(ShapeType.Circle);
			shapeRenderer.identity();
			shapeRenderer.circle(translateTarget.x, translateTarget.y, targetBuffer);
			shapeRenderer.end();
		}
		
		camera.update();

		if (debugInput) {
			System.out.println("Zoom: " + camera.zoom);
		}
		
		anchorList.updateVelocity();
		
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
            		translateBuffer.width = camera.zoom * viewportWidth * BUFFER_RATIO;
            		translateBuffer.height = camera.zoom * viewportHeight * BUFFER_RATIO;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
                    camera.zoom -= 0.02;
            		translateBuffer.width = camera.zoom * viewportWidth * BUFFER_RATIO;
            		translateBuffer.height = camera.zoom * viewportHeight * BUFFER_RATIO;
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
            if(Gdx.input.isKeyPressed(Input.Keys.NUM_0)) {
                camera.zoom = .5f;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
                camera.zoom = 1f;
            }
            if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
                camera.zoom = 2f;
            }
    }
    
    /**
     * set focus of camera to the midpoint of all anchors
     */
    private Vector2 setTranslateTarget(){
    	translateTarget.x = anchorList.midpoint().x;
    	translateTarget.y = anchorList.midpoint().y;
//    	translateTarget.x += anchorList.getMidpointVelocity().x * SPEED_TARGET_MODIFIER; 
//    	translateTarget.y += anchorList.getMidpointVelocity().y * SPEED_TARGET_MODIFIER; 
    	
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
//    	camera.position.set(translateTarget3D);
    	translateVelocity = translateTarget.cpy();
    	translateVelocity.sub(center2D);
    	translateAcceleration = translateVelocity.len() * ACCELERATION_RATIO;
    	translateSpeed += translateAcceleration;
    	translateVelocity.nor();
    	translateVelocity.mul(translateSpeed);
    	camera.translate(translateVelocity);
    }
}