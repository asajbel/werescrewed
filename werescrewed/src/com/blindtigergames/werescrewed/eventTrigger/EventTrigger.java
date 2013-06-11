package com.blindtigergames.werescrewed.eventTrigger;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityType;
import com.blindtigergames.werescrewed.entity.action.IAction;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.player.Player;
import com.blindtigergames.werescrewed.util.Util;

public class EventTrigger extends Platform {

	protected boolean repeatable = false;
	protected boolean activated = false;
	protected boolean beginTriggeredOnce = false, endTriggeredOnce = false;
	protected boolean twoPlayersToActivate = false,
			twoPlayersToDeactivate = false;
	protected boolean playerOneContact = false, playerTwoContact = false;
	protected ArrayList< Entity > entityList;
	protected IAction beginAction, endAction;
	public boolean actOnEntity = false;

	int counter = 0;

	public EventTrigger( String name, World world ) {
		super( name, Vector2.Zero, null, world );
		// String name, Vector2 pos, Texture tex, World world
		// this.world = world;
		entityType = EntityType.EVENTTRIGGER;
		entityList = new ArrayList< Entity >( );
	}

	public void constructCircleBody( float radiusPixel, Vector2 positionPixel ) {

		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set( positionPixel.mul( Util.PIXEL_TO_BOX ) );
		body = world.createBody( bodyDef );

		CircleShape circle = new CircleShape( );
		circle.setRadius( radiusPixel * Util.PIXEL_TO_BOX );

		FixtureDef fixture = new FixtureDef( );
		fixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		fixture.filter.maskBits = Util.CATEGORY_EVERYTHING;
		fixture.isSensor = true;
		fixture.shape = circle;

		body.createFixture( fixture );
		body.setFixedRotation( true );
		body.setUserData( this );

		circle.dispose( );
	}

	public void contructRectangleBody( float heightPixels, float widthPixels,
			Vector2 positionPixel ) {
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set( positionPixel.mul( Util.PIXEL_TO_BOX ) );
		body = world.createBody( bodyDef );

		PolygonShape polygon = new PolygonShape( );
		polygon.setAsBox( heightPixels / 2 * Util.PIXEL_TO_BOX, widthPixels / 2
				* Util.PIXEL_TO_BOX );

		FixtureDef fixture = new FixtureDef( );
		fixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		fixture.filter.maskBits = Util.CATEGORY_EVERYTHING;
		fixture.isSensor = true;
		fixture.shape = polygon;

		body.createFixture( fixture );
		body.setFixedRotation( true );
		body.setUserData( this );

		polygon.dispose( );
	}

	public void constructVertBody( Array< Vector2 > vertices,
			Vector2 positionPixel, float additionalBorderPix ) {
		BodyDef bodyDef = new BodyDef( );
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set( positionPixel.mul( Util.PIXEL_TO_BOX ) );
		body = world.createBody( bodyDef );

		PolygonShape polygon = new PolygonShape( );
		Vector2[ ] verts = new Vector2[ vertices.size - 1 ];

		// MAKE SURE START POINT IS IN THE MIDDLE
		// AND SECOND AND END POINT ARE THE SAME POSITION
		int i = 0;
		for ( int j = 0; j < vertices.size; j++ ) {
			if ( j == vertices.size - 1 )
				continue;
			Vector2 v = vertices.get( j );
			verts[ i ] = new Vector2( v.x * Util.PIXEL_TO_BOX, v.y
					* Util.PIXEL_TO_BOX );
			Vector2 norm = verts[ i ].cpy( ).nor( ).mul( additionalBorderPix )
					.mul( Util.PIXEL_TO_BOX );
			verts[ i ].add( norm );
			++i;
		}
		polygon.set( verts );

		FixtureDef fixture = new FixtureDef( );
		fixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		fixture.filter.maskBits = Util.CATEGORY_EVERYTHING;
		fixture.isSensor = true;
		fixture.shape = polygon;

		body.createFixture( fixture );
		body.setFixedRotation( true );
		body.setUserData( this );

		polygon.dispose( );
	}

	/**
	 * For use with a skeleton for fg and bg triggers
	 * 
	 * @param vertsPixels
	 *            The skeleton background's polysprite points.
	 * @param positionPixel
	 */
	@SuppressWarnings( "unused" )
	private void constructPolygonBody( Array< Vector2 > vertsPixels,
			Vector2 positionPixel, float additionalBorderPix ) {
//		BodyDef bodyDef = new BodyDef( );
//		bodyDef.type = BodyType.StaticBody;
//		bodyDef.position.set( positionPixel.cpy( ).mul( Util.PIXEL_TO_BOX ) );
//		body = world.createBody( bodyDef );
//
//		// Deep copy verts so we can turn the pixel position into meters.
//		// We also have to modify the size of the points to give the skeletons a
//		// buffer in which
//		// they activate/deactivate.
//		Vector2[ ] vertsMeters;
//		int size = vertsPixels.size;
//		while ( vertsPixels.get( size - 1 ) == null )
//			--size;
//		if ( vertsPixels.get( size - 1 ).equals( vertsPixels.get( 0 ) ) ) {
//			size -= 1;
//		}
//		vertsMeters = new Vector2[ size ];
//
//		for ( int i = 0; i < vertsMeters.length; ++i ) {
//			Vector2 newPoint = vertsPixels.get( i ).cpy( )
//					.mul( Util.PIXEL_TO_BOX );
//			Vector2 norm = newPoint.cpy( ).nor( ).mul( additionalBorderPix )
//					.mul( Util.PIXEL_TO_BOX );// may divide by 0
//			newPoint.add( norm );
//			vertsMeters[ i ] = newPoint;
//			// Gdx.app.log( "ET:", "From point"+vertsPixels.get( i ).cpy( ).mul(
//			// 1 )+" To:"+newPoint.cpy( ).mul( Util.BOX_TO_PIXEL ) );
//		}
//
//		PolygonShape polygon = new PolygonShape( );
//		polygon.set( vertsMeters );
//
//		FixtureDef fixture = new FixtureDef( );
//		fixture.filter.categoryBits = Util.CATEGORY_SCREWS;
//		fixture.filter.maskBits = Util.CATEGORY_EVERYTHING;
//		fixture.isSensor = true;
//		fixture.shape = polygon;
//
//		body.createFixture( fixture );
//		body.setFixedRotation( true );
//		body.setUserData( this );
//
//		polygon.dispose( );
	}

	/**
	 * tells whether the action applies to an entity or not
	 * 
	 * @return boolean
	 */
	public boolean isActingOnEntity( ) {
		return actOnEntity;
	}

	/**
	 * sets whether the action applies to an entity or not
	 * 
	 * @param boolean
	 */
	public void setActingOnEntity( boolean value ) {
		actOnEntity = value;
	}

	/**
	 * checks whether this EventTrigger is repeatable, which means player can
	 * exit the sensor and then collide again and the event will trigger again
	 * 
	 * @return boolean
	 * @author Ranveer
	 */
	public boolean isRepeatable( ) {
		return repeatable;
	}

	/**
	 * sets this EventTrigger to repeatable or not, if repeatable this event
	 * will trigger when the player stops contact and then collides again
	 * 
	 * @param repeatable
	 *            - boolean
	 * @author Ranveer
	 */
	public void setRepeatable( boolean repeatable ) {
		this.repeatable = repeatable;
	}

	/**
	 * sets boolean to see if this event takes two players to turn on
	 * 
	 * @author Ranveer
	 */
	public void setTwoPlayersToActivate( boolean b ) {
		twoPlayersToActivate = b;
	}

	/**
	 * returns boolean to see if this event takes two players to turn on
	 * 
	 * @return - boolean
	 * @author Ranveer
	 */
	public boolean takeTwoPlayersToActivate( ) {
		return twoPlayersToActivate;
	}

	/**
	 * sets boolean to see if this event takes two players to turn off
	 * 
	 * @author Ranveer
	 */
	public void setTwoPlayersToDeactivate( boolean b ) {
		twoPlayersToDeactivate = b;
	}

	/**
	 * returns boolean to see if this event takes two players to turn off
	 * 
	 * @return - boolean
	 * @author Ranveer
	 */
	public boolean takeTwoPlayersToDeactivate( ) {
		return twoPlayersToDeactivate;
	}

	/**
	 * returns boolean to see if player one is colliding with this event trigger
	 * 
	 * @return - boolean
	 * @author Ranveer
	 */
	public boolean playerOneColliding( ) {
		return playerOneContact;
	}

	/**
	 * returns boolean to see if player two is colliding with this event trigger
	 * 
	 * @return - boolean
	 * @author Ranveer
	 */
	public boolean playerTwoColliding( ) {
		return playerTwoContact;
	}

	/**
	 * Checks if player is currently colliding with this EventTrigger
	 * 
	 * @return boolean
	 * @author Ranveer
	 */
	public boolean isActivated( ) {
		return activated;
	}

	/**
	 * Sets activated to true or false, depending on player collision and
	 * depending on if it takes two players to activate/deactivate and if the
	 * event is repeatable or not
	 * 
	 * @param active
	 *            - boolean
	 * @author Ranveer
	 */
	public void setActivated( boolean active, String name ) {
		if ( active == true ) {

			// If it takes two players to turn it on
			if ( twoPlayersToActivate ) {
				if ( !playerOneContact ) {
					if ( name.equals( "player1" ) ) {
						playerOneContact = true;
					}
				}
				if ( !playerTwoContact ) {
					if ( name.equals( "player2" ) ) {
						playerTwoContact = true;
					}
				}
				// When both players collide, then turn it active
				if ( playerOneContact && playerTwoContact ) {
					this.activated = true;
				}
				// Else it takes only 1 player to turn it on
			} else {
				if ( name.equals( "player2" ) ) {
					playerTwoContact = true;
				}
				if ( name.equals( "player1" ) ) {
					playerOneContact = true;
				}
				this.activated = true;
			}
		} else if ( active == false ) {
			if ( twoPlayersToActivate ) {
				if ( playerOneContact ) {
					if ( name.equals( "player1" ) ) {
						playerOneContact = false;
					}
				}
				if ( playerTwoContact ) {
					if ( name.equals( "player2" ) ) {
						playerTwoContact = false;
					}
				}
				// When both players dont collide, then turn it off
				if ( !playerOneContact && !playerTwoContact ) {
					this.activated = false;
				}
				// Else it takes only 1 player to turn it on
			} else {
				if ( name.equals( "player2" ) ) {
					playerTwoContact = false;
				}
				if ( name.equals( "player1" ) ) {
					playerOneContact = false;
				}
				this.activated = false;
			}
		}
	}

	/**
	 * triggers the beginning Action depending on if it takes two players to run
	 * it or just one player
	 * 
	 * NOTE: if it takes two players to deactivate, the trigger will still call
	 * begin event twice, if that isn't what you want, then make it take two
	 * players to activate
	 * 
	 * @author Ranveer
	 */
	public void triggerBeginEvent( ) {
		if ( twoPlayersToActivate ) {
			if ( playerOneContact && playerTwoContact ) {
				runBeginAction( null );

			}
		} else {
			runBeginAction( null );
		}
	}

	public void triggerBeginEvent( Player player ) {
		if ( twoPlayersToActivate ) {
			if ( playerOneContact && playerTwoContact ) {
				runBeginAction( player );

			}
		} else {
			runBeginAction( player );
		}
	}

	/**
	 * triggers the ending Action depending on if it takes two players to run it
	 * or just one player
	 * 
	 * NOTE: if it takes two players to activate the trigger, then it will only
	 * call runEndAction, if it has been activated already
	 * 
	 * @author Ranveer
	 */
	public void triggerEndEvent( ) {
		if ( twoPlayersToDeactivate ) {
			if ( !playerOneContact && !playerTwoContact ) {
				runEndAction( );
			}
		} else if ( twoPlayersToActivate ) {
			if ( beginTriggeredOnce == true )
				runEndAction( );
		} else {
			runEndAction( );
		}
	}
	
	public void triggerEndEvent( Player player ) {
		if ( twoPlayersToActivate ) {
			if ( playerOneContact && playerTwoContact ) {
				endAction.act( player );

			}
		} else {
			endAction.act( player );
		}
	}

	/**
	 * Add entity to Event Trigger
	 * 
	 * @param entity
	 *            - Entity
	 * @author Ranveer
	 */
	public void addEntityToTrigger( Entity entity ) {
		entityList.add( entity );
	}
	
	public void addEntitiesToTrigger( Entity... entities ) {
		for(Entity e:entities)
			addEntitiesToTrigger( e );
	}

	/**
	 * Add IAction as a Begin Action
	 * 
	 * @param action
	 *            - IAction
	 * @author Ranveer
	 */
	public EventTrigger setBeginIAction( IAction action ) {
		this.beginAction = action;
		return this;
	}

	/**
	 * Add IAction as a End Action
	 * 
	 * @param action
	 *            - IAction
	 * @author Ranveer
	 */
	public EventTrigger setEndIAction( IAction action ) {
		this.endAction = action;
		return this;
	}

	/**
	 * gets the beginning action couldn't run on any arbitrary entities if they
	 * need to be in a list used for removing entities
	 */
	public IAction getBeginAction( ) {
		return beginAction;
	}
	
	/**
	 * gets the ending action couldn't run on any arbitrary entities if they
	 * need to be in a list used for removing entities
	 */
	public IAction getEndAction( ) {
		return endAction;
	}

	/**
	 * calls act() with its begin action on every entity in this list depending
	 * on if its repeatable or not.
	 * 
	 * @author Ranveer - I didn't write the player stuff associated
	 * @param playerThatTriggeredMe
	 *            pass in null if you don't know what this is for.
	 */
	protected void runBeginAction( Player playerThatTriggeredMe ) {
		if ( !repeatable ) {
			if ( !beginTriggeredOnce ) {
				if ( actOnEntity ) {
					for ( Entity e : entityList ) {
						if ( beginAction != null ) {
							beginAction.act( e );
							beginTriggeredOnce = true;
							// endTriggeredOnce = false;
							// Gdx.app.log( this.name, " begin action " +
							// beginAction.getClass( ).getSimpleName( ) );
						}
					}
					if ( playerThatTriggeredMe != null )
						beginAction.act( playerThatTriggeredMe );
				} else {
					if ( beginAction != null ) {
						beginAction.act( );
						beginTriggeredOnce = true;
						// endTriggeredOnce = false;
						// Gdx.app.log( this.name, " begin action " +
						// beginAction.getClass( ).getSimpleName( ) );
						if ( playerThatTriggeredMe != null )
							beginAction.act( playerThatTriggeredMe );
					}
				}
			}
		} else if ( repeatable ) {
			if ( actOnEntity ) {
				for ( Entity e : entityList ) {
					if ( beginAction != null ) {
						beginAction.act( e );
						beginTriggeredOnce = true;
						endTriggeredOnce = false;
						// Gdx.app.log( this.name, " begin action " +
						// beginAction.getClass( ).getSimpleName( ));
					}
				}
				if ( playerThatTriggeredMe != null )
					beginAction.act( playerThatTriggeredMe );
			} else {
				if ( beginAction != null ) {
					beginAction.act( );
					beginTriggeredOnce = true;
					endTriggeredOnce = false;
					// Gdx.app.log( this.name, " begin action " +
					// beginAction.getClass( ).getSimpleName( ) );
					if ( playerThatTriggeredMe != null )
						beginAction.act( playerThatTriggeredMe );
				}
			}
		}
	}

	/**
	 * calls act() with its end action on every entity in this list depending on
	 * if its repeatable or not
	 * 
	 * @author Ranveer
	 */
	protected void runEndAction( ) {
		if ( !repeatable ) {
			if ( !endTriggeredOnce ) {
				if ( actOnEntity ) {
					for ( Entity e : entityList ) {
						if ( endAction != null ) {
							endAction.act( e );
							endTriggeredOnce = true;
							// beginTriggeredOnce = false;
							// Gdx.app.log( this.name, " end action " +
							// endAction.getClass( ).getSimpleName( ) );
						}
					}
				} else {
					if ( endAction != null ) {
						endAction.act( );
						endTriggeredOnce = true;
						// beginTriggeredOnce = false;
						// Gdx.app.log( this.name, " end action " +
						// endAction.getClass( ).getSimpleName( ) );
					}
				}
			}
		} else if ( repeatable ) {
			if ( actOnEntity ) {
				for ( Entity e : entityList ) {
					if ( endAction != null ) {
						endAction.act( e );
						endTriggeredOnce = true;
						beginTriggeredOnce = false;
						// Gdx.app.log( this.name, " end action " +
						// endAction.getClass( ).getSimpleName( ) );
					}
				}
			} else {
				if ( endAction != null ) {
					endAction.act( );
					endTriggeredOnce = true;
					beginTriggeredOnce = false;
					// Gdx.app.log( this.name, " end action " +
					// endAction.getClass( ).getSimpleName( ) );
				}
			}

		}
	}

	public void draw( SpriteBatch batch, float deltaTime, Camera camera ) {
		super.draw( batch, deltaTime, camera );
	}

}