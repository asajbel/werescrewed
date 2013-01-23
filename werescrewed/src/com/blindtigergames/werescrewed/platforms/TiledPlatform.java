package com.blindtigergames.werescrewed.platforms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.screens.GameScreen;
import java.lang.Math;
import java.util.Iterator;
import java.util.Vector;

/**
 * @param name
 *            blah blah
 * 
 * @author Ranveer
 * 
 */

//Need to fix widht/height storage
public class TiledPlatform extends Platform {
	protected boolean rotate = false;
	protected boolean oneSided;
	protected float tileHeight, tileWidth;
	protected float tileSize;
	protected Vector<Sprite> tiles;
	protected float posx, posy; 
	protected Vector2 bodypos;

	public TiledPlatform( String n, Vector2 pos, Texture tex, float width,
			float height, boolean isOneSided, World world ) {
		super( n, pos, tex, world );
		this.tileHeight = height;
		this.tileWidth = width;
		this.tileSize = tileConstant * 2; 
		this.width = width * tileConstant;
		this.height = height * tileConstant;
		constructTileBody( pos.x, pos.y, width, height );
		tiles = new Vector<Sprite>();
		Sprite temp;
		for (int i = 0; i < width; i++ ){
			temp = new Sprite(sprite);
			tiles.add( temp );
			tiles.get( i ).setOrigin( (i - tileWidth/2) * tileConstant * 2, tileConstant );
		}
		body.setUserData(this);
		setOneSided( isOneSided );
	}

	private void constructTileBody( float x, float y, float width, float height ) {

		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set( new Vector2( x , y ) );
		body = world.createBody( bodyDef );

		PolygonShape polygon = new PolygonShape( );
		polygon.setAsBox( ( width * tileConstant ) * GameScreen.PIXEL_TO_BOX,
				( height * tileConstant ) * GameScreen.PIXEL_TO_BOX );

		sprite.setPosition( body.getPosition( ).x, body.getPosition( ).y - sprite.getHeight( ) / 2);
		sprite.setOrigin( 0 , sprite.getHeight( ) / 2);

		FixtureDef platformFixtureDef = new FixtureDef( );
		platformFixtureDef.shape = polygon;
		body.createFixture( platformFixtureDef );

		polygon.dispose( );
	}
	
	public void setOneSided( boolean value ){
		oneSided = value;
	}
	
	public boolean getOneSided(){
		return oneSided;
	}
	
	public float getActualHeight(){
	    return height * 32;
	}
	
	public float getActualWidth(){
	    return width * 32;
	}

	@Override
	public void update( float deltaTime ) {
		super.update( deltaTime );
		if ( Gdx.input.isKeyPressed( Keys.B ) ) {
			setOneSided(!getOneSided());
			System.out.println(getOneSided());
		}
//		sprite.setOrigin( body.getWorldCenter( ).x, body.getWorldCenter( ).y);
//		Iterator<Sprite> v = tiles.listIterator( ); 
//		for ( int i = -MathUtils.floor( tileWidth / 2 ); i < tileWidth / 2; i++) {
//			posx = body.getPosition( ).x * GameScreen.BOX_TO_PIXEL + i * tileSize * MathUtils.cos( body.getAngle() );
//			posy = body.getPosition( ).y * GameScreen.BOX_TO_PIXEL + tileSize * MathUtils.sin( body.getAngle() );
//			v.next( ).setPosition( posx, posy );
//		}
		
		for ( int i = 0; i < tileWidth; i++ ) {
			bodypos = body.getPosition().mul( GameScreen.BOX_TO_PIXEL );
//			Gdx.app.log( "TiledPlaterform: " + i, String.valueOf( bodypos.x ) );
			tiles.get( i ).setPosition( bodypos.x - tileConstant * (i - tileWidth/2) * 2, bodypos.y - tileConstant );
			tiles.get( i ).setRotation( MathUtils.radiansToDegrees * body.getAngle( ) );
//			Gdx.app.log( "TiledPlaterform: " + i, String.valueOf( tiles.get( i ).getX( ) ) );
		}
	}
	
	@Override 
	public void draw(SpriteBatch batch ) {
		Sprite d;
		Iterator<Sprite> v = tiles.listIterator( ); 
		while (v.hasNext( )) {
			d = v.next( );
			d.draw( batch );
			//Gdx.app.log( "TiledPlatform Draw", String.valueOf( d.getX( ) ));
		}
		
		for (int i = 0; i < tileWidth; i++) {
			tiles.elementAt( i ).draw( batch );
//			Gdx.app.log( "TiledPlatform Draw", String.valueOf( tiles.elementAt( i ).getX( ) ));
//			Gdx.app.log( "TiledPlatform: " + String.valueOf( i ), String.valueOf( tiles.elementAt( i ).getOriginX( ) ) );
		}
		
	}
	

}