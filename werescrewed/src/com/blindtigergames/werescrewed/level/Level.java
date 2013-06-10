package com.blindtigergames.werescrewed.level;

import java.util.ArrayList;
import java.util.Iterator;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.checkpoints.ProgressManager;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.RootSkeleton;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.tween.EntityAccessor;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.screens.ScreenManager;
import com.blindtigergames.werescrewed.screens.ScreenType;
import com.blindtigergames.werescrewed.util.Util;

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
	public boolean debugTest, debug;
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
	public float bgCamZoomScale = 0f, bgCamZoomMax = 1f, bgCamZoomMin = 1f;

	private final float MAX_FALL_POS = -4000.f;

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
		// camera.update( );
		world.step( WereScrewedGame.oneOverTargetFrameRate, 2, 1 );

		if ( player1 != null )
			player1.update( deltaTime );
		if ( player2 != null )
			player2.update( deltaTime );

		if ( WereScrewedGame.debug && (Gdx.input.isTouched( ) || Gdx.input.isButtonPressed( Buttons.LEFT ) ) ) {
			Vector3 cursorPosition = new Vector3( Gdx.input.getX( ),
					Gdx.input.getY( ), 0 );
			camera.camera.unproject( cursorPosition );
			// Gdx.app.log( "Mouse Position in Pixels", cursorPosition.toString(
			// ) );
			cursorPosition.mul( Util.PIXEL_TO_BOX );
			if ( player1 != null && player2 != null ) {
				player1.body.setTransform( cursorPosition.x, cursorPosition.y,
						0 );
				player2.body.setTransform( cursorPosition.x, cursorPosition.y,
						0 );
			}
		}

		root.update( deltaTime );

		if ( progressManager != null && ( player1 != null && player2 != null ) )
			progressManager.update( deltaTime );

		if ( progressManager != null )
			progressManager.update( deltaTime );

		if (  WereScrewedGame.debug && Gdx.input.isKeyPressed( Keys.NUM_0 ) ) {
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

		if ( WereScrewedGame.p1Controller != null ) {
			if ( WereScrewedGame.p1ControllerListener.pausePressed( ) ) {
				if ( !ScreenManager.p1PauseHeld ) {
					ScreenManager.getInstance( ).show( ScreenType.PAUSE );
				}
			} else {
				ScreenManager.p1PauseHeld = false;
			}
		}
		if ( WereScrewedGame.p2Controller != null ) {
			if ( WereScrewedGame.p2ControllerListener.pausePressed( ) ) {
				if ( !ScreenManager.p2PauseHeld ) {
					ScreenManager.getInstance( ).show( ScreenType.PAUSE );
				}
			} else {
				ScreenManager.p2PauseHeld = false;
			}
		}

		if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) ) {
			if ( !ScreenManager.escapeHeld ) {
				ScreenManager.getInstance( ).show( ScreenType.PAUSE );
			}
		} else
			ScreenManager.escapeHeld = false;
	}

	public void draw( SpriteBatch batch, SBox2DDebugRenderer debugRenderer,
			float deltaTime ) {
		batch.setShader( WereScrewedGame.defaultShader );
		batch.setBlendFunction( GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA );
		batch.enableBlending( );
		batch.setProjectionMatrix( camera.combined( ) );
		batch.begin( );

		// float deltaTime = Gdx.graphics.getDeltaTime( );
		drawBGStuff( batch, deltaTime );

		// draw all the normal sprites
		root.draw( batch, deltaTime, camera );
		if ( progressManager != null )
			progressManager.draw( batch, deltaTime, camera );
		if ( player1 != null )
			player1.draw( batch, deltaTime, camera );
		if ( player2 != null )
			player2.draw( batch, deltaTime, camera );

		drawFGStuff( batch );

		player1.drawBubble( batch );
		player2.drawBubble( batch );

		// camera.renderBuffers( );
		batch.end( );

		if ( debug )
			debugRenderer.render( world, camera.combined( ) );

	}

	private void drawBGStuff( SpriteBatch batch, float deltaTime ) {
		ArrayList< Skeleton > skelsToRemove = new ArrayList< Skeleton >( );
		ArrayList< Entity > entitiesToRemove = new ArrayList< Entity >( );
		for ( Skeleton skel : skelBGList ) {
			if ( skel.isRemoved( ) || skel.getPositionPixel( ).y < MAX_FALL_POS ) {
				skelsToRemove.add( skel );
			} else {
				if ( skel.isActive( ) ) {
					if ( skel.bgSprite != null
							&& ( !skel.isFadingSkel( ) || skel.isFGFaded( ) ) ) {
						skel.bgSprite.draw( batch );
					}
					if ( skel.isUpdatable( ) ) {
						skel.drawBGDecals( batch, camera );
					}
				}
			}
		}
		for ( Entity e : entityBGList ) {
			if ( e.getPositionPixel( ).y < MAX_FALL_POS ) {
				entitiesToRemove.add( e );
			} else {
				if ( e.isActive( )
						&& ( e.getParentSkeleton( ) == null || ( e
								.getParentSkeleton( ).isUpdatable( ) && !e
								.getParentSkeleton( ).getWasInactive( ) ) ) ) {
					{
						e.drawBGDecals( batch, camera );
					}
				}
			}
		}
		for ( int i = 0; i < skelsToRemove.size( ); i++ ) {
			skelBGList.remove( skelsToRemove.get( i ) );
		}
		for ( int i = 0; i < entitiesToRemove.size( ); i++ ) {
			entityBGList.remove( entitiesToRemove.get( i ) );
		}
	}

	private void drawFGStuff( SpriteBatch batch ) {
		ArrayList< Skeleton > skelsToRemove = new ArrayList< Skeleton >( );
		ArrayList< Entity > entitiesToRemove = new ArrayList< Entity >( );
		for ( Entity e : entityFGList ) {
			if ( e.getPositionPixel( ).y < MAX_FALL_POS ) {
				entitiesToRemove.add( e );
			} else {
				if ( e.getParentSkeleton( ) == null
						|| ( e.getParentSkeleton( ).isUpdatable( ) && !e
								.getParentSkeleton( ).getWasInactive( ) ) ) {
					e.drawFGDecals( batch, camera );
				}
			}
		}
		for ( Skeleton skel : skelFGList ) {
			if ( skel.isRemoved( ) || skel.getPositionPixel( ).y < MAX_FALL_POS ) {
				skelsToRemove.add( skel );
			} else {
				if ( skel.fgSprite != null && skel.fgSprite.getAlpha( ) != 0 ) {
					skel.fgSprite.draw( batch );
				}
				// if ( !skel.isUpdatable( ) )
				{
					skel.drawFGDecals( batch, camera );
				}
			}
		}
		for ( int i = 0; i < skelsToRemove.size( ); i++ ) {
			skelBGList.remove( skelsToRemove.get( i ) );
		}
		for ( int i = 0; i < entitiesToRemove.size( ); i++ ) {
			entityBGList.remove( entitiesToRemove.get( i ) );
		}
	}

	public void resetPhysicsWorld( ) {
		world.clearForces( );

		for ( Iterator< Body > iter = world.getBodies( ); iter.hasNext( ); ) {
			Body body = iter.next( );
			if ( body != null )
				world.destroyBody( body );
		}
		for ( Iterator< Joint > iter = world.getJoints( ); iter.hasNext( ); ) {
			Joint joint = iter.next( );
			if ( joint != null )
				world.destroyJoint( joint );
		}

	}

	public void initBackgroundRoot( ) {
		// background stuff
		backgroundBatch = new SpriteBatch( );
		backgroundRootSkeleton = new RootSkeleton( "backgroundroot",
				Vector2.Zero, null, world );
		float width = Gdx.graphics.getWidth( ) / 1f;
		float height = Gdx.graphics.getHeight( ) / 1f;
		backgroundCam = new OrthographicCamera( 1, width / height );
		backgroundCam.viewportWidth = width;
		backgroundCam.viewportHeight = height;
		backgroundCam.position.set( width * .5f, height * .5f, 0f );
		backgroundCam.update( );
	}

}