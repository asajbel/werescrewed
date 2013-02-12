package com.blindtigergames.werescrewed.camera;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.util.Util;

/*******************************************************************************
 * Anchors give the camera (via the AnchorList) a position and buffer to always
 * keep within the screen and to translate towards.
 * 
 * @author Edward Ramirez
 ******************************************************************************/
public class Anchor {
	public boolean special;
	protected Vector2 position;
	protected Vector2 positionBox;
	protected Vector2 buffer;
	protected boolean activated;
	private Body body;

	static protected final Vector2 DEFAULT_BUFFER = new Vector2( 128f, 128f );

	/**
	 * Create a new Anchor
	 * 
	 * @param position
	 *            starting position of anchor (in pixels)
	 */
	public Anchor( Vector2 position, World world, float radius ) {
		this( false, position, DEFAULT_BUFFER, world, radius );
	}

	/**
	 * Create a new Anchor
	 * 
	 * @param position
	 *            starting position of anchor (in pixels)
	 * @param buffer
	 *            buffer around anchor which must always stay within view
	 */
	public Anchor( Vector2 position, Vector2 buffer, World world, float radius ) {
		this( false, position, buffer, world, radius );
	}

	/**
	 * Create a new Anchor
	 * 
	 * @param special
	 *            set true if this is a player anchor, false otherwise
	 * @param position
	 *            starting position of anchor (in pixels)
	 * @param buffer
	 *            buffer around anchor which must always stay within view
	 */
	public Anchor( boolean special, Vector2 position, Vector2 buffer,
			World world, float radius ) {
		this.special = special;
		this.position = position;
		this.positionBox = new Vector2( position.x * Util.PIXEL_TO_BOX,
				position.y * Util.PIXEL_TO_BOX );
		this.buffer = buffer;
		this.activated = false;
		this.body = constructBody( world, radius );
		this.body.setTransform( positionBox, body.getAngle( ) );
	}

	/**
	 * Creates a body to hold the sensor fixture
	 * 
	 * @param world
	 * @param radius
	 * @return the reference to the body
	 */
	private Body constructBody( World world, float radius ) {
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.StaticBody;
		Body body = world.createBody( bodyDef );
		CircleShape sensorShape = new CircleShape( );
		sensorShape.setRadius( radius );
		FixtureDef sensor = new FixtureDef( );
		sensor.shape = sensorShape;
		sensor.isSensor = true;
		body.createFixture( sensor );
		body.setUserData( this );
		sensorShape.dispose( );
		return body;
	}

	/**
	 * get the buffer as a rectangle in pixels
	 * 
	 * @return
	 */
	public Rectangle getBufferRectangle( ) {
		return new Rectangle( position.x - ( buffer.x / 2 ), position.y
				- ( buffer.y / 2 ), buffer.x, buffer.y );
	}

	/**
	 * set the position Vector2 in pixels
	 * 
	 * @param newPosition
	 *            in pixels
	 */
	public void setPosition( Vector2 newPosition ) {
		this.position.x = newPosition.x;
		this.position.y = newPosition.y;
		this.positionBox.x = newPosition.x * Util.PIXEL_TO_BOX;
		this.positionBox.y = newPosition.y * Util.PIXEL_TO_BOX;
	}

	/**
	 * set the position Vector2 in box units (meters)
	 * 
	 * @param newPosition
	 *            in meters
	 */
	public void setPositionBox( Vector2 newPosition ) {
		this.positionBox.x = newPosition.x;
		this.positionBox.y = newPosition.y;
		this.position.x = newPosition.x * Util.BOX_TO_PIXEL;
		this.position.y = newPosition.y * Util.BOX_TO_PIXEL;
	}

	/**
	 * set the buffer
	 * 
	 * @param buffer
	 *            a Vector2 of the buffer's width and height
	 */
	public void setBuffer( Vector2 buffer ) {
		this.buffer = buffer;
	}

	/**
	 * activate the anchor
	 */
	public void activate( ) {
		activated = true;
	}

	/**
	 * deactivate the anchor
	 */
	public void deactivate( ) {
		activated = false;
	}
}
