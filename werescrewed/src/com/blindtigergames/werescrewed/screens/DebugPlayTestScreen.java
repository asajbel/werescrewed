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
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.builders.ScrewBuilder;
import com.blindtigergames.werescrewed.entity.tween.EntityAccessor;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.screws.StructureScrew;
import com.blindtigergames.werescrewed.skeleton.Skeleton;
import com.blindtigergames.werescrewed.util.Util;

public class DebugPlayTestScreen implements com.badlogic.gdx.Screen {

	private Camera cam;
	private SpriteBatch batch;
	private Texture testTexture;
	private World world;
	private MyContactListener contactListener;
	private SBox2DDebugRenderer debugRenderer;
	private Player player1, player2;
	private PlatformBuilder platBuilder;
	private Skeleton skeleton;
	private Skeleton rootSkeleton;
	private boolean debug = true;
	private boolean debugTest = true;
	private TiledPlatform wall;
	private TiledPlatform obst;
	private TiledPlatform plat;
	private Skeleton skel1;
	private TiledPlatform stair;

	private static final float TILE = 32;
	private TiledPlatform step;
	private Skeleton skel2;
	private TiledPlatform ground;
	private StrippedScrew strScrew;
	private Skeleton skel3;
	private Skeleton skel4;

	public DebugPlayTestScreen( ) {

		batch = new SpriteBatch( );
		world = new World( new Vector2( 0, -35 ), true );

		skeleton = new Skeleton( "skeleton", Vector2.Zero, null, world );
		rootSkeleton = new Skeleton( "root", Vector2.Zero, null, world );

		platBuilder = new PlatformBuilder( world );

		testTexture = WereScrewedGame.manager.get( WereScrewedGame.dirHandle.path( ) + "/common/TilesetTest.png", Texture.class );


		Tween.registerAccessor( Platform.class, new PlatformAccessor( ) );
		Tween.registerAccessor( Entity.class, new EntityAccessor( ) );

		// Initialize camera
		initCamera( );

		// Initialize listeners
		contactListener = new MyContactListener( );
		world.setContactListener( contactListener );

		// Initialize players
		player1 = new PlayerBuilder( ).name( "player1" ).world( world )
				.position( 112f * TILE, 83f * TILE).buildPlayer( );
		player2 = new PlayerBuilder( ).name( "player2" ).world( world )
				.position( 112f * TILE, 83f * TILE ).buildPlayer( );

		// 115f * TILE, 42 * TILE

		floor1( );
		floor2( );
		floor3( );
		floor4( );

		rootSkeleton.addSkeleton( skeleton );
		debugRenderer = new SBox2DDebugRenderer( Util.BOX_TO_PIXEL );
		debugRenderer.setDrawJoints( false );

		Gdx.app.setLogLevel( Application.LOG_DEBUG );

	}

	private void floor1( ) {

		skel1 = new Skeleton( "skel1", new Vector2( 0, 0 ), null, world );

		// PUZZLE 1 //

		ground = platBuilder.position( 81 * TILE, 0 ).name( "ground1" )
				.dimensions( 160, 2 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0.0f ).buildTilePlatform( );
		ground.setCategoryMask( Util.KINEMATIC_OBJECTS, Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( ground );

		wall = platBuilder.position( 0, 99 * TILE ).name( "wall1" )
				.dimensions( 2, 200 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0.0f ).buildTilePlatform( );
		skel1.addKinematicPlatform( wall );

		obst = platBuilder.position( 15 * TILE, 1.5f * TILE ).name( "obst1" )
				.dimensions( 2, 1 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( obst );

		obst = platBuilder.position( 20 * TILE, 2f * TILE ).name( "obst2" )
				.dimensions( 2, 2 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( obst );

		obst = platBuilder.position( 25 * TILE, 2.5f * TILE ).name( "obst3" )
				.dimensions( 2, 3 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( obst );

		obst = platBuilder.position( 30 * TILE, 3f * TILE ).name( "obst4" )
				.dimensions( 2, 4 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( obst );

		obst = platBuilder.position( 35 * TILE, 3.5f * TILE ).name( "obst5" )
				.dimensions( 2, 5 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( obst );

		obst = platBuilder.position( 40 * TILE, 3.5f * TILE ).name( "obst6" )
				.dimensions( 2, 5 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( obst );

		obst = platBuilder.position( 45 * TILE, 3.5f * TILE ).name( "obst7" )
				.dimensions( 2, 5 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( obst );

		// PUZZLE 2 //

		plat = platBuilder.position( 55 * TILE, 4.5f * TILE ).name( "plat1" )
				.dimensions( 4, 1 ).texture( testTexture ).kinematic( )
				.oneSided( true ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( plat );

		plat = platBuilder.position( 67 * TILE, 4.5f * TILE ).name( "plat2" )
				.dimensions( 4, 1 ).texture( testTexture ).kinematic( )
				.oneSided( true ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( plat );

		plat = platBuilder.position( 55 * TILE, 8.5f * TILE ).name( "plat3" )
				.dimensions( 4, 1 ).texture( testTexture ).kinematic( )
				.oneSided( true ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( plat );

		plat = platBuilder.position( 67 * TILE, 8.5f * TILE ).name( "plat4" )
				.dimensions( 4, 1 ).texture( testTexture ).kinematic( )
				.oneSided( true ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( plat );

		plat = platBuilder.position( 61 * TILE, 6.5f * TILE ).name( "plat5" )
				.dimensions( 4, 1 ).texture( testTexture ).kinematic( )
				.oneSided( true ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( plat );

		// PUZZLE 3 //

		stair = platBuilder.position( 77 * TILE, 2 * TILE ).name( "stair1" )
				.dimensions( 8, 2 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( stair );

		stair = platBuilder.position( 78 * TILE, 4 * TILE ).name( "stair2" )
				.dimensions( 6, 2 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( stair );

		stair = platBuilder.position( 79 * TILE, 6 * TILE ).name( "stair3" )
				.dimensions( 4, 2 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( stair );

		stair = platBuilder.position( 80 * TILE, 8 * TILE ).name( "stair4" )
				.dimensions( 2, 2 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( stair );

		stair = platBuilder.position( 82 * TILE, 2.5f * TILE ).name( "stair5" )
				.dimensions( 2, 3 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( stair );

		plat = platBuilder.position( 93 * TILE, 5 * TILE ).name( "plat6" )
				.dimensions( 2, 8 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( plat );

		stair = platBuilder.position( 95 * TILE, 2.5f * TILE ).name( "stair6" )
				.dimensions( 2, 3 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( stair );

		// PUZZLE 4 //

		float dx = 4;
		float x = 106;
		float dy = 2;
		float y = 1.5f;
		float width = 6f;
		int i = 0;
		while ( width > 0 ) {
			step = platBuilder.position( x * TILE, y * TILE )
					.name( "step" + ( i + 1 ) ).dimensions( width, 1 )
					.texture( testTexture ).kinematic( ).oneSided( true )
					.restitution( 0 ).buildTilePlatform( );
			skel1.addKinematicPlatform( step );
			x += dx + width + .5f;
			y += dy;
			i++;
			width--;
		}

		wall = platBuilder.position( 160 * TILE, 6.5f * TILE ).name( "wall2" )
				.dimensions( 2, 11 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( wall );

		ground = platBuilder.position( 181 * TILE, 11 * TILE ).name( "ground2" )
				.dimensions( 40, 2 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		ground.setCategoryMask( Util.KINEMATIC_OBJECTS, Util.CATEGORY_EVERYTHING );
		skel1.addKinematicPlatform( ground );

		wall = platBuilder.position( 202 * TILE, 25 * TILE ).name( "wall3" )
				.dimensions( 2, 30 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel1.addKinematicPlatform( wall );

		rootSkeleton.addSkeleton( skel1 );
	}

	private void floor2( ) {
		skel2 = new Skeleton( "skel2", new Vector2( 0, 0 ), null, world );

		// PUZZLE 1 //

		strScrew = new StrippedScrew( "strScrew1", world, new Vector2(
				170 * TILE, 17 * TILE ), skel2 );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew2", world, new Vector2(
				185 * TILE, 17 * TILE ), skel2 );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew3", world, new Vector2(
				190 * TILE, 17 * TILE ), skel2 );
		skel2.addStrippedScrew( strScrew );

		plat = platBuilder.position( 180 * TILE, 19 * TILE ).name( "plat6" )
				.dimensions( 3, 1 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel2.addKinematicPlatform( plat );

		plat = platBuilder.position( 195 * TILE, 19 * TILE ).name( "plat7" )
				.dimensions( 3, 1 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel2.addKinematicPlatform( plat );
		
		strScrew = new StrippedScrew( "strScrew3", world, new Vector2(
				190 * TILE, 24 * TILE ), skel2 );
		skel2.addStrippedScrew( strScrew );
		
		strScrew = new StrippedScrew( "strScrew3", world, new Vector2(
				185 * TILE, 24 * TILE ), skel2 );
		skel2.addStrippedScrew( strScrew );
		
		plat = platBuilder.position( 195 * TILE, 25 * TILE ).name( "plat7" )
				.dimensions( 3, 1 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel2.addKinematicPlatform( plat );
		
		plat = platBuilder.position( 180 * TILE, 25 * TILE ).name( "plat7" )
				.dimensions( 3, 1 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel2.addKinematicPlatform( plat );
		
		plat = platBuilder.position( 187.5f * TILE, 30 * TILE ).name( "plat8" )
				.dimensions( 5, 1 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel2.addKinematicPlatform( plat );
		
		// PUZZLE 2 //

		strScrew = new StrippedScrew( "strScrew4", world, new Vector2(
				178 * TILE, 34 * TILE ), skel2 );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew5", world, new Vector2(
				171 * TILE, 34 * TILE ), skel2 );
		skel2.addStrippedScrew( strScrew );

		ground = platBuilder.position( 131 * TILE, 22 * TILE ).name( "ground3" )
				.dimensions( 68, 2 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		ground.setCategoryMask( Util.KINEMATIC_OBJECTS, Util.CATEGORY_EVERYTHING );
		skel2.addKinematicPlatform( ground );

		// PUZZLE 3 //

		plat = platBuilder.position( 127f * TILE, 28 * TILE ).name( "plat10" )
				.dimensions( 3, 11 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel2.addKinematicPlatform( plat );

		strScrew = new StrippedScrew( "strScrew6", world, new Vector2(
				139 * TILE, 29 * TILE ), skel2 );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew7", world, new Vector2(
				134 * TILE, 34 * TILE ), skel2 );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew8", world, new Vector2(
				119 * TILE, 34 * TILE ), skel2 );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew9", world, new Vector2(
				115 * TILE, 29 * TILE ), skel2 );
		skel2.addStrippedScrew( strScrew );

		// PUZZLE 4 //

		strScrew = new StrippedScrew( "strScrew10", world, new Vector2(
				105 * TILE, 29 * TILE ), skel2 );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew11", world, new Vector2(
				101 * TILE, 34 * TILE ), skel2 );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew12", world, new Vector2(
				109 * TILE, 34 * TILE ), skel2 );
		skel2.addStrippedScrew( strScrew );

		strScrew = new StrippedScrew( "strScrew13", world, new Vector2(
				105 * TILE, 39 * TILE ), skel2 );
		skel2.addStrippedScrew( strScrew );

		wall = platBuilder.position( 96.5f * TILE, 71.5f * TILE )
				.name( "wall4" ).dimensions( 2, 100 ).texture( testTexture )
				.kinematic( ).oneSided( false ).restitution( 0 )
				.buildTilePlatform( );
		skel2.addKinematicPlatform( wall );

		rootSkeleton.addSkeleton( skel2 );
	}

	private void floor3( ) {
		skel3 = new Skeleton( "skel3", new Vector2( 0, 0 ), null, world );
		
		ground = platBuilder.position( 183 * TILE, 40.5f * TILE ).name( "ground4" )
				.dimensions( 150, 1 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		ground.setCategoryMask( Util.KINEMATIC_OBJECTS, Util.CATEGORY_EVERYTHING );
		skel3.addKinematicPlatform( ground );
		
		plat = platBuilder.position( 115f * TILE, 48 * TILE ).name( "plat9" )
				.dimensions( 9, 1 ).texture( testTexture ).dynamic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel3.addDynamicPlatform( plat );
		
		StructureScrew s1 = new ScrewBuilder( )
		.position(
				plat.body.getPosition( ).x * Util.BOX_TO_PIXEL
						- ( plat.sprite.getWidth( ) ),
				plat.body.getPosition( ).y * Util.BOX_TO_PIXEL )
		.entity( plat ).skeleton( skel3 ).world( world )
		.buildStructureScrew( );
		plat.addScrew( s1 );
		
		StructureScrew s2 = new ScrewBuilder( )
		.position(
				plat.body.getPosition( ).x * Util.BOX_TO_PIXEL
						+ ( plat.sprite.getWidth( ) ),
				plat.body.getPosition( ).y * Util.BOX_TO_PIXEL )
		.entity( plat ).skeleton( skel3 ).world( world )
		.buildStructureScrew( );
		plat.addScrew( s2 );
		
		plat = platBuilder.position( 125f * TILE, 49 * TILE ).name( "plat9" )
				.dimensions( 1, 9).texture( testTexture ).dynamic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel3.addDynamicPlatform( plat );
		
		StructureScrew s3 = new ScrewBuilder( )
		.position(plat.body.getPosition( ).x * Util.BOX_TO_PIXEL ,
				plat.body.getPosition( ).y * Util.BOX_TO_PIXEL - ( plat.sprite.getHeight( ) ))
		.entity( plat ).skeleton( skel3 ).world( world )
		.buildStructureScrew( );
		plat.addScrew( s3 );
		
		StructureScrew s4 = new ScrewBuilder( )
		.position(
				plat.body.getPosition( ).x * Util.BOX_TO_PIXEL
						,
				plat.body.getPosition( ).y * Util.BOX_TO_PIXEL + ( plat.sprite.getHeight( ) ))
		.entity( plat ).skeleton( skel3 ).world( world )
		.buildStructureScrew( );
		plat.addScrew( s4 );
		
		ground = platBuilder.position( 160 * TILE, 45f * TILE ).name( "ground4" )
				.dimensions( 50, 1 ).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		ground.setCategoryMask( Util.KINEMATIC_OBJECTS, Util.CATEGORY_EVERYTHING );
		skel3.addKinematicPlatform( ground );
		
		plat = platBuilder.position( 135f * TILE, 58 * TILE ).name( "plat9" )
				.dimensions( 1, 25).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel3.addKinematicPlatform( plat );
		
		plat = platBuilder.position( 185f * TILE, 58 * TILE ).name( "plat9" )
				.dimensions( 1, 25).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel3.addKinematicPlatform( plat );
		
		plat = platBuilder.position( 195f * TILE, 57 * TILE ).name( "plat9" )
				.dimensions( 1, 30).texture( testTexture ).kinematic( )
				.oneSided( false ).restitution( 0 ).buildTilePlatform( );
		skel3.addKinematicPlatform( plat );
		
		rootSkeleton.addSkeleton( skel3 );
	}
	private void floor4( ) {
		skel4 = new Skeleton( "skel4", new Vector2( 0, 0 ), null, world );
		
		strScrew = new StrippedScrew( "strScrew4", world, new Vector2(
				192f * TILE, 49 * TILE ), skel4 );
		skel4.addStrippedScrew( strScrew );
		
		strScrew = new StrippedScrew( "strScrew4", world, new Vector2(
				188f * TILE, 49 * TILE ), skel4 );
		skel4.addStrippedScrew( strScrew );
		
		rootSkeleton.addSkeleton( skel4 );
	}

	private void initCamera( ) {
		float zoom = 1.0f;
		float width = Gdx.graphics.getWidth( ) / zoom;
		float height = Gdx.graphics.getHeight( ) / zoom;
		cam = new Camera( width, height, world );
	}

	@Override
	public void render( float deltaTime ) {
		if ( Gdx.gl20 != null ) {
			Gdx.gl20.glClearColor( 0.0f, 0f, 0.0f, 1.0f );
			Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT );
		} else {
			Gdx.gl10.glClearColor( 0.0f, 0f, 0.0f, 1.0f );
			Gdx.gl10.glClear( GL20.GL_COLOR_BUFFER_BIT );
		}

		cam.update( );

		if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) ) {
			ScreenManager.getInstance( ).show( ScreenType.PAUSE );
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
		
		player1.update( deltaTime );
		player2.update( deltaTime );

		rootSkeleton.update( deltaTime );

		batch.setProjectionMatrix( cam.combined( ) );
		batch.begin( );

		rootSkeleton.draw( batch );
		player1.draw( batch );
		player2.draw( batch );

		batch.end( );

		if ( debug )
			debugRenderer.render( world, cam.combined( ) );

		world.step( 1 / 60f, 6, 6 );
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