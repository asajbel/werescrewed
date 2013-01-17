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
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Player;
import com.blindtigergames.werescrewed.entity.mover.TimelineMover;
import com.blindtigergames.werescrewed.input.InputHandler;
import com.blindtigergames.werescrewed.input.InputHandler.player_t;
import com.blindtigergames.werescrewed.platforms.*;






public class IMoverGameScreen implements com.badlogic.gdx.Screen {
	
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
	Skeleton skeleton;
	//ShapePlatform sp;

	ArrayList<Body> platforms;
	
	FPSLogger logger;

	
	private final Vector2 dec = new Vector2(.5f,0);
	private final Vector2 acc = new Vector2(.3f,0);
	private final Vector2 max = new Vector2(1f,0);



	
	public IMoverGameScreen() {
		System.out.println("GameScreen starting");
		float zoom = 1.0f;
		float w = Gdx.graphics.getWidth()/zoom;
		float h = Gdx.graphics.getHeight()/zoom;


		texture = new Texture(Gdx.files.internal("data/rletter.png"));
		//takes in width, height
        //cam = new Camera(w, h);
        batch = new SpriteBatch();
      
        world = new World( new Vector2(0, -100), true );
       // mcl = new MyContactListener();
        //world.setContactListener(mcl);
        String name = "player";

        player = new Player( world, new Vector2(1.0f, 1.0f), name );

        cam = new Camera( w, h, player );
        tp = new TiledPlatform( "plat", new Vector2(5.0f, 40.0f), texture, 1, 2, world );
        rp = new RoomPlatform( "room", new Vector2(-1.0f, 1.0f), texture, 1, 10, world );
        cp = new ComplexPlatform( "bottle", new Vector2(0.0f, 3.0f), texture, 1, world, "bottle" );
        //sp = new ShapePlatform( "trap", new Vector2( 1.0f, 1.0f), texture, world, Shapes.trapezoid, 0.5f);
        
        //tp = new TiledPlatform("plat", new Vector2(200.0f, 100.0f), null, 1, 2, world);
        tp.setMover(new TimelineMover());
        //BOX_TO_PIXEL, PIXEL_TO_BOX
        BodyDef groundBodyDef =new BodyDef();  
        groundBodyDef.position.set(new Vector2(0*PIXEL_TO_BOX, 0*PIXEL_TO_BOX));  
        Body groundBody = world.createBody(groundBodyDef);  
        PolygonShape groundBox = new PolygonShape();  
        groundBox.setAsBox(Gdx.graphics.getWidth()*PIXEL_TO_BOX, 1f*PIXEL_TO_BOX);  
        groundBody.createFixture(groundBox, 0.0f);
        groundBody.getFixtureList().get(0).setFriction(0.5f);
        

        //make sure you uncomment the next two lines        debugRenderer = new SBox2DDebugRenderer(BOX_TO_PIXEL); for physics world        
        //debugRenderer = new Box2DDebugRenderer();
        debugRenderer = new SBox2DDebugRenderer(BOX_TO_PIXEL);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        
        logger = new FPSLogger();
        
       skeleton = new Skeleton("skeleton1", new Vector2(), null, world);
       skeleton.mover = new TimelineMover();
      platforms = new ArrayList<Body>();
       
       Iterator<Joint> joints = world.getJoints(); 
       for( int i = 0; i < 5; ++i ){
       	for ( int j = 0; j < 5; ++j ){
		        BodyDef bDef = new BodyDef();
		        bDef.position.set(new Vector2( ( 250*i+200 )*PIXEL_TO_BOX,( 25*j+200 )*PIXEL_TO_BOX ) );
		        bDef.type = BodyType.DynamicBody;
		        Body b = world.createBody( bDef );
		        PolygonShape bBox = new PolygonShape(); 
		        bBox.setAsBox( 100*PIXEL_TO_BOX, 5*PIXEL_TO_BOX );
		        b.createFixture( bBox,1.0f );
		        //platforms.add(b);
		        
		        
		        
		        RevoluteJointDef jointDef = new RevoluteJointDef();
		        //jointDef.initialize(b, groundBody, b.getWorldCenter());
		        jointDef.bodyA = b;
		        jointDef.bodyB = skeleton.body;
		        jointDef.collideConnected = false;
		        jointDef.localAnchorA.set( new Vector2()); //attach joint to center to platform
		        jointDef.localAnchorB.set( new Vector2((250*i+200)*PIXEL_TO_BOX,(25*j+200)*PIXEL_TO_BOX));//attach to center of platform
		        platforms.add(b);
		        skeleton.addBoneAndJoint( new Entity("b"+i, b), world.createJoint(jointDef) );
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
		
		
		if ( Gdx.input.isKeyPressed(Input.Keys.X) ){
			skeleton.body.setTransform(skeleton.body.getTransform().getPosition().add(0f,0.01f),skeleton.body.getTransform().getRotation());
			skeleton.wakeSkeleton();
			//groundBody.setTransform(0f, -0.01f, 0);
			//Gdx.app.log("dude", "DUDE!");
			
		}
		
		if ( Gdx.input.isKeyPressed(Input.Keys.Z) ){
			//groundBody.setTransform(0f, 0.01f, 0);
			skeleton.body.setTransform(skeleton.body.getTransform().getPosition().add(0f,-0.01f),skeleton.body.getTransform().getRotation());
			skeleton.wakeSkeleton();
		}
		
		if ( Gdx.input.isKeyPressed(Input.Keys.C) ){
			skeleton.body.setTransform(skeleton.body.getTransform().getPosition(),skeleton.body.getTransform().getRotation()+0.01f);
			//groundBody.setTransform(0f, -0.01f, 0);
			//Gdx.app.log("dude", "DUDE!");
			skeleton.wakeSkeleton();
		}
		
		if ( Gdx.input.isKeyPressed(Input.Keys.V) ){
			//groundBody.setTransform(0f, 0.01f, 0);
			skeleton.body.setTransform(skeleton.body.getTransform().getPosition(),skeleton.body.getTransform().getRotation()-0.01f);
			//Gdx.app.log("dude", "DUDE!");
			skeleton.wakeSkeleton();
		}
		

		player.update();
		tp.update();
		rp.update();
		cp.update();
		//sp.update();
		skeleton.update();
		
		batch.setProjectionMatrix(cam.combined());
		//batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		
		// test drawing the texture by uncommenting the next line:
		//tp.draw(batch);
		player.draw(batch);
		
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
