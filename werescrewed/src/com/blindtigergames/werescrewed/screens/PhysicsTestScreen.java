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
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Player;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.mover.PistonMover;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.SlidingMotorMover;
import com.blindtigergames.werescrewed.joint.JointFactory;
import com.blindtigergames.werescrewed.joint.PrismaticJointBuilder;
import com.blindtigergames.werescrewed.platforms.Box;
import com.blindtigergames.werescrewed.platforms.ComplexPlatform;
import com.blindtigergames.werescrewed.platforms.PlatformBuilder;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
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
	// RoomPlatform rp;
	ComplexPlatform cp;
	// ShapePlatform sp;
	Box box;
	PlatformBuilder platBuilder;

	Texture screwTex;
	Texture background;
	StructureScrew structScrew;
	PuzzleScrew puzzleScrew;
	Skeleton skeleton;
	Skeleton rootSkeleon;
	ArrayList< StrippedScrew > climbingScrews = new ArrayList< StrippedScrew >( );

	FPSLogger logger;

	public PhysicsTestScreen( ) {
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
		rootSkeleon = new Skeleton( "", Vector2.Zero, null, world );
		String name = "player";
		platBuilder = new PlatformBuilder( world );

		player = new Player( name, world, new Vector2( 1.0f, 1.0f ), null );

		texture = new Texture( Gdx.files.internal( "data/rletter.png" ) );

		tp = platBuilder.setPosition( 400.0f, 100.0f ).setDimensions( 10, 1 )
				.setTexture( texture ).setName( "tp" ).setResitituion( 0.0f )
				.buildTilePlatform( );

		movingTP = platBuilder.setPosition( 350.0f, 170.0f )
				.setDimensions( 10, 1 ).setTexture( texture )
				.setName( "movingTP" ).setResitituion( 0.0f )
				.buildTilePlatform( );

		movingTP.body.setType( BodyType.DynamicBody );

		screwTex = new Texture( Gdx.files.internal( "data/screw.png" ) );
		background = new Texture( Gdx.files.internal( "data/libgdx.png" ) );
		structScrew = new StructureScrew( "", tp.body.getPosition( ), screwTex,
				50, tp, skeleton, world );
		puzzleScrew = new PuzzleScrew( "001", new Vector2( 0.0f, 0.2f ),
				screwTex, 50, skeleton, world );

		Vector2 axis = new Vector2( 1, 0 );
		PrismaticJointDef jointDef = new PrismaticJointDef( );
		jointDef.initialize( movingTP.body, skeleton.body,
				movingTP.body.getPosition( ), axis );
		jointDef.enableMotor = true;
		jointDef.enableLimit = true;
		jointDef.lowerTranslation = -1.5f;
		jointDef.upperTranslation = 1.0f;
		jointDef.motorSpeed = 7.0f;

		puzzleScrew.puzzleManager.addEntity( movingTP );
		puzzleScrew.puzzleManager.addJointDef( jointDef );

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
		// rp = platBuilder
		// .setPosition( -200.0f, 101.0f )
		// .setName( "rp" )
		// .setDimensions( 1, 10 )
		// .setTexture( texture )
		// .setResitituion( 0.0f )
		// .buildRoomPlatform( );

		// cp = new ComplexPlatform( "bottle", new Vector2(0.0f, 3.0f), texture,
		// 1, world, "bottle" );
		// sp = new ShapePlatform( "trap", new Vector2( 1.0f, 1.0f), texture,
		// world, Shapes.trapezoid, 0.5f);
		box = new Box( "box", new Vector2( 120.0f, 130.0f ), texture, world );
		box.setRestitution( 0.0f );
		// if ( box.body.getUserData( ) instanceof Box ) {
		// System.out.print( "worked" );
		// } else
		// System.out.print( "nope" );

		ground = platBuilder.setPosition( 0.0f, 0.0f ).setName( "ground" )
				.setDimensions( 100, 1 ).setTexture( texture )
				.setResitituion( 0.0f ).buildTilePlatform( );
		skeleton.addPlatformFixed( ground );
		// skeleton.addPlatformFixed( tp );

		/**
		 * Uncomment if you don't want stew's moving platforms in your way!
		 */
		buildMoverPlatforms( );
		rootSkeleon.addSkeleton( skeleton );

		// make sure you uncomment the next two lines debugRenderer = new
		// SBox2DDebugRenderer(BOX_TO_PIXEL); for physics world
		// debugRenderer = new Box2DDebugRenderer();
		debugRenderer = new SBox2DDebugRenderer( BOX_TO_PIXEL );
		debugRenderer.setDrawJoints( false );
		Gdx.app.setLogLevel( Application.LOG_DEBUG );

		logger = new FPSLogger( );

	}

	void buildMoverPlatforms( ) {
		TiledPlatform slidingPlatform = new PlatformBuilder( world )
				.setWidth( 10 ).setHeight( 1 ).setOneSided( true )
				.setPosition( -1000, 200 ).setTexture( texture )
				.setFriction( 1f ).buildTilePlatform( );
		slidingPlatform.body.setType( BodyType.DynamicBody );

		PrismaticJointDef prismaticJointDef = JointFactory
				.constructSlidingJointDef( skeleton.body, slidingPlatform.body,
						slidingPlatform.body.getWorldCenter( ), new Vector2( 1,
								0 ), 1.0f, 1f );
		PrismaticJoint j = ( PrismaticJoint ) world
				.createJoint( prismaticJointDef );
		skeleton.addBoneAndJoint( slidingPlatform, j );
		slidingPlatform.setMover( new SlidingMotorMover(
				PuzzleType.PRISMATIC_SLIDER, j ) );

		TiledPlatform skeletonTest1 = new PlatformBuilder( world )
				.setWidth( 10 ).setHeight( 1 ).setFriction( 1f )
				.setOneSided( false ).setPosition( -500, -200 )
				.setTexture( texture ).buildTilePlatform( );
		skeletonTest1.body.setType( BodyType.DynamicBody );
		skeleton.addPlatformFixed( skeletonTest1 );

		TiledPlatform skeletonTest2 = new PlatformBuilder( world )
				.setWidth( 10 ).setHeight( 1 ).setOneSided( false )
				.setPosition( 500, 300 ).setTexture( texture ).setFriction( 1f )
				.buildTilePlatform( );
		skeletonTest2.body.setType( BodyType.DynamicBody );
		skeleton.addPlatformRotatingCenter( skeletonTest2 );

		/*
		 * TODO: FIX PLATFORM DENSITY
		 */

		PlatformBuilder builder = new PlatformBuilder( world ).setWidth( 1 )
				.setHeight( 3 ).setOneSided( false )
				// .setPosition( (-500f-i*40)*PIXEL_TO_BOX, 150f*PIXEL_TO_BOX )
				.setTexture( texture ).setFriction( 1f );
		// .buildTilePlatform( world );

		PrismaticJointBuilder jointBuilder = new PrismaticJointBuilder( world )
				.skeleton( skeleton ).axis( 0, 1 ).motor( true ).limit( true )
				.upper( 1 ).motorSpeed( 1 );
		for ( int i = 0; i < 10; ++i ) {
			TiledPlatform piston = builder.setPosition( ( -100f - i * 40 ),
					220f ).buildTilePlatform( );

			piston.body.setType( BodyType.DynamicBody );
			PrismaticJoint pistonJoint = jointBuilder.bodyB( ( Entity ) piston )
					.anchor( piston.body.getWorldCenter( ) ).build( );
			// Something is still not quite right with this, try replacing 3
			// with 0.
			piston.setMover( new PistonMover( pistonJoint, 0f, i / 10.0f + 2f ) );
			piston.body.setSleepingAllowed( false );
			skeleton.addBoneAndJoint( piston, pistonJoint );
		}

		ComplexPlatform gear = new ComplexPlatform( "gear", new Vector2(
				1000 * PIXEL_TO_BOX, 300 * PIXEL_TO_BOX ), null, 3, world,
				"gearSmall" );
		gear.body.setType( BodyType.DynamicBody );
		skeleton.addPlatformRotatingCenterWithRot( gear, 1f );

	}

	@Override
	public void render( float deltaTime ) {
		Gdx.gl10.glClearColor( 0.0f, 0f, 0.0f, 1.0f );
		Gdx.gl10.glClear( GL20.GL_COLOR_BUFFER_BIT );

		cam.update( );

		if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) ) {
			ScreenManager.getInstance( ).show( ScreenType.PAUSE );
		}
		if ( Gdx.input.isKeyPressed( Keys.P ) ) {
			System.exit( 0 );
		}

		player.update( deltaTime );

		structScrew.update( deltaTime );
		puzzleScrew.update( deltaTime );
		for ( StrippedScrew s : climbingScrews ) {
			s.update( deltaTime );
		}

		rootSkeleon.update( deltaTime );
		movingTP.update( deltaTime );
		//
		tp.update( deltaTime );
		// rp.update( deltaTime );
		// cp.update();
		// sp.update();
		// box.update( deltaTime );

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