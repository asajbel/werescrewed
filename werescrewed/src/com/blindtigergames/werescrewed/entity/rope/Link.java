package com.blindtigergames.werescrewed.entity.rope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.sound.SoundManager;
import com.blindtigergames.werescrewed.util.Util;

public class Link extends Entity {
	protected static final float MIN_AV = 0.05f;
	protected static final float SOUND_DELAY = 0.4f;
	protected static final float SOUND_DELAY_VARIANCE = 0.1f;
	protected static final float SOUND_PITCH = 1.0f;
	protected static final float SOUND_PITCH_VARIANCE = 0.0f;
	protected static final float SOUND_VOLUME = 1.0f;
	
	private float width, height;
	private float xOffset, yOffset;
	
	protected Link child;
	
	@SuppressWarnings( "unused" )
	private boolean drawTwoLinks = false;
	private static TextureRegion chainLinkTexRegion = WereScrewedGame.manager
			.getAtlas( "common-textures" ).findRegion( "chainlink" );

	private static final float spriteScale = ( 22f / 64f );

	public Link( String name, World world, Vector2 pos, Texture texture,
			Vector2 widthHeight ) {
		super( name, pos, texture, null, true );
		this.world = world;
		this.width = widthHeight.x;
		this.height = widthHeight.y;
		this.child = null;
		constructBody( pos );
		Sprite temp = constructSprite( chainLinkTexRegion );
		temp.scale( spriteScale );
		this.xOffset = ( temp.getWidth( ) / 2 );// +this.width/2;
		this.yOffset = ( temp.getHeight( ) / 2 );// +this.height/2;

		this.changeSprite( temp );
		loadSounds();
	}

	private void loadSounds( ) {
		if (sounds == null)
			sounds = new SoundManager();
		sounds.getSound( "clink", WereScrewedGame.dirHandle + "/common/sounds/chains.ogg" );
	}

	private void constructBody( Vector2 pos ) {
		BodyDef bodyDef = new BodyDef( );
		bodyDef.position.set( pos );
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.gravityScale = 0.1f;
		PolygonShape polygonShape = new PolygonShape( );
		polygonShape.setAsBox( width / 2 * Util.PIXEL_TO_BOX, height / 2
				* Util.PIXEL_TO_BOX );
		FixtureDef fixtureDef = new FixtureDef( );
		fixtureDef.filter.categoryBits = Util.CATEGORY_ROPE;
		fixtureDef.filter.maskBits = Util.CATEGORY_NOTHING;
		fixtureDef.shape = polygonShape;
		fixtureDef.density = 1f;
		fixtureDef.restitution = 0.0f;
		fixtureDef.friction = 0.1f;
		body = world.createBody( bodyDef );
		body.createFixture( fixtureDef );

		body.setUserData( this );
	}

	public void createLinkJoint( Link link ) {

		RevoluteJointDef revoluteJointDef = new RevoluteJointDef( );
		revoluteJointDef.initialize( link.body, body,
				new Vector2( body.getWorldCenter( ).x, body.getWorldCenter( ).y
						- height / 2 * Util.PIXEL_TO_BOX ) );
		revoluteJointDef.enableMotor = false;
		revoluteJointDef.collideConnected = false;
		world.createJoint( revoluteJointDef );
	}

	public float getWidth( ) {
		return this.width;
	}

	public float getHeight( ) {
		return this.height;
	}
	
	@Override
	public void draw( SpriteBatch batch, float deltaTime ) {
		// if(drawTwoLinks){
		// float xpos = body.getPosition( ).x - (xOffset * Util.PIXEL_TO_BOX);
		// float ypos = body.getPosition( ).y - (this.yOffset2 *
		// Util.PIXEL_TO_BOX);
		//
		// //this.sprite.setOrigin( this.sprite.getWidth( ) / 2,
		// this.sprite.getHeight( ) / 2);
		// this.sprite.setPosition( xpos * Util.BOX_TO_PIXEL, ypos *
		// Util.BOX_TO_PIXEL);
		// this.sprite.setRotation( MathUtils.radiansToDegrees
		// * body.getAngle( ) );
		//
		// this.sprite.draw( batch );
		// }

		Vector2 screenPos = new Vector2();
		screenPos.x = body.getPosition( ).x - ( xOffset * Util.PIXEL_TO_BOX );
		screenPos.y = body.getPosition( ).y - ( yOffset * Util.PIXEL_TO_BOX );
		screenPos.mul( Util.BOX_TO_PIXEL );
		
		// this.sprite.setOrigin( this.sprite.getWidth( ) / 2,
		// this.sprite.getHeight( ) / 2);
		
		this.sprite.setPosition( screenPos );
		this.sprite.setRotation( Util.RAD_TO_DEG * body.getAngle( ) );
		
		this.sprite.draw( batch );
		
		sounds.update( deltaTime );
		float av;
		if (child != null){
			av = (float)Math.pow( Math.abs( body.getAngle() - child.body.getAngle( ) ), 2.0f );
		} else {
			av = Math.abs( body.getAngularVelocity( ) );
		}
		if (av > MIN_AV){
			float vol = av * SOUND_VOLUME * sounds.calculatePositionalVolume( "clink", screenPos, Camera.CAMERA_RECT );
			float del = SOUND_DELAY + SOUND_DELAY_VARIANCE * WereScrewedGame.random.nextFloat( );
			float pitch = (SOUND_PITCH - SOUND_PITCH_VARIANCE) + (SOUND_PITCH_VARIANCE * Math.min( av, 1.0f ) ); 
			if (vol > 0.0f){
				sounds.playSound( "clink", 0, del, vol, pitch);
			}
		}
	}

	public void setChild(Link c){
		child = c;
	}
}