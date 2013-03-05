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
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityManager;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.mover.RockingMover;
import com.blindtigergames.werescrewed.entity.tween.EntityAccessor;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.player.Player;
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
	public Camera camera;
	public World world;
	public static final int NUM_PLAYERS = 2;
	Array<Player> players;
	EntityManager entities;
	ArrayList<Platform> platforms;
	Skeleton root;
	
	public Level( ){
		
		float zoom = 1.0f;
		float width = Gdx.graphics.getWidth( ) / zoom;
		float height = Gdx.graphics.getHeight( ) / zoom;
		world = new World( new Vector2( 0, GRAVITY ), true );
		camera = new Camera( width, height, world);
		players = new Array<Player>();
		for (int p = 0; p < NUM_PLAYERS; p++){
			players.add( new PlayerBuilder()
			.name("player"+Integer.toString( p+1 ))
			.world( world )
			.position( new Vector2(0.0f,0.0f) )
			.buildPlayer() );
		}

		entities = new EntityManager();
		platforms = new ArrayList<Platform>();
		root = new Skeleton("root", new Vector2(0,0), null, world);
		
		Tween.registerAccessor( Platform.class, new PlatformAccessor( ) );
		Tween.registerAccessor( Entity.class, new EntityAccessor( ) );
	}
	
	public void update( float deltaTime ){
		camera.update( );
		
		for (Player player: players)
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
		for (Player player: players)
			player.draw( sb );
		sb.end();
		dr.render( world, camera.combined( ) );
		world.step( 1 / 60f, 6, 2 );

	}
	
	public static Level getDefaultLevel(){
		Level out = new Level();
		TiledPlatform tp, ground;
		//ShapePlatform sp;
		Texture texture =
				WereScrewedGame.manager.get(WereScrewedGame.dirHandle.path( ) + "/common/rletter.png", Texture.class);
		
		tp = new PlatformBuilder(out.world)
		.position( 2.0f, 0.2f )
		.dimensions( 10, 1 )
		.texture( texture )
		.buildTilePlatform( );

		
		ground = new PlatformBuilder(out.world)
		.position( 0.0f, 0.0f )
		.dimensions( 100, 1 )
		.texture( texture )
		.buildTilePlatform( );
		
		out.platforms.add( ground );
		out.platforms.add( tp );
		
		
		return out;
	}
	
	public static int GRAVITY = -45;
}