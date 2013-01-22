package com.blindtigergames.werescrewed.screens;

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
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Player;
import com.blindtigergames.werescrewed.platforms.Box;
import com.blindtigergames.werescrewed.platforms.ComplexPlatform;
import com.blindtigergames.werescrewed.platforms.PlatformBuilder;
import com.blindtigergames.werescrewed.platforms.RoomPlatform;
import com.blindtigergames.werescrewed.platforms.Skeleton;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.screws.StructureScrew;

public class PhysicsTestScreen implements com.badlogic.gdx.Screen {

	/***
	 * Box2D to pixels conversion *************
	 * 
	 * This number means 1 meter equals 256 pixels. That means the biggest
	 * in-game object (10 meters) we can use is 2560 pixels wide, which is much
	 * bigger than our max screen resolution so it should be enough.
	 */
	public static final float BOX_TO_PIXEL = 256f;
	public static final float PIXEL_TO_BOX = 1 / BOX_TO_PIXEL;
	public static final float DEGTORAD = 0.0174532925199432957f;
	public static final float RADTODEG = 57.295779513082320876f;

	OrthographicCamera camera;
	Camera cam;
	SpriteBatch batch;
	Texture texture;
	Texture playerTexture;
	Sprite sprite;
	World world;
	MyContactListener MCL;
	SBox2DDebugRenderer debugRenderer;
	Body playerBody;
	Entity playerEntity;
	Player player;
	TiledPlatform tp, tp2;
	RoomPlatform rp;
	ComplexPlatform cp;
	// ShapePlatform sp;
	Box box;

	Texture screwTex;
	Texture background;
	StructureScrew structScrew;
	Skeleton skeleton;

	FPSLogger logger;

	private final Vector2 dec = new Vector2( .5f, 0 );
	private final Vector2 acc = new Vector2( .3f, 0 );
	private final Vector2 max = new Vector2( 1f, 0 );

	public PhysicsTestScreen( ) {
		System.out.println( "GameScreen starting" );
		float zoom = 1.0f;
		float w = Gdx.graphics.getWidth( ) / zoom;
		float h = Gdx.graphics.getHeight( ) / zoom;

		texture = new Texture( Gdx.files.internal( "data/rletter.png" ) );
		// takes in width, height
		// cam = new Camera(w, h);
		batch = new SpriteBatch( );

		world = new World( new Vector2( 0, -100 ), true );
		MCL = new MyContactListener( );
		world.setContactListener( MCL );
		String name = "player";

		player = new Player( world, new Vector2( 1.0f, 1.0f ), name );

		texture = new Texture( Gdx.files.internal( "data/rletter.png" ) );

		tp = new TiledPlatform( "plat", new Vector2( 2.0f, 0.5f ), texture,
				10, 1, world );

		screwTex = new Texture( Gdx.files.internal( "data/screw.png" ) );
		background = new Texture( Gdx.files.internal( "data/libgdx.png" ) );
		skeleton = new Skeleton( "", Vector2.Zero, background, world );
		structScrew = new StructureScrew( "", tp.body.getPosition( ), screwTex,
				25, tp, skeleton, world );

		cam = new Camera( w, h, player );
		// tp = new TiledPlatform( "plat", new Vector2(5.0f, 40.0f), texture, 1,
		// 2, world );
		rp = new RoomPlatform( "room", new Vector2( -1.0f, 1.0f ), texture, 1,
				10, world );
		// cp = new ComplexPlatform( "bottle", new Vector2(0.0f, 3.0f), texture,
		// 1, world, "bottle" );
		// sp = new ShapePlatform( "trap", new Vector2( 1.0f, 1.0f), texture,
		// world, Shapes.trapezoid, 0.5f);
		box = new Box( "box", new Vector2( 80.0f, 0.0f ), texture, world );
		if ( box.body.getUserData( ) instanceof Box ) {
			System.out.print( "worked" );
		} else
			System.out.print( "nope" );
		tp2 = new PlatformBuilder()
			.setPosition( 0.0f, 0.0f )
			.setDimensions( 100, 1 )
			.setTexture( texture )
			.buildTilePlatform( world );


		// make sure you uncomment the next two lines debugRenderer = new
		// SBox2DDebugRenderer(BOX_TO_PIXEL); for physics world
		// debugRenderer = new Box2DDebugRenderer();
		debugRenderer = new SBox2DDebugRenderer( BOX_TO_PIXEL );
		Gdx.app.setLogLevel( Application.LOG_DEBUG );

		logger = new FPSLogger( );

	}

	@Override
	public void render( float delta ) {
		Gdx.gl20.glClearColor( 0.0f, 0f, 0.0f, 1.0f );
		Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT );

		cam.update( );

		if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) ) {
			ScreenManager.getInstance( ).show( Screen.PAUSE );
		}
		if ( Gdx.input.isKeyPressed( Keys.P ) ) {
			System.exit( 0 );
		}

		player.update( );

		structScrew.update( );

		//
	    tp.update();
		rp.update( );
		// cp.update();
		// sp.update();
		box.update( );

		batch.setProjectionMatrix( cam.combined( ) );
		// batch.setProjectionMatrix(camera.combined);
		batch.begin( );

		structScrew.draw( batch );
		// sprite.draw(batch);
		// Drawing the player here
		// playerEntity.draw(batch);
		// player.draw(batch);

		// test drawing the texture by uncommenting the next line:
		// tp.draw(batch);
		player.draw( batch );

		batch.end( );

		// logger.log();
		debugRenderer.render( world, cam.combined( ) );

		world.step( 1 / 60f, 6, 2 ); // step our physics calculations
		// Gdx.app.debug("Physics",
		// "delta = "+Gdx.app.getGraphics().getDeltaTime());
	}

	@Override
	public void resize( int width, int height ) {
	}

	@Override
	public void show( ) {

	}

	@Override
	public void hide( ) {
	}

	@Override
	public void pause( ) {
	}

	@Override
	public void resume( ) {
	}

	@Override
	public void dispose( ) {
	}

}