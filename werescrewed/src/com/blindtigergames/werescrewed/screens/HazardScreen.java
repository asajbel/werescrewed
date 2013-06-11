package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.camera.AnchorList;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.checkpoints.CheckPoint;
import com.blindtigergames.werescrewed.checkpoints.ProgressManager;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.action.AnchorActivateAction;
import com.blindtigergames.werescrewed.entity.action.AnchorDeactivateAction;
import com.blindtigergames.werescrewed.entity.action.DestroyPlatformJointAction;
import com.blindtigergames.werescrewed.entity.action.RemoveEntityAction;
import com.blindtigergames.werescrewed.entity.builders.EventTriggerBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.hazard.Electricity;
import com.blindtigergames.werescrewed.entity.hazard.Fire;
import com.blindtigergames.werescrewed.entity.hazard.Hazard;
import com.blindtigergames.werescrewed.entity.hazard.Spikes;
import com.blindtigergames.werescrewed.entity.hazard.builders.HazardBuilder;
import com.blindtigergames.werescrewed.entity.mover.RotateTweenMover;
import com.blindtigergames.werescrewed.entity.particles.Steam;
import com.blindtigergames.werescrewed.entity.platforms.Pipe;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.entity.screws.PowerScrew;
import com.blindtigergames.werescrewed.entity.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.entity.screws.StructureScrew;
import com.blindtigergames.werescrewed.entity.tween.EntityAccessor;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.eventTrigger.PowerSwitch;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.level.Level;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.util.Metrics;
import com.blindtigergames.werescrewed.util.Util;

public class HazardScreen implements com.badlogic.gdx.Screen {

	public ScreenType screenType;
	private Camera cam;
	private SpriteBatch batch;
	private Texture testTexture;
	private World world;
	private MyContactListener contactListener;
	private ProgressManager progressManager;
	private SBox2DDebugRenderer debugRenderer;
	private Player player1, player2;
	private Skeleton skeleton;
	private Skeleton rootSkeleton;
	private TiledPlatform ground, crusher;
	private StructureScrew struct1, struct2;
	private PlatformBuilder platBuilder;
	@SuppressWarnings( "unused" )
	private Hazard hazard;
	private Fire fire;
	@SuppressWarnings( "unused" )
	private Electricity elec;
	private Spikes spikes, spikes2;
	private HazardBuilder spikesBuilder;
	private boolean debug = true;
	private boolean debugTest = true;
	private Steam testSteam;
	private Pipe testPipe;

	Platform fallingGear1;

	@SuppressWarnings( "unused" )
	private Level level;

	public HazardScreen( ) {
		batch = new SpriteBatch( );
		world = new World( new Vector2( 0, -35 ), true );
		level = new Level( );
		initCamera( );
		Tween.registerAccessor( Platform.class, new PlatformAccessor( ) );
		Tween.registerAccessor( Entity.class, new EntityAccessor( ) );

		skeleton = new Skeleton( "skeleton", Vector2.Zero, null, world );
		skeleton.setFgFade( false );
		rootSkeleton = new Skeleton( "root", Vector2.Zero, null, world );
		rootSkeleton.setFgFade( false );
		platBuilder = new PlatformBuilder( world );
		spikesBuilder = new HazardBuilder( world );
		contactListener = new MyContactListener( );
		world.setContactListener( contactListener );

		player1 = new PlayerBuilder( ).name( "player1" )
				.definition( "red_male" ).world( world ).position( 1800f, 100f )
				.buildPlayer( );
		player2 = new PlayerBuilder( ).name( "player2" )
				.definition( "red_female" ).world( world )
				.position( 1900f, 100.0f ).buildPlayer( );

		Metrics.registerPlayer1( player1.name );
		Metrics.registerPlayer2( player2.name );

		ArrayList< Vector2 > pipePath = new ArrayList< Vector2 >( );
		pipePath.add( new Vector2( 2, 0 ) );
		pipePath.add( new Vector2( 2, -4 ) );
		pipePath.add( new Vector2( 5, -4 ) );

		testPipe = new Pipe( "pipe", new Vector2( 800f, 0f ), pipePath, null,
				world, false );
		skeleton.addKinematicPlatform( testPipe );

		RotateTweenMover rtm1 = new RotateTweenMover( testPipe, 10f, Util.PI,
				2f, true );
		testPipe.setMoverAtCurrentState( rtm1 );

		initTiledPlatforms( );
		initHazards( );
		initCheckPoints( );
		initCrushTest( );
		initParticleEffect( );
		initDeathBarrier( );
//		initPowerScrew( );

		PuzzleScrew pscrew = new PuzzleScrew( "pscrew1", new Vector2( 1550f,
				200f ), 100, skeleton, world, 0, false, Vector2.Zero );
		skeleton.addScrewForDraw( pscrew );

		pscrew = new PuzzleScrew( "pscrew2", new Vector2( 1850f, 200f ), 100,
				skeleton, world, 100, false, Vector2.Zero );
		skeleton.addScrewForDraw( pscrew );

		rootSkeleton.addSkeleton( skeleton );
		debugRenderer = new SBox2DDebugRenderer( Util.BOX_TO_PIXEL );
		debugRenderer.setDrawJoints( false );

		Gdx.app.setLogLevel( Application.LOG_DEBUG );
	}

	private void initCamera( ) {
		float zoom = 1.0f;
		float width = Gdx.graphics.getWidth( ) / zoom;
		float height = Gdx.graphics.getHeight( ) / zoom;
		cam = new Camera( new Vector2( 1000, 0 ), width, height, world );
	}

	private void initTiledPlatforms( ) {
		ground = platBuilder.position( 0.0f, -75 ).name( "ground" )
				.dimensions( 200, 4 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0.0f ).buildTilePlatform( );
		ground.setCategoryMask( Util.CATEGORY_PLATFORMS,
				Util.CATEGORY_EVERYTHING );
		ground.setCrushing( true );
		skeleton.addKinematicPlatform( ground );

		ground = platBuilder.position( 1000, 150 ).name( "ground" )
				.dimensions( 20, 1 ).texture( testTexture ).kinematic( )
				.oneSided( true ).restitution( 0.0f ).buildTilePlatform( );
		ground.setCategoryMask( Util.CATEGORY_PLATFORMS,
				Util.CATEGORY_EVERYTHING );
		skeleton.addKinematicPlatform( ground );

	}

	private void initPowerScrew( ) {
		PowerScrew pscrew = new PowerScrew( "powerscrewtest", new Vector2(
				1800, 100 ), skeleton, world );
		skeleton.addScrewForDraw( pscrew );
	}

	private void initDeathBarrier( ) {
		// death barrier
		EventTriggerBuilder etb = new EventTriggerBuilder( world );
		EventTrigger removeTrigger = etb.name( "removeEntity" ).rectangle( )
				.width( 10 ).height( 50000 ).position( new Vector2( 0, -3200 ) )
				.beginAction( new RemoveEntityAction( ) ).addEntity( player1 )
				.addEntity( player2 ).build( );
		removeTrigger.setCategoryMask( Util.CATEGORY_PLAYER,
				Util.CATEGORY_EVERYTHING );
		rootSkeleton.addEventTrigger( removeTrigger );
	}

	private void initHazards( ) {
		/*
		 * Vector2 tempPos = new Vector2( -1250.0f, 300.0f ); float tempWH =
		 * 100.0f; hazard = new Hazard ( "Test", tempPos, null, world, tempWH,
		 * tempWH, true ); hazard.constructBody( tempPos, tempWH, tempWH );
		 */
		fire = new Fire( "Fire1", new Vector2( -700.0f, -10.0f ), 50, 100,
				world, true );
		fire.particleEffect.changeEffectMaxSize( 2000, 40, 90 ); 
//		fire.particleEffect.addParticles( 600 ); 
//		fire.particleEffect.setEmmisionLifeTime( 3000 );
		// elec = new Electricity( "Elec1", new Vector2( 700.0f, 0.0f ),
		// new Vector2( 700.0f, 150.0f ), world, true );
		/*
		 * saw = new Saws( "Saw1", new Vector2( -2000.0f, 40.0f ), 2, world,
		 * true );
		 */
		spikes = new Spikes( "Spikes1", new Vector2( 1700.0f, 500f ), 1, 3,
				world, true, false, false );
		spikes2 = spikesBuilder.position( 1500.0f, 500f ).dimensions( 2, 1 )
				.up( ).active( ).buildSpikes( );
		// add the spikes to the skeleton
		skeleton.addKinematicPlatform( spikes );
		skeleton.addKinematicPlatform( spikes2 );
	}

	/**
	 * Initializes steam for testing, not on a skeleton at the moment
	 */
	private void initParticleEffect( ) {
		testSteam = new Steam( "testSteam", new Vector2( 2000f, 120f ), 25,
				120, world );
		Skeleton steamSkel = new Skeleton( "steam", new Vector2( 2000f, 100f ),
				null, world );
		steamSkel.setFgFade( false );
		// testSteam.particleEffect.setOffset(0f, -100f);
		rootSkeleton.addSkeleton( steamSkel );

		steamSkel.addMover( new RotateTweenMover( steamSkel, 6f, -Util.PI * 2,
				1f, true ), RobotState.IDLE );

		steamSkel.addSteam( testSteam );
		// Create anchor with start position and buffer as parameters
		Anchor testAnchor = new Anchor( new Vector2( 600f, 200f ), new Vector2(
				-100f, 100f ) );
		// Add to the universally accessible anchor list
		AnchorList.getInstance( ).addAnchor( testAnchor );
		// Set timer in steps
		// testAnchor.setTimer( 200 );
		// Activate it
		// testAnchor.activate( );

		EventTriggerBuilder etb = new EventTriggerBuilder( world );
		EventTrigger et = etb.name( "event1" ).circle( ).radius( 100 )
				.position( new Vector2( 1200f, 20f ) ).repeatable( )
				.beginAction( new AnchorActivateAction( testAnchor ) )
				.endAction( new AnchorActivateAction( testAnchor ) )
				.twoPlayersToActivate( ).build( );
		EventTriggerBuilder etb2 = new EventTriggerBuilder( world );
		EventTrigger et2 = etb2.name( "event2" ).circle( ).radius( 100 )
				.position( new Vector2( 600f, 20f ) ).repeatable( )
				.beginAction( new AnchorDeactivateAction( testAnchor ) )
				.endAction( new AnchorDeactivateAction( testAnchor ) )
				.twoPlayersToDeactivate( ).build( );
		EventTriggerBuilder etb3 = new EventTriggerBuilder( world );
		EventTrigger et3 = etb3.name( "event3" ).circle( ).radius( 100 )
				.position( new Vector2( 2100f, 20f ) ).repeatable( )
				.beginAction( new AnchorDeactivateAction( testAnchor ) )
				.endAction( new AnchorDeactivateAction( testAnchor ) ).build( );

		fallingGear1 = new PlatformBuilder( world ).name( "fallingGear1" )
				.type( EntityDef.getDefinition( "gearSmall" ) )
				.position( 496.0f, 400.0f )
				.texture( EntityDef.getDefinition( "gearSmall" ).getTexture( ) )
				.solid( true ).dynamic( ).buildComplexPlatform( );
		skeleton.addDynamicPlatform( fallingGear1 );

		fallingGear1.addJointToSkeleton( skeleton );

		PowerSwitch ps = new PowerSwitch( "power1", new Vector2( -1000f, 100 ),
				world );
		ps.addEntityToTrigger( fallingGear1 );
		ps.actOnEntity = true;
		ps.setBeginIAction( new DestroyPlatformJointAction( ) );
		ps.setEndIAction( new DestroyPlatformJointAction( ) );
		// AnchorDeactivateAction

		skeleton.addEventTrigger( et );
		skeleton.addEventTrigger( et2 );
		skeleton.addEventTrigger( et3 );
		skeleton.addEventTrigger( ps );

	}

	private void initCrushTest( ) {
		crusher = platBuilder.position( 400.0f, 200.0f ).name( "crusher" )
				.dimensions( 6, 1 ).texture( testTexture ).dynamic( )
				.oneSided( false ).restitution( 0.0f ).buildTilePlatform( );
		crusher.setCrushing( true );
		skeleton.addDynamicPlatform( crusher );
		struct1 = new StructureScrew( "struct1", crusher.getPositionPixel( )
				.add( new Vector2( -50f, 0f ) ), 50, crusher, world,
				new Vector2( 0, 1 ) );
		struct1.addStructureJoint( skeleton );
		struct2 = new StructureScrew( "struct2", crusher.getPositionPixel( )
				.add( new Vector2( 50f, 0f ) ), 50, crusher, world,
				new Vector2( 0, 1 ) );
		struct2.addStructureJoint( skeleton );
		skeleton.addScrewForDraw( struct1 );
		skeleton.addScrewForDraw( struct2 );
	}

	private void initCheckPoints( ) {
		progressManager = new ProgressManager( player1, player2, world );
		skeleton.addCheckPoint( new CheckPoint( "check_01", new Vector2( -512f,
				32f ), skeleton, world, progressManager, "levelStage_0_0" ) );
		skeleton.addCheckPoint( new CheckPoint( "check_02", new Vector2(
				1900.0f, 5.0f ), skeleton, world, progressManager,
				"levelStage_0_1" ) );
	}

	@Override
	public void render( float deltaTime ) {
		if ( Gdx.gl20 != null ) {
			Gdx.gl20.glClearColor( 0.0f, 0.0f, 0.0f, 1.0f );
			Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT );
		} else {
			Gdx.gl10.glClearColor( 0.0f, 0.0f, 0.0f, 1.0f );
			Gdx.gl10.glClear( GL20.GL_COLOR_BUFFER_BIT );
		}

		cam.update( deltaTime ); 

		// Set hazards active
		if ( Gdx.input.isKeyPressed( Input.Keys.NUM_1 ) ) {
			fire.setActive( true );
			spikes.setActive( true );
			spikes2.setActive( true );
		}
		// Set hazards inactive
		if ( Gdx.input.isKeyPressed( Input.Keys.NUM_2 ) ) {
			fire.setActive( false );
			spikes.setActive( false );
			spikes2.setActive( false );
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.C ) ) {
			if ( skeleton != null )
				skeleton.rotateBy( -0.01f );
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.V ) ) {
			if ( skeleton != null )
				skeleton.rotateBy( 0.01f );
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.BACKSPACE ) ) {
			ScreenManager.getInstance( ).show( ScreenType.TROPHY );
		}

		if ( Gdx.input.isKeyPressed( Keys.NUM_0 ) ) {
			if ( debugTest )
				debug = !debug;
			debugTest = false;
		} else
			debugTest = true;

		player1.update( deltaTime );
		player2.update( deltaTime );
		progressManager.update( deltaTime );
		rootSkeleton.update( deltaTime );
		batch.setProjectionMatrix( cam.combined( ) );
		batch.begin( );

		rootSkeleton.draw( batch, deltaTime, cam );
		progressManager.draw( batch, deltaTime, cam );
		fire.draw( batch, deltaTime, cam );
		// elec.draw( batch, deltaTime );
		// testSteam.draw( batch, deltaTime );
		player1.draw( batch, deltaTime, cam );
		player2.draw( batch, deltaTime, cam );
		testPipe.draw( batch, deltaTime, cam );

		batch.end( );

		if ( debug )
			debugRenderer.render( world, cam.combined( ) );

		if ( Gdx.input.isKeyPressed( Keys.P ) ) {
			System.exit( 0 );
		}

		world.step( 1 / 60f, 6, 6 );

		if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) ) {
			if ( !ScreenManager.escapeHeld ) {
				ScreenManager.getInstance( ).show( ScreenType.PAUSE );
			}
		} else
			ScreenManager.escapeHeld = false;

	}

	@Override
	public void resize( int arg0, int arg1 ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show( ) {
		// TODO Auto-generated method stub

	}

}
