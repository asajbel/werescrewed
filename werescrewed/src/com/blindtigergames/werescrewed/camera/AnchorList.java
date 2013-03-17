package com.blindtigergames.werescrewed.camera;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/*******************************************************************************
 * Stores a list of all current anchors in the world
 * 
 * @author Edward Ramirez
 ******************************************************************************/
public class AnchorList {

	protected class AnchorPair {
		protected Anchor first;
		protected Anchor second;

		protected void swap( ) {
			Anchor temp = first;
			first = second;
			second = temp;
		}
	}

	protected ArrayList< Anchor > anchorList;
	private Vector2 midpoint2;
	private Vector2 prevMidpoint;
	private Vector2 midpointVelocity;
	private Vector2 specialMidpoint;
	private static AnchorList instance;
	private ShapeRenderer shapeRenderer;
	private OrthographicCamera camera;
	private AnchorPair furthestX;
	private AnchorPair furthestY;

	private AnchorList( ) {
		this( null );
	}

	private AnchorList( OrthographicCamera camera ) {
		anchorList = new ArrayList< Anchor >( );
		new Vector2( 0f, 0f );
		midpoint2 = new Vector2( 0f, 0f );
		prevMidpoint = new Vector2( 0f, 0f );
		midpointVelocity = new Vector2( 0f, 0f );
		specialMidpoint = new Vector2( 0f, 0f );
		shapeRenderer = new ShapeRenderer( );
		this.camera = camera;
	}

	public static AnchorList getInstance( ) {
		if ( instance == null ) {
			instance = new AnchorList( );
		}
		return instance;
	}

	public static AnchorList getInstance( OrthographicCamera camera ) {
		if ( instance == null ) {
			instance = new AnchorList( camera );
		}
		return instance;
	}

	public void update( ) {
		update( false );
	}

	public void update( boolean debugRender ) {

		// Update timers
		for ( Anchor curAnchor : anchorList ) {
			if ( curAnchor.getTimer( ) > 0 && curAnchor.activated == true )
				// Gdx.app.log( "timer", curAnchor.getTimer( ) + "" );
				curAnchor.decrementTimer( );

			// Safety check & deactivate
			if ( curAnchor.getTimer( ) == 0 ) {
				curAnchor.setTimer( 0 );
				curAnchor.deactivate( );
			}
		}

		// update velocity of midpoint
		setMidpoint( );
		midpointVelocity.x = midpoint2.x;
		midpointVelocity.y = midpoint2.y;
		midpointVelocity.sub( prevMidpoint );

		prevMidpoint.x = midpoint2.x;
		prevMidpoint.y = midpoint2.y;

		// render anchor points + buffer
		if ( camera != null && debugRender ) {
			for ( Anchor curAnchor : anchorList ) {
				// renders a square of buffer width and height
				shapeRenderer.setProjectionMatrix( camera.combined );
				shapeRenderer.begin( ShapeType.Rectangle );
				shapeRenderer.identity( );
				Rectangle drawRect = curAnchor.getBufferRectangle( );
				shapeRenderer.rect( drawRect.x, drawRect.y, drawRect.width,
						drawRect.height );
				shapeRenderer.end( );

				// renders a cross through the square if the current anchor is
				// special (i.e. the player)
				if ( curAnchor.special ) {

					shapeRenderer.begin( ShapeType.Line );
					shapeRenderer.line( drawRect.x, drawRect.y, drawRect.x
							+ drawRect.width, drawRect.y + drawRect.height );
					shapeRenderer.line( drawRect.x, drawRect.y
							+ drawRect.height, drawRect.x + drawRect.width,
							drawRect.y );
					shapeRenderer.end( );
				}
			}
		}
	}

	public void addAnchor( Anchor newAnchor ) {
		anchorList.add( newAnchor );
	}

	public void clear( ) {
		anchorList.clear( );
	}

	/**
	 * set an anchor's position in pixels
	 * 
	 * @param id
	 *            the int ID of the anchor
	 * @param position
	 *            the new position in pixels for the anchor
	 */
	public void setAnchorPos( int id, Vector2 position ) {
		// assuming pass by value, try pass by reference later
		Anchor temp = anchorList.get( id );
		temp.setPosition( position );
		anchorList.set( id, temp );
	}

	/**
	 * set the anchor's position in box2D units
	 * 
	 * @param id
	 *            the int ID of the anchor
	 * @param position
	 *            the new position in box2D units for the anchor
	 */
	public void setAnchorPosBox( int id, Vector2 position ) {
		// assuming pass by value, try pass by reference later
		Anchor temp = anchorList.get( id );
		temp.setPositionBox( position );
		anchorList.set( id, temp );
	}

	/**
	 * set anchor's buffer
	 * 
	 * @param id
	 *            the int ID of the anchor
	 * @param buffer
	 *            vecter2(width, height) of new buffer
	 */
	public void setAnchorBuffer( int id, Vector2 buffer ) {
		Anchor temp = anchorList.get( id );
		temp.setBuffer( buffer );
		anchorList.set( id, temp );
	}

	/**
	 * get an anchor's position in pixels
	 * 
	 * @param id
	 *            the int ID of the anchor
	 * @return anchor's position in pixels
	 */
	public Vector2 getAnchorPos( int id ) {
		return anchorList.get( id ).position;
	}

	/**
	 * get the anchor's position in box2D units
	 * 
	 * @param id
	 *            the int ID of the anchor
	 */
	public Vector2 getAnchorPosBox( int id ) {
		return anchorList.get( id ).positionBox;
	}

	/**
	 * get anchor's buffer
	 * 
	 * @param id
	 *            the int ID of the anchor
	 */
	public Vector2 getAnchorBuffer( int id ) {
		return anchorList.get( id ).buffer;
	}

	public Vector2 getSepcialMidpoint( ) {
		return specialMidpoint;
	}

	public float specialDistance( ) {
		if ( anchorList.size( ) < 2 )
			return 0.0f;

		boolean foundFirst = false;
		for ( Anchor curAnchor : anchorList ) {
			if ( curAnchor.special ) {
				if ( !foundFirst ) {
					foundFirst = true;
					specialMidpoint.x = curAnchor.position.x;
					specialMidpoint.y = curAnchor.position.y;
				} else {
					specialMidpoint.sub( curAnchor.position );
				}
			}
		}

		return specialMidpoint.len( );
	}

	/**
	 * Get longest x and y distance between anchors plus their buffer widths and
	 * heights respectively
	 * 
	 * @return a Vector 2 where: x = the longest x distance between anchors plus
	 *         their buffer widths and y = the longest y distance between
	 *         anchors plus their buffer heights
	 */
	public Vector2 getLongestXYDist( ) {
		Vector2 vectDist = new Vector2( 0f, 0f );

		// Start with players (guaranteed to be present and active)
		AnchorPair pair = getSpecialPair( );

		if ( pair.first.position.x > pair.second.position.x )
			pair.swap( );

		// Find anchor with left- and right-most buffers //

		// For each anchor
		for ( Anchor curAnchor : anchorList ) {
			// Making sure its active and not the players (since we already
			// included them)
			if ( curAnchor.activated && !curAnchor.special ) {
				// If the current anchor (minus the buffer) is to the left of
				// the one tracked, replace it
				if ( curAnchor.position.x - curAnchor.buffer.x < pair.first.position.x
						- pair.first.buffer.x ) {
					pair.first = curAnchor;
				}
				// If the current anchor (plus the buffer) is to the right of
				// the one tracked, replace it
				if ( curAnchor.position.x + curAnchor.buffer.x > pair.second.position.x
						+ pair.second.buffer.x ) {
					pair.second = curAnchor;
				}
			}
		}
		furthestX = pair;

		if ( pair != null && pair.first != null && pair.second != null ) {
			// set x distance
			vectDist.x = ( pair.second.position.x + pair.second.buffer.x )
					- ( pair.first.position.x - pair.first.buffer.x );
		}

		// Find longest y distance //
		pair = getSpecialPair( );

		if ( pair.first.position.y > pair.second.position.y )
			pair.swap( );

		// Find anchor with top- and bottom-most buffers //

		// For each anchor
		for ( Anchor curAnchor : anchorList ) {
			// Making sure its active and not the players (since we already
			// included them)
			if ( curAnchor.activated && !curAnchor.special ) {
				// If the current anchor (minus the buffer) is below the one
				// tracked, replace it
				if ( curAnchor.position.y - curAnchor.buffer.y < pair.first.position.y
						- pair.first.buffer.y ) {
					pair.first = curAnchor;
				}
				// If the current anchor (plus the buffer) is above the one
				// tracked, replace it
				if ( curAnchor.position.y + curAnchor.buffer.y > pair.second.position.y
						+ pair.second.buffer.y ) {
					pair.second = curAnchor;
				}
			}
		}
		furthestY = pair;

		if ( pair != null && pair.first != null && pair.second != null ) {
			// set x distance
			vectDist.y = ( pair.second.position.y + pair.second.buffer.y )
					- ( pair.first.position.y - pair.first.buffer.y );
		}

		return vectDist;
	}

	public Vector2 getMidpoint( ) {
		return midpoint2;
	}

	public Vector2 getMidpointVelocity( ) {
		return midpointVelocity;
	}

	protected AnchorPair getSpecialPair( ) {
		AnchorPair returnPair = new AnchorPair( );
		returnPair.first = null;
		returnPair.second = null;
		boolean foundFirst = false;
		// for now, just the first 2 special anchors (the players)
		for ( Anchor curAnchor : anchorList ) {
			if ( curAnchor.special ) {
				if ( !foundFirst ) {
					returnPair.first = curAnchor;
					foundFirst = true;
				} else {
					returnPair.second = curAnchor;
					break;
				}
			}
		}

		if ( returnPair.first != null && returnPair.second != null )
			return returnPair;
		else
			return null;
	}

	private void setMidpoint( ) {
		Vector2 longest = getLongestXYDist( );
		midpoint2.x = furthestX.first.position.x;
		midpoint2.x -= furthestX.first.buffer.x;
		midpoint2.x += longest.x / 2;
		midpoint2.y = furthestY.first.position.y;
		midpoint2.y -= furthestY.first.buffer.y;
		midpoint2.y += longest.y / 2;
		// int count = 0;
		// sum.x = 0f;
		// sum.y = 0f;
		// for ( Anchor curAnchor : anchorList ) {
		// if ( curAnchor != null
		// && ( curAnchor.activated || curAnchor.special ) ) {
		// sum.add( curAnchor.position );
		// count++;
		// }
		// }
		// sum.div( ( float ) count );
		// midpoint2.x = sum.x;
		// midpoint2.y = sum.y;
		//
		// // set special midpoint
		// int count = 0;
		// sum.x = 0f;
		// sum.y = 0f;
		//
		// for ( Anchor curAnchor : anchorList ) {
		// if ( curAnchor.special && curAnchor.activated ) {
		// sum.add( curAnchor.position );
		// count++;
		// }
		// }
		//
		// sum.div( ( float ) count );
		// specialMidpoint.x = sum.y;
		// specialMidpoint.y = sum.y;
	}
}