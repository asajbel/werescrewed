package com.blindtigergames.werescrewed.level;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.tween.EntityAccessor;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.entity.PolySprite;
import com.blindtigergames.werescrewed.entity.RootSkeleton;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.player.Player;


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
	public Player player1, player2;
	public RootSkeleton rootSkeleton;
	public Skeleton root;
	public PolySprite polySprite;
	private boolean debugTest, debug;
	
	public Level( ){
		
		world = new World( new Vector2( 0, GRAVITY ), true );
		myContactListener = new MyContactListener();
		world.setContactListener( myContactListener );
		
//		camera = new Camera( width, height, world);
//		player1 = new PlayerBuilder( ).name( "player1" ).world( world )
//				.position( 0, 0 ).buildPlayer( );
//		player2 = new PlayerBuilder( ).name( "player2" ).world( world )
//				.position( 0, 0  ).buildPlayer( );


//		rootSkeleton = new RootSkeleton("root", new Vector2(0,0), null, world);
//		root = new Skeleton("root1", new Vector2(0,0), null, world);
//		rootSkeleton.addSkeleton( root );
		
		Tween.registerAccessor( Platform.class, new PlatformAccessor( ) );
		Tween.registerAccessor( Entity.class, new EntityAccessor( ) );

		
		/*Array<Vector2> verts = new Array<Vector2>();
		verts.add( new Vector2(-500,-500) );
		verts.add( new Vector2(500.0f,-500.0f) );
		verts.add( new Vector2(500.0f,500.0f) );
		verts.add( new Vector2(-500.0f,500.0f) );
		
		Texture polyTex = WereScrewedGame.manager.get(
				WereScrewedGame.dirHandle.path( ) + "/common/robot/alphabot_tile_interior.png",
				Texture.class );
		
		polySprite = new PolySprite( polyTex, verts );*/
		
	}
	
	public void update( float deltaTime ){
		camera.update( );
		
		player1.update( deltaTime );
		player2.update( deltaTime );
		rootSkeleton.update( deltaTime );
		
		if ( Gdx.input.isKeyPressed( Keys.NUM_0 ) ) {
			if ( debugTest )
				debug = !debug;
			debugTest = false;
		} else
			debugTest = true;
		

	}
	
	public void draw ( SpriteBatch batch, SBox2DDebugRenderer debugRenderer){
		batch.setProjectionMatrix( camera.combined() );
		
		batch.begin();
		//polySprite.draw( batch );
		rootSkeleton.draw( batch );
		
		player1.draw( batch );
		player2.draw( batch );
		
		batch.end();
		
		if(debug)
			debugRenderer.render( world, camera.combined( ) );
		world.step( 1 / 60f, 6,6 );

	}
	
	

}