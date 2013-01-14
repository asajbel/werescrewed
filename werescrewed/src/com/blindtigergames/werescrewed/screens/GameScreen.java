package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import javax.swing.JTextField;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.gui.Button;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.gui.Button.ButtonHandler;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.screens.ScreenSwitchHandler;

import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Player;

import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;





public class GameScreen implements com.badlogic.gdx.Screen {
	
	/*** Box2D to pixels conversion *************
	 * 
	 * This number means 1 meter equals 256 pixels.
	 * That means the biggest in-game object (10 meters) we can use
	 * is 2560 pixels wide, which is much bigger than our max
	 * screen resolution so it should be enough.
	 */
	public static final float BOX_TO_PIXEL = 256f;
	public static final float PIXEL_TO_BOX = 1/BOX_TO_PIXEL;
	
	OrthographicCamera camera;
	Camera cam;
	SpriteBatch batch;
	Texture texture;
	Texture playerTexture;
	Sprite sprite;
    World world;
	SBox2DDebugRenderer debugRenderer;
	Body playerBody;
	Entity playerEntity;
	Player player;
	TiledPlatform tp;

	FPSLogger logger;

	
	private final Vector2 dec = new Vector2(.5f,0);
	private final Vector2 acc = new Vector2(.3f,0);
	private final Vector2 max = new Vector2(1f,0);
	static final float WORLD_TO_BOX = 0.01666667f;
	static final float BOX_TO_WORLD = 60f;



	
	public GameScreen() {
		System.out.println("GameSCreen starting");
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();


		
		//takes in width, height
        //cam = new Camera(w, h);
        batch = new SpriteBatch();
      
        world = new World(new Vector2(0, -100), true);
       // mcl = new MyContactListener();
        //world.setContactListener(mcl);
        String name = "player";

        player = new Player(world, new Vector2(100.0f *PIXEL_TO_BOX, 100.0f *PIXEL_TO_BOX), name);

        cam = new Camera(w, h, player);
        tp = new TiledPlatform("plat", new Vector2(200.0f, 25.0f), null, 1, 2, world);
        //BOX_TO_PIXEL, PIXEL_TO_BOX
        BodyDef groundBodyDef =new BodyDef();  
        groundBodyDef.position.set(new Vector2(0*PIXEL_TO_BOX, 0*PIXEL_TO_BOX));  
        Body groundBody = world.createBody(groundBodyDef);  
        PolygonShape groundBox = new PolygonShape();  
        groundBox.setAsBox(cam.viewportWidth*PIXEL_TO_BOX, 1f*PIXEL_TO_BOX);  
        groundBody.createFixture(groundBox, 0.0f);
        groundBody.getFixtureList().get(0).setFriction(0.5f);
        
      

        //make sure you uncomment the next two lines        debugRenderer = new SBox2DDebugRenderer(BOX_TO_PIXEL); for physics world        
        //debugRenderer = new Box2DDebugRenderer();
       debugRenderer = new SBox2DDebugRenderer(BOX_TO_PIXEL);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        
        logger = new FPSLogger();
        
       
      
       
	}

	@Override
	public void render(float delta) {
		Gdx.gl20.glClearColor(0.0f, 0f, 0.0f, 1.0f);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		cam.update();
		
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
	        ScreenManager.getInstance().show(Screen.PAUSE);			
		}


		player.update();
		tp.update();
		
		batch.setProjectionMatrix(cam.combined());
		//batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		//sprite.draw(batch);
		//Drawing the player here
		//playerEntity.draw(batch);
		//player.draw(batch);
		//tp.draw(batch);
		
		batch.end();

		
		//logger.log();
		debugRenderer.render(world, cam.combined());

	

		world.step(1/60f, 6, 2); //step our physics calculations
		//Gdx.app.debug("Physics", "delta = "+Gdx.app.getGraphics().getDeltaTime());
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
	


}
