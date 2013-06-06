package com.blindtigergames.werescrewed.entity.platforms;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.util.Util;

public class Pipe extends Platform {

	protected enum Direction {
		LEFT, RIGHT, UP, DOWN
	}

	protected ArrayList< Vector2 > path;
	protected ArrayList< Fixture > pipes;
	protected ArrayList< Tile > tiles;
	protected Vector2 start;
	protected Vector2 position;
	protected Vector2 currentPos;
	protected Direction currentDirection;
	protected Direction nextDirection;
	protected Direction previousDirection;
	protected boolean open;

	public static final float TILE_SIZE = 32.0f;

	public Pipe( String name, Vector2 pos, ArrayList< Vector2 > path,
			Texture tex, World world, boolean openEnded ) {
		super( name, pos, tex, world );
		open = openEnded;
		this.path = path;
		this.currentPos = new Vector2( );
		start = new Vector2( );
		tiles = new ArrayList< Tile >( );
		Vector2 currentPair;
		int numberOfSegments;
		previousDirection = null;

		boolean bodymake = true;

		Vector2 previousPair = new Vector2( );

		for ( int i = 0; i < path.size( ); i++ ) {

			currentPair = new Vector2( path.get( i ) );
			currentPair = currentPair.sub( previousPair );
			previousPair = path.get( i );

			// error-check coordinate pairs, only allowing for movement in one
			// direction
			if ( ( ( currentPair.x == 0 ) && ( currentPair.y == 0 ) )
					|| ( ( currentPair.x != 0 ) && ( currentPair.y != 0 ) ) ) {
				break;
			}

			// determine which direction this segment of pipe will run
			if ( currentPair.x < 0 )
				currentDirection = Direction.LEFT;
			else if ( currentPair.x > 0 )
				currentDirection = Direction.RIGHT;
			else if ( currentPair.y < 0 )
				currentDirection = Direction.DOWN;
			else if ( currentPair.y > 0 )
				currentDirection = Direction.UP;

			if ( bodymake ) {

				switch ( currentDirection ) {
				case DOWN:
					position = new Vector2( pos.x * Util.PIXEL_TO_BOX,
							( pos.y - TILE_SIZE ) * Util.PIXEL_TO_BOX );
					break;
				case LEFT:
					position = new Vector2( ( pos.x - TILE_SIZE )
							* Util.PIXEL_TO_BOX, pos.y * Util.PIXEL_TO_BOX );
					break;
				case RIGHT:
					position = new Vector2( ( pos.x + TILE_SIZE )
							* Util.PIXEL_TO_BOX, pos.y * Util.PIXEL_TO_BOX );
					break;
				case UP:
					position = new Vector2( pos.x * Util.PIXEL_TO_BOX,
							( pos.y + TILE_SIZE ) * Util.PIXEL_TO_BOX );
					break;
				default:
					break;

				}
				BodyDef bodyDef = new BodyDef( );
				bodyDef.position.set( position );
				bodyDef.type = BodyType.KinematicBody;
				bodyDef.gravityScale = 0.1f;
				body = world.createBody( bodyDef );
				body.setUserData( this );
				bodymake = false;
			}

			if ( currentPair.x != 0 )
				numberOfSegments = ( int ) Math.abs( currentPair.x );
			else
				numberOfSegments = ( int ) Math.abs( currentPair.y );

			if ( i != path.size( ) - 1 ) {
				constructPipeSegments( name, currentPos, numberOfSegments,
						world, false );
			} else
				constructPipeSegments( name, currentPos, numberOfSegments,
						world, true );

			previousDirection = currentDirection;

		}
	}

	protected void constructPipeSegments( String name, Vector2 pos,
			int numberOfSegments, World world, boolean lastSegment ) {
		Tile temp;
		Sprite tempSprite;
		float offset_x = 0;
		float offset_y = 0;

		start.x = currentPos.x;
		start.y = currentPos.y;

		TextureAtlas commonTextures = WereScrewedGame.manager
				.getAtlas( "common-textures" );
		String texName = "";

		switch ( currentDirection ) {
		case LEFT:
			if ( previousDirection == Direction.UP ) {
				texName = "pipeDL";
			} else if ( previousDirection == Direction.DOWN ) {
				texName = "pipeUL";
			} else
				texName = "pipeLR";
			break;
		case RIGHT:
			if ( previousDirection == Direction.UP ) {
				texName = "pipeDR";
			} else if ( previousDirection == Direction.DOWN ) {
				texName = "pipeUR";
			} else
				texName = "pipeLR";
			break;
		case UP:
			if ( previousDirection == Direction.LEFT ) {
				texName = "pipeUR";
			} else if ( previousDirection == Direction.RIGHT ) {
				texName = "pipeUL";
			} else
				texName = "pipeUD";
			break;
		case DOWN:
			if ( previousDirection == Direction.LEFT ) {
				texName = "pipeDR";
			} else if ( previousDirection == Direction.RIGHT ) {
				texName = "pipeDL";
			} else
				texName = "pipeUD";
			break;
		}

		tempSprite = commonTextures.createSprite( texName );
		offset_x = currentPos.x * Util.BOX_TO_PIXEL - tempSprite.getWidth( )
				/ 2;
		offset_y = currentPos.y * Util.BOX_TO_PIXEL - tempSprite.getHeight( )
				/ 2;
		tempSprite.setOrigin( -offset_x, -offset_y );
		tempSprite.setColor( .9f, 0.7f, 0.1f, 1 );
		temp = new Tile( offset_x, offset_y, tempSprite );
		tiles.add( temp );

		switch ( currentDirection ) {
		case LEFT:
			currentPos.x -= TILE_SIZE * ( numberOfSegments - 1 )
					* Util.PIXEL_TO_BOX;
			break;
		case RIGHT:
			currentPos.x += TILE_SIZE * ( numberOfSegments - 1 )
					* Util.PIXEL_TO_BOX;
			break;
		case UP:
			currentPos.y += TILE_SIZE * ( numberOfSegments - 1 )
					* Util.PIXEL_TO_BOX;
			break;
		case DOWN:
			currentPos.y -= TILE_SIZE * ( numberOfSegments - 1 )
					* Util.PIXEL_TO_BOX;
			break;
		}

		PolygonShape polygonShape = new PolygonShape( );
		if ( currentDirection == Direction.LEFT
				|| currentDirection == Direction.RIGHT ) {
			polygonShape.setAsBox( numberOfSegments * TILE_SIZE
					* Util.PIXEL_TO_BOX, TILE_SIZE * Util.PIXEL_TO_BOX,
					currentPos, body.getAngle( ) );
		} else {
			polygonShape.setAsBox( TILE_SIZE * Util.PIXEL_TO_BOX,
					numberOfSegments * TILE_SIZE * Util.PIXEL_TO_BOX,
					currentPos, body.getAngle( ) );
		}
		FixtureDef fixtureDef = new FixtureDef( );
		fixtureDef.filter.categoryBits = Util.CATEGORY_EVERYTHING;
		fixtureDef.filter.maskBits = Util.CATEGORY_EVERYTHING;
		fixtureDef.shape = polygonShape;
		fixtureDef.density = 1f;
		fixtureDef.restitution = 0.0f;
		fixtureDef.friction = 1.0f;
		body.createFixture( fixtureDef );

		switch ( currentDirection ) {
		case LEFT:
			currentPos.x -= TILE_SIZE * ( numberOfSegments + 1 )
					* Util.PIXEL_TO_BOX;
			break;
		case RIGHT:
			currentPos.x += TILE_SIZE * ( numberOfSegments + 1 )
					* Util.PIXEL_TO_BOX;
			break;
		case UP:
			currentPos.y += TILE_SIZE * ( numberOfSegments + 1 )
					* Util.PIXEL_TO_BOX;
			break;
		case DOWN:
			currentPos.y -= TILE_SIZE * ( numberOfSegments + 1 )
					* Util.PIXEL_TO_BOX;
			break;
		}

		Vector2 distance = new Vector2( currentPos.x - start.x, currentPos.y
				- start.y );

		for ( int i = 1; i < numberOfSegments; i++ ) {
			switch ( currentDirection ) {
			case LEFT:
			case RIGHT:
				texName = "pipeLR";
				break;
			case UP:
			case DOWN:
				texName = "pipeUD";
				break;
			}
			if ( i == numberOfSegments - 1 && lastSegment && !open ) {
				switch ( currentDirection ) {
				case LEFT:
					texName = "pipeEndL";
					break;
				case RIGHT:
					texName = "pipeEndR";
					break;
				case UP:
					texName = "pipeEndU";
					break;
				case DOWN:
					texName = "pipeEndD";
					break;
				}
			}

			tempSprite = commonTextures.createSprite( texName );
			if ( currentDirection == Direction.RIGHT
					|| currentDirection == Direction.LEFT ) {
				offset_x = ( start.x + ( i / ( float ) numberOfSegments )
						* distance.x )
						* Util.BOX_TO_PIXEL - tempSprite.getWidth( ) / 2;
				offset_y = currentPos.y * ( float ) Util.BOX_TO_PIXEL
						- tempSprite.getHeight( ) / 2;
			} else {
				offset_x = currentPos.x * ( float ) Util.BOX_TO_PIXEL
						- tempSprite.getWidth( ) / 2;
				offset_y = ( start.y + ( i / ( float ) numberOfSegments )
						* distance.y )
						* Util.BOX_TO_PIXEL - tempSprite.getHeight( ) / 2;
			}
			tempSprite.setColor( .9f, 0.7f, 0.1f, 1 );
			tempSprite.setOrigin( -offset_x, -offset_y );
			temp = new Tile( offset_x, offset_y, tempSprite );
			tiles.add( temp );

		}
	}

	@Override
	public void draw( SpriteBatch batch, float deltaTime, Camera camera ) {

		// for ( int i = 0; i < segments.size(); i++ )
		// segments.get( i ).draw( batch );
		// Color c = batch.getColor( );
		// batch.setColor( 1f, 0f, 0f, 1f );
		for ( Tile a : tiles ) {
			a.tileSprite.setPosition( body.getPosition( ).x * Util.BOX_TO_PIXEL
					+ a.xOffset, body.getPosition( ).y * Util.BOX_TO_PIXEL
					+ a.yOffset );
			a.tileSprite.setRotation( MathUtils.radiansToDegrees
					* body.getAngle( ) );
			if ( a.tileSprite.getBoundingRectangle( ).overlaps(
					camera.getBounds( ) ) ) {
				a.tileSprite.draw( batch );
			}
		}
		// batch.setColor( c.r, c.g, c.b, c.a );
	}

	public void setOpen( boolean open2 ) {
		open = open2;
	}

}
