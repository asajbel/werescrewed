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
	protected Texture texture;

	public static final float TILE_SIZE = 32.0f;

	public Pipe( String name, Vector2 pos, ArrayList< Vector2 > path,
			Texture tex, World world ) {
		super( name, pos, tex, world );
		position = new Vector2( pos.x * Util.PIXEL_TO_BOX, pos.y
				* Util.PIXEL_TO_BOX );
		this.path = path;
		this.currentPos = new Vector2( );
		start = new Vector2( );
		tiles = new ArrayList< Tile >( );
		texture = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/common/pipe/pipeLR.png", Texture.class );
		Vector2 currentPair;
		int numberOfSegments;
		previousDirection = null;

		BodyDef bodyDef = new BodyDef( );
		bodyDef.position.set( position );
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.gravityScale = 0.1f;
		body = world.createBody( bodyDef );
		body.setUserData( this );
		
		Vector2 previousPair = new Vector2(); 

		for ( int i = 0; i < path.size( ); i++ ) {
			
			currentPair = new Vector2(path.get( i ));
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
		
		TextureAtlas commonTextures = WereScrewedGame.manager.getAtlas( "common-textures" );
		String texName = "";
		
		switch ( currentDirection ){
		case LEFT:
			if ( previousDirection == Direction.UP) {
				texName = "pipeUL";
			} else if ( previousDirection == Direction.DOWN ) {
				texName = "pipeDL";
			} else 
				texName = "pipeEndR";
			break;
		case RIGHT:
			if ( previousDirection == Direction.UP) {
				texName = "pipeUR";
			} else if ( previousDirection == Direction.DOWN ) {
				texName = "pipeDR";
			} else 
				texName = "pipeEndL";
			break;
		case UP:
			if ( previousDirection == Direction.LEFT) {
				texName = "pipeUL";
			} else if ( previousDirection == Direction.RIGHT ) {
				texName = "pipUR";
			} else 
				texName = "pipeEndD";
			break;
		case DOWN:
			if ( previousDirection == Direction.LEFT) {
				texName = "pipeDL";
			} else if ( previousDirection == Direction.RIGHT ) {
				texName = "pipDR";
			} else 
				texName = "pipeEndU";
			break;
		}
		
		tempSprite = commonTextures.createSprite( texName );
		offset_x = currentPos.x * Util.BOX_TO_PIXEL;
		offset_y = currentPos.y * Util.BOX_TO_PIXEL;
		tempSprite.setOrigin( -offset_x + TILE_SIZE, -offset_y + TILE_SIZE );
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

		Vector2 distance = new Vector2(currentPos.x - start.x, currentPos.y - start.y); 
		
		for ( int i = 1; i < numberOfSegments; i++ ) {
			if ( i != numberOfSegments - 1) {
				switch ( currentDirection ){
				case LEFT:
				case RIGHT:
					texName = "pipeLR";
					break;
				case UP:
				case DOWN:
					texName = "pipeUD";
					break;
				}
			} else {
				switch ( currentDirection ){
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
			if (currentDirection == Direction.RIGHT || currentDirection == Direction.LEFT) {
				offset_x = (start.x + ( i / (float) numberOfSegments ) * distance.x)
					* Util.BOX_TO_PIXEL;
				offset_y = currentPos.y * (float) Util.BOX_TO_PIXEL;
			} else {
				offset_x = currentPos.x * (float) Util.BOX_TO_PIXEL;
				offset_y = (start.y + ( i / (float) numberOfSegments ) * distance.y)
					* Util.BOX_TO_PIXEL;
			}
			tempSprite.setOrigin( -offset_x + TILE_SIZE, -offset_y + TILE_SIZE );
			temp = new Tile( offset_x, offset_y, tempSprite );
			tiles.add( temp );

		}

		/*
		 * for ( int i = 0; i < numberOfSegments; i++ ){ Sprite tempSprite;
		 * System.out.println(startPos.x + ", " + startPos.y); PipeTile temp =
		 * new PipeTile(name, world, startPos, null );
		 * 
		 * //for first pipe in each segment if ( i == 0 ){ if (
		 * previousDirection != null ){ //if not the first segment, change pipe
		 * to elbow sprite switch ( previousDirection ){ case LEFT: if (
		 * currentDirection == Direction.UP ){ tempSprite = constructSprite( (
		 * Texture ) WereScrewedGame.manager .get( WereScrewedGame.dirHandle +
		 * "/common/pipe/pipeUR.png" ) ); temp.changeSprite( tempSprite ); }
		 * else if ( currentDirection == Direction.DOWN ){ tempSprite =
		 * constructSprite( ( Texture ) WereScrewedGame.manager .get(
		 * WereScrewedGame.dirHandle + "/common/pipe/pipeRD.png" ) );
		 * temp.changeSprite( tempSprite ); } break; case RIGHT: if (
		 * currentDirection == Direction.UP ){ tempSprite = constructSprite( (
		 * Texture ) WereScrewedGame.manager .get( WereScrewedGame.dirHandle +
		 * "/common/pipe/pipeLU.png" ) ); temp.changeSprite( tempSprite ); }
		 * else if ( currentDirection == Direction.DOWN ){ tempSprite =
		 * constructSprite( ( Texture ) WereScrewedGame.manager .get(
		 * WereScrewedGame.dirHandle + "/common/pipe/pipeDL.png" ) );
		 * temp.changeSprite( tempSprite ); } break; case UP: if (
		 * currentDirection == Direction.LEFT ){ tempSprite = constructSprite( (
		 * Texture ) WereScrewedGame.manager .get( WereScrewedGame.dirHandle +
		 * "/common/pipe/pipeDL.png" ) ); temp.changeSprite( tempSprite ); }
		 * else if ( currentDirection == Direction.RIGHT ) { tempSprite =
		 * constructSprite( ( Texture ) WereScrewedGame.manager .get(
		 * WereScrewedGame.dirHandle + "/common/pipe/pipeRD.png" ) );
		 * temp.changeSprite( tempSprite ); } break; case DOWN: if (
		 * currentDirection == Direction.LEFT ){ tempSprite = constructSprite( (
		 * Texture ) WereScrewedGame.manager .get( WereScrewedGame.dirHandle +
		 * "/common/pipe/pipeLU.png" ) ); temp.changeSprite( tempSprite ); }
		 * else if ( currentDirection == Direction.RIGHT ) { tempSprite =
		 * constructSprite( ( Texture ) WereScrewedGame.manager .get(
		 * WereScrewedGame.dirHandle + "/common/pipe/pipeUR.png" ) );
		 * temp.changeSprite( tempSprite ); } break; } } else { //if first
		 * segment, change pipe to end sprite switch ( currentDirection ){ case
		 * LEFT: tempSprite = constructSprite( ( Texture )
		 * WereScrewedGame.manager .get( WereScrewedGame.dirHandle +
		 * "/common/pipe/pipeEndR.png" ) ); temp.changeSprite( tempSprite );
		 * break; case RIGHT: tempSprite = constructSprite( ( Texture )
		 * WereScrewedGame.manager .get( WereScrewedGame.dirHandle +
		 * "/common/pipe/pipeEndL.png" ) ); temp.changeSprite( tempSprite );
		 * break; case UP: tempSprite = constructSprite( ( Texture )
		 * WereScrewedGame.manager .get( WereScrewedGame.dirHandle +
		 * "/common/pipe/pipeEndD.png" ) ); temp.changeSprite( tempSprite );
		 * break; case DOWN: tempSprite = constructSprite( ( Texture )
		 * WereScrewedGame.manager .get( WereScrewedGame.dirHandle +
		 * "/common/pipe/pipeEndU.png" ) ); temp.changeSprite( tempSprite );
		 * break; } } } else { //if not first pipe, change sprite to appropriate
		 * direction switch ( currentDirection ){ case LEFT: case RIGHT:
		 * tempSprite = constructSprite( ( Texture ) WereScrewedGame.manager
		 * .get( WereScrewedGame.dirHandle + "/common/pipe/pipeLR.png" ) );
		 * temp.changeSprite( tempSprite ); break; case UP: case DOWN:
		 * tempSprite = constructSprite( ( Texture ) WereScrewedGame.manager
		 * .get( WereScrewedGame.dirHandle + "/common/pipe/pipeUD.png" ) );
		 * temp.changeSprite( tempSprite ); break; } }
		 * 
		 * //if last pipe && last segment, change sprite to appropriate end
		 * sprite if ( lastSegment && i == numberOfSegments - 1 ){ switch (
		 * currentDirection ){ case LEFT: tempSprite = constructSprite( (
		 * Texture ) WereScrewedGame.manager .get( WereScrewedGame.dirHandle +
		 * "/common/pipe/pipeEndL.png" ) ); temp.changeSprite( tempSprite );
		 * break; case RIGHT: tempSprite = constructSprite( ( Texture )
		 * WereScrewedGame.manager .get( WereScrewedGame.dirHandle +
		 * "/common/pipe/pipeEndR.png" ) ); temp.changeSprite( tempSprite );
		 * break; case UP: tempSprite = constructSprite( ( Texture )
		 * WereScrewedGame.manager .get( WereScrewedGame.dirHandle +
		 * "/common/pipe/pipeEndU.png" ) ); temp.changeSprite( tempSprite );
		 * break; case DOWN: tempSprite = constructSprite( ( Texture )
		 * WereScrewedGame.manager .get( WereScrewedGame.dirHandle +
		 * "/common/pipe/pipeEndD.png" ) ); temp.changeSprite( tempSprite );
		 * break; } }
		 * 
		 * switch ( currentDirection ){ case LEFT: startPos.x -= 32.0f; break;
		 * case RIGHT: startPos.x += 32.0f; break; case UP: startPos.y -= 32.0f;
		 * break; case DOWN: startPos.y += 32.0f; break; }
		 * 
		 * segments.add( temp ); }
		 */
	}

	@Override 
	public void draw( SpriteBatch batch , float deltaTime ) {

		// for ( int i = 0; i < segments.size(); i++ )
		// segments.get( i ).draw( batch );
		for ( Tile a : tiles ) {
			a.tileSprite.setPosition( 
					body.getPosition( ).x * Util.BOX_TO_PIXEL - TILE_SIZE + a.xOffset,
					body.getPosition( ).y * Util.BOX_TO_PIXEL - TILE_SIZE + a.yOffset );
			a.tileSprite.setRotation( MathUtils.radiansToDegrees
					* body.getAngle( ) );
			a.tileSprite.draw( batch );
		}
	}

}
