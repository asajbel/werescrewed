package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.PulleyJointDef;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.checkpoints.CheckPoint;
import com.blindtigergames.werescrewed.checkpoints.ProgressManager;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.PolySprite;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.RootSkeleton;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.action.EntityActivateMoverAction;
import com.blindtigergames.werescrewed.entity.action.EntityDeactivateMoverAction;
import com.blindtigergames.werescrewed.entity.builders.EventTriggerBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.builders.RopeBuilder;
import com.blindtigergames.werescrewed.entity.builders.ScrewBuilder;
import com.blindtigergames.werescrewed.entity.builders.SkeletonBuilder;
import com.blindtigergames.werescrewed.entity.mover.LerpMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.mover.PistonTweenMover;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.RotateByDegree;
import com.blindtigergames.werescrewed.entity.mover.SlidingMotorMover;
import com.blindtigergames.werescrewed.entity.mover.puzzle.PuzzlePistonTweenMover;
import com.blindtigergames.werescrewed.entity.tween.EntityAccessor;
import com.blindtigergames.werescrewed.entity.tween.PathBuilder;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.hazard.Spikes;
import com.blindtigergames.werescrewed.hazard.builders.SpikesBuilder;
import com.blindtigergames.werescrewed.joint.JointFactory;
import com.blindtigergames.werescrewed.joint.RevoluteJointBuilder;
import com.blindtigergames.werescrewed.particles.Steam;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.rope.Rope;
import com.blindtigergames.werescrewed.screws.BossScrew;
import com.blindtigergames.werescrewed.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.screws.StructureScrew;
import com.blindtigergames.werescrewed.util.Util;

public class PhysicsTestScreen implements com.badlogic.gdx.Screen {

	// FIELDS

	// Variables

	private Camera cam;
	private SpriteBatch batch;
	private Texture testTexture;
	private World world;
	private MyContactListener contactListener;
	private SBox2DDebugRenderer debugRenderer;
	private ProgressManager progressManager;
	private Player player1, player2;
	@SuppressWarnings( "unused" )
	RootSkeleton rootSkeleton;
	Skeleton s1;
	Skeleton groundSkeleton;
	PlatformBuilder platBuilder;

	private boolean debug = true;
	private boolean debugTest = true;

	// PolySprite polySprite;

	// ArrayList< TiledPlatform > tp2 = new ArrayList< TiledPlatform >( );

	/**
	 * Defines all necessary components in a screen for testing different
	 * physics-related mechanics
	 */
	public PhysicsTestScreen( ) {
		// Initialize world and variables to allow adding entities
		batch = new SpriteBatch( );
		world = new World( new Vector2( 0, -35 ), true );

		// Entity e = null;
		// if ( e.name == null ){}

		// Initialize camera
		initCamera( );
		Tween.registerAccessor( Platform.class, new PlatformAccessor( ) );
		Tween.registerAccessor( Entity.class, new EntityAccessor( ) );

		// entityManager = new EntityManager( );
		SkeletonBuilder sb = new SkeletonBuilder( world );
		rootSkeleton = sb.buildRoot( );
		groundSkeleton = sb.name("groundSkele").build( );
		s1 = sb.position( 200, 100 ).kinematic( ).name( "movingSkeleton" ).vert( 0, 0 )
				.vert( 200, 0 ).vert( 200, 200 ).vert( 0, 200 ).texBackground( WereScrewedGame.manager.get( "data/common/robot/alphabot_texture_skin.png",Texture.class  )).build( );
		//s1.setMoverAtCurrentState( new PathBuilder().begin( s1 ).target( 0, 200, 2 ).target( -200, 200, 2 ).target(0,0,3).build( ) );
		groundSkeleton.addSkeleton( s1 );
		rootSkeleton.addSkeleton( groundSkeleton );

		platBuilder = new PlatformBuilder( world );
		
		TiledPlatform ground = platBuilder.position( 0,0 ).name("ground").dimensions( 100,1 ).buildTilePlatform( );
		groundSkeleton.addPlatform( ground );
		
		TiledPlatform plat = platBuilder.position( 350,200 ).name( "movingPlat" ).dimensions( 4,1 ).buildTilePlatform( );
		s1.addPlatform( plat );
		plat.setMoverAtCurrentState( new PathBuilder( ).begin(plat).target( 100, 100, 1 ).target( -100, 100, 2 ).target( 0, 0, 1.7f ).target( -200, -200, 1.4f ).target( 0, 0, 1f ).build( ) );
		
		//plat = platBuilder.position( -450, 250 ).name( "non-moving plat").buildTilePlatform( );
		//groundSkeleton.addPlatform( plat );
		
		//groundSkeleton.addSkeleton( sb.name( "non-moving skeleton" ).position( -200,-400 ).build( ) );
		
		
		// Initialize listeners
		contactListener = new MyContactListener( );
		world.setContactListener( contactListener );

		debugRenderer = new SBox2DDebugRenderer( Util.BOX_TO_PIXEL );

		debugRenderer.setDrawJoints( false );

		Gdx.app.setLogLevel( Application.LOG_DEBUG );
		
		player1 = new PlayerBuilder( ).name( "player1" ).world( world )
				.position( 0f, 500f).buildPlayer( );
		player2 = new PlayerBuilder( ).name( "player2" ).world( world )
				.position( 0.0f, 500.0f ).buildPlayer( );

	}

	/**
	 * Initializes camera settings
	 */
	private void initCamera( ) {
		float zoom = 1.0f;
		float width = Gdx.graphics.getWidth( ) / zoom;
		float height = Gdx.graphics.getHeight( ) / zoom;
		cam = new Camera( width, height, world );
	}

	@Override
	public void render( float deltaTime ) {
		if ( Gdx.gl20 != null ) {
			Gdx.gl20.glClearColor( 0.2f, 0.2f, 0.2f, 1.0f );
			Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT );
		} else {
			Gdx.gl10.glClearColor( 0.0f, 0f, 0.0f, 1.0f );
			Gdx.gl10.glClear( GL20.GL_COLOR_BUFFER_BIT );
		}

		cam.update( );

		if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) ) {
			ScreenManager.getInstance( ).show( ScreenType.PAUSE );
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.NUM_1 ) ) {
			ScreenManager.getInstance( ).show( ScreenType.WIN );
		}
		if ( Gdx.input.isKeyPressed( Keys.P ) ) {
			System.exit( 0 );
		}

		if ( Gdx.input.isKeyPressed( Keys.NUM_0 ) ) {
			if ( debugTest )
				debug = !debug;
			debugTest = false;
		} else
			debugTest = true;
		
		if ( Gdx.input.isKeyPressed( Input.Keys.Z ) ) {
			groundSkeleton.rotateBy( -0.01f );
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.X ) ) {
			groundSkeleton.rotateBy( 0.01f);
		}
		if ( Gdx.input.isKeyPressed( Keys.C ) ) {
			groundSkeleton.translateBy( -1f, -1f );
		}
		if ( Gdx.input.isKeyPressed( Keys.V ) ) {
			groundSkeleton.translateBy( 1f, 1f );
		}

		player1.update( deltaTime );
		player2.update( deltaTime );
		// oldRootSkeleton.update( deltaTime );
		rootSkeleton.update( deltaTime );
		//progressManager.update( deltaTime );
		// spikes.update( deltaTime );
		
		
		batch.setProjectionMatrix( cam.combined( ) );
		batch.begin( );

		// tp2.draw( batch );
		//progressManager.draw( batch );
		rootSkeleton.draw( batch );
		// spikes.draw( batch );
		player1.draw( batch );
		player2.draw( batch );

		batch.end( );

		if ( debug )
			debugRenderer.render( world, cam.combined( ) );

		world.step( 1 / 60f, 6, 3 );
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