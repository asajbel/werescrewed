package com.blindtigergames.werescrewed.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Player;
import com.blindtigergames.werescrewed.platforms.PlatformBuilder;
import com.blindtigergames.werescrewed.platforms.RoomPlatform;
import com.blindtigergames.werescrewed.platforms.ShapePlatform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;


/**
 * @param name
 * Basically everything that a level needs to exist
 * should exist here
 * Things can change in the future
 * @author Ranveer
 * 
 */

public class Level{
	Camera camera;
	World world;
	Texture texture;
	Player player;
	TiledPlatform tp, ground;
	RoomPlatform rp;
	ShapePlatform sp;
	// When the entityManager is done these objects
	// will go inside the manager instead of 
	// just hanging outside here
	
	public Level( ){
		
		float zoom = 1.0f;
		float w = Gdx.graphics.getWidth( ) / zoom;
		float h = Gdx.graphics.getHeight( ) / zoom;

		world = new World( new Vector2 ( 0, -100 ), true);
		player = new Player( "player", world, new Vector2( 1.0f, 1.0f ));
		camera = new Camera( w, h, player);
		
		texture = new Texture( Gdx.files.internal( "data/rletter.png" ) );
		
		tp = new PlatformBuilder( world )
		.setPosition( 2.0f, 0.2f )
		.setDimensions( 10, 1 )
		.setTexture( texture )
		.buildTilePlatform( );

		rp = new PlatformBuilder( world )
		.setPosition( -1.0f, 0.4f )
		.setDimensions( 1, 10 )
		.setTexture( texture )
		.buildRoomPlatform( );
		
		ground = new PlatformBuilder( world )
		.setPosition( 0.0f, 0.0f )
		.setDimensions( 100, 1 )
		.setTexture( texture )
		.buildTilePlatform( );

		
	}
	
	public void update( float deltaTime ){
		camera.update( );
		
		player.update( deltaTime );
		tp.update( deltaTime );
		rp.update( deltaTime );
	}
	
	public void draw ( SpriteBatch sb, SBox2DDebugRenderer dr){
		sb.setProjectionMatrix( camera.combined() );
		sb.begin( );
		
		tp.draw( sb );
		player.draw( sb );
		rp.draw( sb );
		
		sb.end( );
		
		dr.render( world, camera.combined( ) );
		world.step( 1 / 60f, 6, 2 );

	}
}