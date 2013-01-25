package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

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
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityManager;
import com.blindtigergames.werescrewed.entity.Player;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.SlidingMotorMover;
import com.blindtigergames.werescrewed.joint.PrismaticJointBuilder;
import com.blindtigergames.werescrewed.platforms.Box;
import com.blindtigergames.werescrewed.platforms.ComplexPlatform;
import com.blindtigergames.werescrewed.platforms.PlatformBuilder;
import com.blindtigergames.werescrewed.platforms.RoomPlatform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.puzzles.PuzzleManager;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.screws.StructureScrew;

public class PhysicsTestScreen implements com.badlogic.gdx.Screen {

	/***
	 * Box2D to pixels conversion.
	 * 
	 * This number means 1 meter equals 256 pixels. That means the biggest
	 * in-game object (10 meters) we can use is 2560 pixels wide, which is much
	 * bigger than our max screen resolution so it should be enough.
	 */
	public static final float BOX_TO_PIXEL = 256f;
	public static final float PIXEL_TO_BOX = 1 / BOX_TO_PIXEL;
	public static final float DEG_TO_RAD = 0.0174532925199432957f;
	public static final float RAD_TO_DEG = 57.295779513082320876f;

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
	TiledPlatform tp, ground, movingTP;
	RoomPlatform rp;
	ComplexPlatform cp;
	// ShapePlatform sp;
	Box box;
	SlidingMotorMover sm;
	PuzzleManager pm;
	
	Texture screwTex;
	Texture background;
	StructureScrew structScrew;
	PuzzleScrew puzzleScrew;
	Skeleton skeleton;
	ArrayList< StrippedScrew > climbingScrews = new ArrayList< StrippedScrew >( );

	FPSLogger logger;

	private final Vector2 dec = new Vector2( .5f, 0 );
	private final Vector2 acc = new Vector2( .3f, 0 );
	private final Vector2 max = new Vector2( 1f, 0 );

	public PhysicsTestScreen( ) {
		System.out.println( "GameScreen starting" );
		float zoom = 1.0f;
		float w = Gdx.graphics.getWidth( ) / zoom;
		float h = Gdx.graphics.getHeight( ) / zoom;
		


		cam = new Camera( w, h );
		texture = new Texture( Gdx.files.internal( "data/rletter.png" ) );
		// takes in width, height
		// cam = new Camera(w, h);
		batch = new SpriteBatch( );
		
		world = new World( new Vector2( 0, -45 ), true );
		MCL = new MyContactListener( );
		world.setContactListener( MCL );
		skeleton = new Skeleton( "", Vector2.Zero, background, world );
		pm = new PuzzleManager();
		String name = "player";
		
		player = new Player( world, new Vector2( 1.0f, 1.0f ), name );

		texture = new Texture( Gdx.files.internal( "data/rletter.png" ) );

		tp = new PlatformBuilder( world )
				.setPosition( 2.0f, 0.5f )
				.setDimensions( 10, 1 )
				.setTexture( texture )
				.setName( "tp" )
				.setResitituion( 0.0f )
				.buildTilePlatform(  );

		movingTP = new PlatformBuilder( world )
				.setPosition( 2.0f, 1.1f )
				.setDimensions( 10, 1 )
				.setTexture( texture )
				.setName( "movingTP" )
				.setResitituion( 0.0f )
				.buildTilePlatform( );
		
		movingTP.body.setType( BodyType.DynamicBody );
        PrismaticJoint pistonJoint = new PrismaticJointBuilder( world )
        .skeleton( skeleton )
        .bodyB( (Entity)movingTP )
        .anchor( movingTP.body.getWorldCenter() )
        .axis( 0, 1 )
        .motor( true )
        .limit( true )
        .upper( 0.5f )
        .motorSpeed( 1 )
        .build();
		
		movingTP.setMover( new SlidingMotorMover( PuzzleType.PRISMATIC_SLIDER, pistonJoint ) );
		
        PrismaticJoint puzzleJoint = new PrismaticJointBuilder( world )
        .skeleton( skeleton )
        .bodyB( (Entity)movingTP )
        .anchor( movingTP.body.getWorldCenter() )
        .axis( 1, 0 )
        .motor( true )
        .limit( true )
        .upper( 0.5f )
        .motorSpeed( 1 )
        .build();
        
		sm = new SlidingMotorMover ( PuzzleType.PRISMATIC_SLIDER, puzzleJoint );
		
		pm.addEntity( "001_0", movingTP );
		pm.addMover( "001_0", sm );

		screwTex = new Texture( Gdx.files.internal( "data/screw.png" ) );
		background = new Texture( Gdx.files.internal( "data/libgdx.png" ) );
		structScrew = new StructureScrew( "", tp.body.getPosition( ), screwTex,
				50, tp, skeleton, world );
		puzzleScrew = new PuzzleScrew( "001", new Vector2( 1.0f, 0.2f ), screwTex,
				50, skeleton, world );

		float x1 = 1.75f;
		float x2 = 2.25f;
		float y1 = 0.6f;
		float dy = 0.7f;
		for ( int i = 0; i < 10; i++ ) {
			if ( i % 2 == 0 ) {
				climbingScrews.add( new StrippedScrew( "",
						new Vector2( x1, y1 ), screwTex, skeleton, world ) );
			} else {
				climbingScrews.add( new StrippedScrew( "",
						new Vector2( x2, y1 ), screwTex, skeleton, world ) );
			}
			y1 += dy;
		}
		// tp = new TiledPlatform( "plat", new Vector2(5.0f, 40.0f), texture, 1,
		// 2, world );
		rp = new PlatformBuilder( world )
				.setPosition( -1.0f, 1.01f )
				.setName( "rp" )
				.setDimensions( 1, 10 )
				.setTexture( texture )
				.setResitituion( 0.0f )
				.buildRoomPlatform( );

		// cp = new ComplexPlatform( "bottle", new Vector2(0.0f, 3.0f), texture,
		// 1, world, "bottle" );
		// sp = new ShapePlatform( "trap", new Vector2( 1.0f, 1.0f), texture,
		// world, Shapes.trapezoid, 0.5f);
		box = new Box( "box", new Vector2( 120.0f, 160.0f ), texture, world );
		box.setRestitution( 0.0f );
		if ( box.body.getUserData( ) instanceof Box ) {
			System.out.print( "worked" );
		} else
			System.out.print( "nope" );
		
		
		ground = new PlatformBuilder( world )
				.setPosition( 0.0f, 0.0f )
				.setName( "ground" )
				.setDimensions( 100, 1 )
				.setTexture( texture )
				.setResitituion( 0.0f )
				.buildTilePlatform( );

		// make sure you uncomment the next two lines debugRenderer = new
		// SBox2DDebugRenderer(BOX_TO_PIXEL); for physics world
		// debugRenderer = new Box2DDebugRenderer();
		debugRenderer = new SBox2DDebugRenderer( BOX_TO_PIXEL );
		debugRenderer.setDrawJoints( false );
		Gdx.app.setLogLevel( Application.LOG_DEBUG );

		logger = new FPSLogger( );

	}

	@Override
	public void render( float deltaTime ) {
		Gdx.gl20.glClearColor( 0.0f, 0f, 0.0f, 1.0f );
		Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT );

		cam.update( );

		if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) ) {
			ScreenManager.getInstance( ).show( Screen.PAUSE );
		}
		if ( Gdx.input.isKeyPressed( Keys.P ) ) {
			System.exit( 0 );
		}

		pm.update( deltaTime );
		
		player.update( deltaTime );

		structScrew.update( deltaTime );
		puzzleScrew.update( deltaTime );
		for ( StrippedScrew s : climbingScrews ) {
			s.update( deltaTime );
		}

		movingTP.update( deltaTime );
		//
		tp.update( deltaTime );
		rp.update( deltaTime );
		// cp.update();
		// sp.update();
	//	box.update( deltaTime );

		batch.setProjectionMatrix( cam.combined( ) );
		// batch.setProjectionMatrix(camera.combined);
		batch.begin( );

		for ( StrippedScrew s : climbingScrews ) {
			s.draw( batch );
		}

		puzzleScrew.draw( batch );
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