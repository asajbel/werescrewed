package com.blindtigergames.werescrewed.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.util.Util;

/**
 * Panel for Alphabot level for displaying which body parts are on/off.
 * 
 * @author stew
 * 
 */
public class Panel extends Platform {
	TextureAtlas panelAtlas;

	public Panel( String name, Vector2 posPix, World world,
			String panelAtlasName, String initialPanel ) {
		super( name, posPix, null, world );
		this.panelAtlas = WereScrewedGame.manager.getAtlas( panelAtlasName );

		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set( posPix.cpy( ).mul( Util.PIXEL_TO_BOX ) );
		this.body = world.createBody( bodyDef );

		if ( !initialPanel.equals( "" ) ) {
			changeSprite( panelAtlas.createSprite( initialPanel ) );
			this.sprite.setPosition( posPix.cpy( ) );
		}
	}

	public void setPanelSprite( String spriteName ) {
		Vector2 oldPos = getPositionPixel( );
		float rotation = ( sprite == null ) ? body.getAngle( ) : sprite.getRotation( );
		changeSprite( panelAtlas.createSprite( spriteName ) );
		sprite.setPosition( oldPos );
		sprite.rotate( rotation );
		
		//clearAllDecals();
				//Sprite s = panelAtlas.createSprite( spriteName );
				//addBGDecal(s);//, new Vector2(-s.getWidth()/2,-s.getHeight()/2) );
	}
}
