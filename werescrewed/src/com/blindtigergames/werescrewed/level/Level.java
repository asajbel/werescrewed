package com.blindtigergames.werescrewed.level;

import java.util.ArrayList;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.checkpoints.ProgressManager;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.PolySprite;
import com.blindtigergames.werescrewed.entity.RootSkeleton;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.tween.EntityAccessor;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.player.Player;

/**
 * @param name
 *            Basically everything that a level needs to exist should exist here
 *            Things can change in the future
 * @author Ranveer
 * 
 */

public class Level {

	public static int GRAVITY = -35;

	public Camera camera;
	public World world;
	public MyContactListener myContactListener;
	public Player player1, player2;
	public RootSkeleton root;
	public PolySprite polySprite;
	private boolean debugTest, debug;
	public ProgressManager progressManager;
	public static ArrayList< Joint > jointsToRemove = new ArrayList< Joint >( );
	public ArrayList< Skeleton > skelBGList;
	public ArrayList< Skeleton > skelFGList;
	public ArrayList< Entity > entityBGList;
	public ArrayList< Entity > entityFGList;
	// background stuff
	public OrthographicCamera backgroundCam;
	public RootSkeleton backgroundRootSkeleton;
	public SpriteBatch backgroundBatch;

	public Level( ) {

		world = new World( new Vector2( 0, GRAVITY ), true );
		myContactListener = new MyContactListener( );
		world.setContactListener( myContactListener );

		skelBGList = new ArrayList< Skeleton >( );
		skelFGList = new ArrayList< Skeleton >( );
		entityBGList = new ArrayList< Entity >( );
		entityFGList = new ArrayList< Entity >( );

		// progressManager = new ProgressManager(player1, player2, world);
		// camera = new Camera( width, height, world);
		// player1 = new PlayerBuilder( ).name( "player1" ).world( world )
		// .position( 0, 0 ).buildPlayer( );
		// player2 = new PlayerBuilder( ).name( "player2" ).world( world )
		// .position( 0, 0 ).buildPlayer( );

		// rootSkeleton = new RootSkeleton("root", new Vector2(0,0), null,
		// world);
		// root = new Skeleton("root1", new Vector2(0,0), null, world);
		// rootSkeleton.addSkeleton( root );

		Tween.registerAccessor( Platform.class, new PlatformAccessor( ) );
		Tween.registerAccessor( Entity.class, new EntityAccessor( ) );

	}

	public void update( float deltaTime ) {
		camera.update( );

		if ( player1 != null )
			player1.update( deltaTime );
		if ( player2 != null )
			player2.update( deltaTime );

		root.update( deltaTime );

		if ( progressManager != null && ( player1 != null && player2 != null ) )
			progressManager.update( deltaTime );

		if ( progressManager != null )
			progressManager.update( deltaTime );

		if ( Gdx.input.isKeyPressed( Keys.NUM_0 ) ) {
			if ( debugTest )
				debug = !debug;
			debugTest = false;
		} else
			debugTest = true;

		if ( jointsToRemove.size( ) > 0 ) {
			for ( Joint j : jointsToRemove ) {
				world.destroyJoint( j );
			}
			jointsToRemove.clear( );
		}
	}

	public void draw( SpriteBatch batch, SBox2DDebugRenderer debugRenderer,
			float deltaTime ) {
		batch.setShader( WereScrewedGame.defaultShader );
		batch.setBlendFunction( GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA );
		batch.enableBlending( );
		batch.setProjectionMatrix( camera.combined( ) );
		batch.begin( );

		// float deltaTime = Gdx.graphics.getDeltaTime( );
		// draw all background of skeletons before everything
		for ( Skeleton skel : skelBGList ) {
			if ( skel.isActive( ) ) {
				if ( skel.bgSprite != null ) {
					skel.bgSprite.draw( batch );
				}
				skel.drawBGDecals( batch );
			}
		}
		// draw all the normal sprites
		root.draw( batch, deltaTime );
		if ( progressManager != null )
			progressManager.draw( batch, deltaTime );
		if ( player1 != null )
			player1.draw( batch, deltaTime );
		if ( player2 != null )
			player2.draw( batch, deltaTime );
		// draw all foreground entity sprites after everything
		for ( Entity e : entityFGList ) {
			//if ( e.isActive( ) ) 
			{
				e.drawFGDecals( batch );
			}
		}
		// draw all foreground skeleton sprites after everything
		for ( Skeleton skel : skelFGList ) {
			if ( skel.fgSprite != null && skel.fgSprite.getAlpha( ) != 0 ) {
				skel.fgSprite.draw( batch );
			}
			//if ( !skel.isActive( ) ) {
				skel.drawFGDecals( batch );
			//}
		}
		batch.end( );

		if ( debug )
			debugRenderer.render( world, camera.combined( ) );
		world.step( WereScrewedGame.oneOverTargetFrameRate, 6, 6 );

	}

}