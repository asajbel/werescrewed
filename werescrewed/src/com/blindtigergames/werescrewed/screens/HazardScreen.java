package com.blindtigergames.werescrewed.screens;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.checkpoints.CheckPoint;
import com.blindtigergames.werescrewed.checkpoints.ProgressManager;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.tween.EntityAccessor;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.hazard.Electricity;
import com.blindtigergames.werescrewed.hazard.Fire;
import com.blindtigergames.werescrewed.hazard.Saws;
import com.blindtigergames.werescrewed.hazard.Spikes;
import com.blindtigergames.werescrewed.hazard.builders.SpikesBuilder;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.screws.StructureScrew;
import com.blindtigergames.werescrewed.util.Util;

public class HazardScreen implements com.badlogic.gdx.Screen {

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
	private Fire fire;
	private Electricity elec;
	private Saws saw;
	private SpikesBuilder spikesBuilder;
	private boolean debug = true;
	private boolean debugTest = true;

	public HazardScreen( ) {
		batch = new SpriteBatch( );
		world = new World( new Vector2( 0, -35 ), true );
		initCamera( );
		Tween.registerAccessor( Platform.class, new PlatformAccessor( ) );
		Tween.registerAccessor( Entity.class, new EntityAccessor( ) );

		skeleton = new Skeleton( "skeleton", Vector2.Zero, null, world );
		rootSkeleton = new Skeleton( "root", Vector2.Zero, null, world );
		platBuilder = new PlatformBuilder( world );
		spikesBuilder = new SpikesBuilder( world );
		contactListener = new MyContactListener( );
		world.setContactListener( contactListener );

		player1 = new PlayerBuilder( ).name( "player1" ).world( world )
				.position( -1000.0f, 100.0f ).buildPlayer( );
		player2 = new PlayerBuilder( ).name( "player2" ).world( world )
				.position( -950.0f, 100.0f ).buildPlayer( );

		initTiledPlatforms( );
		initHazards( );
		initCheckPoints( );
		initCrushTest( );

		rootSkeleton.addSkeleton( skeleton );
		debugRenderer = new SBox2DDebugRenderer( Util.BOX_TO_PIXEL );
		debugRenderer.setDrawJoints( false );

		Gdx.app.setLogLevel( Application.LOG_DEBUG );
	}

	private void initCamera( ) {
		float zoom = 1.0f;
		float width = Gdx.graphics.getWidth( ) / zoom;
		float height = Gdx.graphics.getHeight( ) / zoom;
		cam = new Camera( width, height, world );
	}

	private void initTiledPlatforms( ) {
		ground = platBuilder.position( 0.0f, -75 ).name( "ground" )
				.dimensions( 200, 4 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0.0f ).buildTilePlatform( );
		ground.setCategoryMask( Util.KINEMATIC_OBJECTS,
				Util.CATEGORY_EVERYTHING );
		ground.setCrushing( true );
		skeleton.addKinematicPlatform( ground );
	}

	private void initHazards( ) {
		/*
		fire = new Fire( "Fire1", new Vector2( -3000.0f, -10.0f ), 
				world, true );
		elec = new Electricity( "Elec1", new Vector2( 3000.0f, -10.0f ),
				world, true );
		saw = new Saws( "Saw1", new Vector2( -2000.0f, 40.0f ),
				2, world, true );
		 */
		Spikes spikes = new Spikes( "Spikes1", new Vector2( -1250.0f, -10.0f ), 
				1, 12, world, true, false, false );
		Spikes spikes2 = spikesBuilder.position( -1050.0f, -10.0f ).dimensions( 1, 8 )
				.left( ).buildSpikes( );
		//add the spikes to the skeleton
		skeleton.addKinematicPlatform( spikes );
		skeleton.addKinematicPlatform( spikes2 );
	}

	private void initCrushTest( ) {
		crusher = platBuilder.position( 400.0f, 100.0f ).name( "crusher" )
				.dimensions( 6, 1 ).texture( testTexture ).dynamic( )
				.oneSided( false ).restitution( 0.0f ).buildTilePlatform( );
		crusher.setCrushing( true );
		skeleton.addDynamicPlatform( crusher );
		struct1 = new StructureScrew( "struct1", crusher.getPositionPixel( )
				.add( new Vector2( -50f, 0f ) ), 50, crusher, world );
		struct1.addStructureJoint( skeleton );
		struct2 = new StructureScrew( "struct1", crusher.getPositionPixel( )
				.add( new Vector2( 50f, 0f ) ), 50, crusher, world );
		struct2.addStructureJoint( skeleton );
		skeleton.addScrewForDraw( struct1 );
		skeleton.addScrewForDraw( struct2 );
	}

	private void initCheckPoints( ) {
		progressManager = new ProgressManager( player1, player2, world );
		progressManager.addCheckPoint( new CheckPoint( "check_01", new Vector2(
				-512f, 32f ), skeleton, world, progressManager,
				"levelStage_0_0" ) );
		progressManager
				.addCheckPoint( new CheckPoint( "check_01", new Vector2( 0f,
						32f ), skeleton, world, progressManager,
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
		if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) ) {
			ScreenManager.getInstance( ).show( ScreenType.PAUSE );
		}

		cam.update( );

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

		rootSkeleton.draw( batch );
		progressManager.draw( batch );
		player1.draw( batch );
		player2.draw( batch );

		batch.end( );

		if ( debug )
			debugRenderer.render( world, cam.combined( ) );

		world.step( 1 / 60f, 6, 6 );
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
