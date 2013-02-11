package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityManager;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.mover.PistonMover;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.SlidingMotorMover;
import com.blindtigergames.werescrewed.joint.JointFactory;
import com.blindtigergames.werescrewed.joint.PrismaticJointBuilder;
import com.blindtigergames.werescrewed.platforms.ComplexPlatform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.screws.StructureScrew;
import com.blindtigergames.werescrewed.skeleton.Skeleton;
import com.blindtigergames.werescrewed.util.Util;
/**
 * Screen to test out moving platforms and skeletons
 * 
 * Debug Keys in use:
 * z - move skeleton down
 * x - move skeleton up
 * c - rotate skeleton left
 * v - rotate skeleton right
 * 
 * @author stew
 * 
 */
public class IMoverGameScreen implements com.badlogic.gdx.Screen {

	private Camera cam;
	private SpriteBatch batch;
	private Texture texture;
	private World world;
	private MyContactListener mcl;
	private SBox2DDebugRenderer debugRenderer;
	private Player player;
	private TiledPlatform tp, ground, movingTP;
	private PlatformBuilder platBuilder;
	private EntityManager entityManager;

	private Texture background;
	private StructureScrew structScrew;
	private PuzzleScrew puzzleScrew;
	private Skeleton skeleton;
	private Skeleton rootSkeleton;
	private ArrayList< StrippedScrew > climbingScrews = new ArrayList< StrippedScrew >( );

	public IMoverGameScreen( ) {
		System.out.println( "Physics Test Screen starting" );
		float zoom = 1.0f;
		float width = Gdx.graphics.getWidth( ) / zoom;
		float height = Gdx.graphics.getHeight( ) / zoom;

		cam = new Camera( width, height );
		texture = WereScrewedGame.manager.get(
				"assets/common/data/rletter.png", Texture.class);
		batch = new SpriteBatch( );
		entityManager = new EntityManager( );

		world = new World( new Vector2( 0, -45 ), true );
		mcl = new MyContactListener( );
		world.setContactListener( mcl );
		skeleton = new Skeleton( "", Vector2.Zero, background, world );
		rootSkeleton = new Skeleton( "", Vector2.Zero, null, world );
		platBuilder = new PlatformBuilder( world );
		entityManager.addSkeleton( rootSkeleton.name, rootSkeleton );

		player = new Player( "player", world, new Vector2( 1.0f, 1.0f ) );

		texture = WereScrewedGame.manager.get(
				"assets/common/data/TilesetTest.png", Texture.class);

		tp = platBuilder.position( 350.0f, 100.0f ).dimensions( 10, 1 )
				.texture( texture ).name( "tp" ).resitituion( 0.0f )
				.buildTilePlatform( );

		movingTP = platBuilder.position( 350.0f, 170.0f )
				.dimensions( 10, 1 ).texture( texture )
				.name( "movingTP" ).resitituion( 0.0f )
				.buildTilePlatform( );

		movingTP.body.setType( BodyType.DynamicBody );

		entityManager.addEntity( movingTP.name, movingTP );
		entityManager.addEntity( tp.name, tp );
		entityManager.removeEntity( movingTP.name, movingTP );

		background = WereScrewedGame.manager.get(
				"assets/common/data/libgdx.png",Texture.class);
		structScrew = new StructureScrew( "", tp.body.getPosition( ), 50, tp,
				skeleton, world );
		puzzleScrew = new PuzzleScrew( "001", new Vector2( 0.0f, 0.2f ), 50,
				skeleton, world );
		
	
		entityManager.addEntity( structScrew.name, structScrew );


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
				climbingScrews.add( new StrippedScrew( "", world, new Vector2(
						x1, y1 ), skeleton ) );
			} else {
				climbingScrews.add( new StrippedScrew( "", world, new Vector2(
						x2, y1 ), skeleton ) );
			}
			y1 += dy;
		}

		for ( StrippedScrew ss : climbingScrews ) {
			skeleton.addStrippedScrew( ss );
		}

		ground = platBuilder.position( 0.0f, 0.0f ).name( "ground" )
				.dimensions( 100, 1 ).texture( texture )
				.resitituion( 0.0f ).buildTilePlatform( );
		skeleton.addPlatformFixed( ground );
		skeleton.addPlatform( tp ); // Tp already has a structureScrew holding
									// it up

		/*
		 * Comment if you don't want stew's moving platforms in your way!
		 */
		buildMoverPlatforms( );
		rootSkeleton.addSkeleton( skeleton );

		debugRenderer = new SBox2DDebugRenderer( Util.BOX_TO_PIXEL );
		debugRenderer.setDrawJoints( false );
		Gdx.app.setLogLevel( Application.LOG_DEBUG );

		new FPSLogger( );

	}

	void buildMoverPlatforms( ) {
		TiledPlatform slidingPlatform = platBuilder.width( 10 )
				.height( 1 ).oneSided( true ).position( -1000, 200 )
				.texture( texture ).friction( 1f ).buildTilePlatform( );
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

		TiledPlatform skeletonTest1 = platBuilder.width( 10 ).height( 1 )
				.friction( 1f ).oneSided( false )
				.position( -500, -200 ).texture( texture )
				.buildTilePlatform( );
		skeletonTest1.body.setType( BodyType.DynamicBody );
		skeleton.addPlatformFixed( skeletonTest1 );

		TiledPlatform skeletonTest2 = platBuilder.width( 10 ).height( 1 )
				.oneSided( false ).position( 500, 300 )
				.texture( texture ).friction( 1f ).buildTilePlatform( );
		skeletonTest2.body.setType( BodyType.DynamicBody );
		skeleton.addPlatformRotatingCenter( skeletonTest2 );

		/*
		 * TODO: FIX PLATFORM DENSITY
		 */

		platBuilder.reset( );

		PlatformBuilder builder = platBuilder.width( 1 ).height( 3 )
				.oneSided( false )
				// .setPosition( (-500f-i*40)*PIXEL_TO_BOX, 150f*PIXEL_TO_BOX )
				.texture( texture ).friction( 1f );
		// .buildTilePlatform( world );

		PrismaticJointBuilder jointBuilder = new PrismaticJointBuilder( world )
				.skeleton( skeleton ).axis( 0, 1 ).motor( true ).limit( true )
				.upper( 1 ).motorSpeed( 1 );
		for ( int i = 0; i < 10; ++i ) {
			TiledPlatform piston = builder.position( ( -100f - i * 40 ),
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
				1000 * Util.PIXEL_TO_BOX, 300 * Util.PIXEL_TO_BOX ), null, 3, world,
				"gearSmall" );
		gear.body.setType( BodyType.DynamicBody );
		skeleton.addPlatformRotatingCenterWithRot( gear, 1f );

	}

	@Override
	public void render( float deltaTime ) {
		Gdx.gl20.glClearColor( 0.0f, 0f, 0.0f, 1.0f );
		Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT );

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
		entityManager.update( deltaTime );

		// ONLY FOR TESTING, EVERYTHING IN WORLD IS IN A SKELETON (THEREFORE CAN
		// MOVE)
		if ( Gdx.input.isKeyPressed( Input.Keys.U ) ) {
			rootSkeleton.translate( 0.0f, 0.01f );
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.J ) ) {
			rootSkeleton.translate( 0.0f, -0.01f );
		}

		batch.setProjectionMatrix( cam.combined( ) );
		batch.begin( );

		puzzleScrew.draw( batch );
		rootSkeleton.draw( batch );
		player.draw( batch );

		batch.end( );

		// logger.log();
		debugRenderer.render( world, cam.combined( ) );

		world.step( 1 / 60f, 6, 2 ); // step our physics calculations
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