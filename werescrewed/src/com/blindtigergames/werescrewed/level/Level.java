package com.blindtigergames.werescrewed.level;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.EntityManager;
import com.blindtigergames.werescrewed.entity.Player;
import com.blindtigergames.werescrewed.entity.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.PlatformBuilder;
import com.blindtigergames.werescrewed.platforms.RoomPlatform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;


/**
 * @param name
 * Basically everything that a level needs to exist
 * should exist here
 * Things can change in the future
 * @author Ranveer
 * 
 */

public class Level {
	public Camera camera;
	public World world;
	Player player;
	EntityManager entities;
	ArrayList<Platform> platforms;
	Skeleton root;
	
	public Level( ){
		
		float zoom = 1.0f;
		float w = Gdx.graphics.getWidth( ) / zoom;
		float h = Gdx.graphics.getHeight( ) / zoom;

		world = new World( new Vector2( 0, -100 ), true );
		camera = new Camera( w, h);
		player = (Player)new PlayerBuilder()
					.name("Player")
					.world( world )
					.position( new Vector2(0.0f,0.0f) )
					.build();

		entities = new EntityManager();
		platforms = new ArrayList<Platform>();
		root = new Skeleton("root", new Vector2(0,0), null, world);
	}
	
	public void update( float deltaTime ){
		camera.update( );
		
		player.update( deltaTime );
		root.update( deltaTime );
		entities.update( deltaTime );
		for (Platform p: platforms)
			p.update( deltaTime );
	}
	
	public void draw ( SpriteBatch sb, SBox2DDebugRenderer dr){
		sb.setProjectionMatrix( camera.combined() );
		sb.begin();
		entities.draw( sb );
		root.draw( sb );
		for (Platform p: platforms)
			p.draw( sb );
		
		player.draw( sb );
		sb.end();
		dr.render( world, camera.combined( ) );
		world.step( 1 / 60f, 6, 2 );

	}
	
	public static Level getDefaultLevel(){
		Level out = new Level();
		TiledPlatform tp, ground;
		RoomPlatform rp;
		//ShapePlatform sp;
		Texture texture = new Texture( Gdx.files.internal( "data/rletter.png" ) );
		
		tp = new PlatformBuilder(out.world)
		.setPosition( 2.0f, 0.2f )
		.setDimensions( 10, 1 )
		.setTexture( texture )
		.buildTilePlatform( );

		rp = new PlatformBuilder(out.world)
		.setPosition( -1.0f, 0.4f )
		.setDimensions( 1, 10 )
		.setTexture( texture )
		.buildRoomPlatform( );
		
		ground = new PlatformBuilder(out.world)
		.setPosition( 0.0f, 0.0f )
		.setDimensions( 100, 1 )
		.setTexture( texture )
		.buildTilePlatform( );
		
		out.platforms.add( ground );
		out.platforms.add( rp );
		out.platforms.add( tp );
		
		
		return out;
	}
}