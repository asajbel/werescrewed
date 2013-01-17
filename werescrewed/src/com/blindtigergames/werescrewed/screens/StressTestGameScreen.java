package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Player;
import com.blindtigergames.werescrewed.entity.mover.TimelineMover;
import com.blindtigergames.werescrewed.platforms.ComplexPlatform;
import com.blindtigergames.werescrewed.platforms.RoomPlatform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;





public class StressTestGameScreen implements com.badlogic.gdx.Screen {
	
	/*** Box2D to pixels conversion *************
	 * 
	 * This number means 1 meter equals 256 pixels.
	 * That means the biggest in-game object (10 meters) we can use
	 * is 2560 pixels wide, which is much bigger than our max
	 * screen resolution so it should be enough.
	 */
	public static final float BOX_TO_PIXEL = 256f;
	public static final float PIXEL_TO_BOX = 1/BOX_TO_PIXEL;
	public static final float DEGTORAD = 0.0174532925199432957f;
	public static final float RADTODEG = 57.295779513082320876f;
	
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
	TiledPlatform tp, tp2;
	RoomPlatform rp;
	ComplexPlatform cp;
	Body groundBody;
	
	ArrayList<Body> platforms;

	FPSLogger logger;

	
	private final Vector2 dec = new Vector2(.5f,0);
	private final Vector2 acc = new Vector2(.3f,0);
	private final Vector2 max = new Vector2(1f,0);



	
	public StressTestGameScreen() {
		System.out.println("GameScreen starting");
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		

		texture = new Texture(Gdx.files.internal("data/rletter.png"));
		//takes in width, height
        //cam = new Camera(w, h);
        batch = new SpriteBatch();
      
        world = new World( new Vector2(0, -100), true );
       // mcl = new MyContactListener();
        //world.setContactListener(mcl);
        String name = "player";

        player = new Player( world, new Vector2(100.0f, 500.0f), name );

        cam = new Camera( w, h, player );
        //tp = new TiledPlatform( "plat", new Vector2(200.0f, 100.0f), texture, 1, 2, world );
        rp = new RoomPlatform( "room", new Vector2(-100.0f, 100.0f), texture, 1, 5, world );
        cp = new ComplexPlatform( "bottle", new Vector2(-200.0f, 300.0f), texture, 1, 1, world );
        
        //tp = new TiledPlatform("plat", new Vector2(200.0f, 100.0f), null, 1, 2, world);
        //tp.setMover(new TimelineMover());
        //BOX_TO_PIXEL, PIXEL_TO_BOX
        BodyDef groundBodyDef =new BodyDef();  
        groundBodyDef.type = BodyType.DynamicBody;
        groundBodyDef.position.set(new Vector2(0*PIXEL_TO_BOX, 0*PIXEL_TO_BOX));  
        groundBody = world.createBody(groundBodyDef);  
        //PolygonShape groundBox = new PolygonShape();  
        //groundBox.setAsBox(cam.viewportWidth*4*PIXEL_TO_BOX, 1f*PIXEL_TO_BOX);  
        //groundBody.createFixture(groundBox, 0.0f);
        //groundBody.getFixtureList().get(0).setFriction(0.5f);

        //make sure you uncomment the next two lines        debugRenderer = new SBox2DDebugRenderer(BOX_TO_PIXEL); for physics world        
        //debugRenderer = new Box2DDebugRenderer();
        debugRenderer = new SBox2DDebugRenderer(BOX_TO_PIXEL);
        debugRenderer.setDrawJoints(false);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        
        logger = new FPSLogger();
        
        platforms = new ArrayList<Body>();
        
        for( int i = 0; i < 2; ++i ){
        	for ( int j = 0; j < 2; ++j ){
		        BodyDef bDef = new BodyDef();
		        bDef.position.set(new Vector2((250*i+200)*PIXEL_TO_BOX,(25*j+200)*PIXEL_TO_BOX));
		        bDef.type = BodyType.DynamicBody;
		        Body b = world.createBody(bDef);
		        PolygonShape bBox = new PolygonShape(); 
		        bBox.setAsBox(100*PIXEL_TO_BOX, 5*PIXEL_TO_BOX);
		        b.createFixture(bBox,1.0f);
		        platforms.add(b);
		        
		        RevoluteJointDef jointDef = new RevoluteJointDef();
		        //jointDef.initialize(b, groundBody, b.getWorldCenter());
		        jointDef.bodyA = b;
		        jointDef.bodyB = groundBody;
		        jointDef.collideConnected = false;
		        jointDef.localAnchorA.set(new Vector2());
		        jointDef.localAnchorB.set(new Vector2((250*i+200)*PIXEL_TO_BOX,(25*j+200)*PIXEL_TO_BOX));
		        //jointDef.localAnchorA = b.getWorldCenter().sub(-80*PIXEL_TO_BOX,0);
		        world.createJoint(jointDef);
		        
		        
		        //b.setSleepingAllowed(false);
        	}
        }
       
	}

	@Override
	public void render(float delta) {
		Gdx.gl20.glClearColor(0.0f, 0f, 0.0f, 1.0f);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		cam.update();
		
		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
	        ScreenManager.getInstance().show(Screen.PAUSE);			
		}
		if(Gdx.input.isKeyPressed(Keys.P)){
			System.exit(0);
		}

		player.update();
		//tp.update();
		rp.update();
		cp.update();
		
		if ( Gdx.input.isKeyPressed(Input.Keys.X) ){
			groundBody.setTransform(groundBody.getTransform().getPosition().add(0f,0.1f),0);
			//groundBody.setTransform(0f, -0.01f, 0);
			//Gdx.app.log("dude", "DUDE!");
			for ( Body b: platforms ){
				b.setActive(true);
			}
		}
		
		if ( Gdx.input.isKeyPressed(Input.Keys.Z) ){
			//groundBody.setTransform(0f, 0.01f, 0);
			groundBody.setTransform(groundBody.getTransform().getPosition().add(0f,-0.1f),0);
			//Gdx.app.log("dude", "DUDE!");
			for ( Body b: platforms ){
				b.setActive(true);
			}
		}
		
		if ( Gdx.input.isKeyPressed(Input.Keys.C) ){
			groundBody.setTransform(groundBody.getTransform().getPosition(),groundBody.getTransform().getRotation()+0.1f);
			//groundBody.setTransform(0f, -0.01f, 0);
			//Gdx.app.log("dude", "DUDE!");
			for ( Body b: platforms ){
				b.setActive(true);
			}
		}
		
		if ( Gdx.input.isKeyPressed(Input.Keys.V) ){
			//groundBody.setTransform(0f, 0.01f, 0);
			groundBody.setTransform(groundBody.getTransform().getPosition(),groundBody.getTransform().getRotation()-0.1f);
			//Gdx.app.log("dude", "DUDE!");
			for ( Body b: platforms ){
				b.setActive(true);
			}
		}
		
		
		batch.setProjectionMatrix(cam.combined());
		//batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		//sprite.draw(batch);
		//Drawing the player here
		//playerEntity.draw(batch);
		//player.draw(batch);
		
		// test drawing the texture by uncommenting the next line:
		//tp.draw(batch);
		
		batch.end();

		
		//logger.log();
		
		//Render before stepping world
		debugRenderer.render(world, cam.combined());

		//Step our physics calculations
		world.step(1/60f, 6, 2); 
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
