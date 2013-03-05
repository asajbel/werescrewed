package com.blindtigergames.werescrewed.screens;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
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
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.tween.EntityAccessor;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.hazard.Spikes;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.skeleton.Skeleton;
import com.blindtigergames.werescrewed.util.Util;
import com.blindtigergames.werescrewed.screens.Screen;

public class HazardScreen extends Screen{

	private Texture testTexture;
	private ProgressManager progressManager;
	private SBox2DDebugRenderer debugRenderer;
	private TiledPlatform ground;
	private Spikes spikes;
	private boolean debug = true;
	private boolean debugTest = true;
	
	public HazardScreen ( ) {
		super();
		
		initLevel();
		
		level.getPlayer(0).setPosition(-1000.0f, 100.0f);
		level.getPlayer(1).setPosition(-950.0f, 100.0f);
		
		initTiledPlatforms( );
		initHazards( );
		initCheckPoints( );
		
		debugRenderer = new SBox2DDebugRenderer( Util.BOX_TO_PIXEL );
		debugRenderer.setDrawJoints( false );

		Gdx.app.setLogLevel( Application.LOG_DEBUG );
	}

	private void initTiledPlatforms( ) {
		PlatformBuilder platBuilder = new PlatformBuilder(level.world);
		ground = platBuilder
				.position( 0.0f, -75.0f )
				.name( "ground" )
				.dimensions( 200, 4 )
				.texture( testTexture )
				.kinematic( )
				.oneSided( false )
				.restitution( 0.0f )
				.buildTilePlatform( );
		ground.setCategoryMask( Util.KINEMATIC_OBJECTS,
				Util.CATEGORY_EVERYTHING );
		level.getRoot().addKinematicPlatform( ground );
		level.entities.addEntity( "ground", ground );
	}
	
	private void initHazards( ) {
		spikes = new Spikes( "Spikes1", new Vector2( -1050.0f, 90.0f), 
				50.0f, 50.0f, level.world, true );
		//level.getRoot().addEntity(spikes); <- Method needs to be implemented.
	}
	
	private void initCheckPoints( ) {
		progressManager = new ProgressManager( level.getPlayer( 0 ), level.getPlayer( 1 ), level.world );
		progressManager.addCheckPoint( new CheckPoint( "check_01", new Vector2(
				-512f, 32f ), level.getRoot( ), level.world, progressManager, "levelStage_0_0" ) );
		progressManager.addCheckPoint( new CheckPoint( "check_01", new Vector2(
				0f, 32f ), level.getRoot( ), level.world, progressManager, "levelStage_0_1" ) );
	}
	
	@Override
	public void render( float deltaTime ) {
		progressManager.update( deltaTime );
		spikes.update( deltaTime );
		super.render( deltaTime );
		if ( Gdx.input.isKeyPressed( Keys.NUM_0 ) ) {
			if ( debugTest )
				debug = !debug;
			debugTest = false;
		} else
			debugTest = true;
		
	}

}
