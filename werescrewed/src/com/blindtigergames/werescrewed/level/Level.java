package com.blindtigergames.werescrewed.level;

import java.util.ArrayList;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityManager;
import com.blindtigergames.werescrewed.entity.PolySprite;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.mover.RockingMover;
import com.blindtigergames.werescrewed.entity.tween.EntityAccessor;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.skeleton.RootSkeleton;
import com.blindtigergames.werescrewed.skeleton.Skeleton;


/**
 * @param name
 * Basically everything that a level needs to exist
 * should exist here
 * Things can change in the future
 * @author Ranveer
 * 
 */



public class Level {
	
	public static int GRAVITY = -35;
	
	public Camera camera;
	public World world;
	public MyContactListener myContactListener;
	Player player1, player2;
	RootSkeleton rootSkeleton;
	Skeleton root;
	PolySprite polySprite;
	
	public Level( ){
		
		float zoom = 1.0f;
		float width = Gdx.graphics.getWidth( ) / zoom;
		float height = Gdx.graphics.getHeight( ) / zoom;
		
		world = new World( new Vector2( 0, GRAVITY ), true );
		myContactListener = new MyContactListener();
		world.setContactListener( myContactListener );
		
		camera = new Camera( width, height, world);
		player1 = new PlayerBuilder( ).name( "player1" ).world( world )
				.position( 0, 0 ).buildPlayer( );
		player2 = new PlayerBuilder( ).name( "player2" ).world( world )
				.position( 0, 0  ).buildPlayer( );


		rootSkeleton = new RootSkeleton("root", new Vector2(0,0), null, world);
		root = new RootSkeleton("root1", new Vector2(0,0), null, world);
		rootSkeleton.addSkeleton( root );
		Tween.registerAccessor( Platform.class, new PlatformAccessor( ) );
		Tween.registerAccessor( Entity.class, new EntityAccessor( ) );
		
		
		Array<Vector2> verts = new Array<Vector2>();
		verts.add( new Vector2(-500,-500) );
		verts.add( new Vector2(500.0f,-500.0f) );
		verts.add( new Vector2(500.0f,500.0f) );
		verts.add( new Vector2(-500.0f,500.0f) );
		
		Texture polyTex = WereScrewedGame.manager.get(
				WereScrewedGame.dirHandle.path( ) + "/common/tileset/TilesetTest.png",
				Texture.class );
		
		polySprite = new PolySprite( polyTex, verts );
	}
	
	public void update( float deltaTime ){
		camera.update( );
		
		player1.update( deltaTime );
		player2.update( deltaTime );
		rootSkeleton.update( deltaTime );

	}
	
	public void draw ( SpriteBatch batch, SBox2DDebugRenderer debugRenderer){
		batch.setProjectionMatrix( camera.combined() );
		
		batch.begin();
		
		
		rootSkeleton.draw( batch );
		
		player1.draw( batch );
		player2.draw( batch );
		polySprite.draw( batch );
		batch.end();
		
		debugRenderer.render( world, camera.combined( ) );
		world.step( 1 / 60f, 6, 6 );

	}
	
	

}