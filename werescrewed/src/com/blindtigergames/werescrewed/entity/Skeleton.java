package com.blindtigergames.werescrewed.entity;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.joint.RevoluteJointBuilder;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.rope.Rope;
import com.blindtigergames.werescrewed.screws.Screw;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.util.Util;

/**
 * A Skeleton is a node in the level tree structure. It moves platforms under it
 * as well as skeletons attached.
 * 
 * @author Stewart
 * 
 *         TODO: Perhaps change skeleton name, and make skeleton more like a
 *         tree (i.e. It should have a list of non-jointed entities too.)
 */

public class Skeleton extends Platform {

	public static final int foreground = 0;
	public static final int background = 1;
	public static final int midground = 2;

	public PolySprite bgSprite, fgSprite;

	protected HashMap< String, Platform > dynamicPlatformMap = new HashMap< String, Platform >( );
	protected HashMap< String, Skeleton > childSkeletonMap = new HashMap< String, Skeleton >( );
	protected HashMap< String, Platform > kinematicPlatformMap = new HashMap< String, Platform >( );
	protected HashMap< String, Rope > ropeMap = new HashMap< String, Rope >( );
	protected HashMap< String, Screw > screwMap = new HashMap< String, Screw >( );
	protected HashMap< String, EventTrigger > eventMap = new HashMap< String, EventTrigger >( );

	private int entityCount = 0;

	public Skeleton( String n, Vector2 pos, Texture tex, World world ) {
		super( n, pos, tex, world ); // not constructing body class
		this.world = world;
		constructSkeleton( pos );
		super.setSolid( false );
	}

	public void constructSkeleton( Vector2 pos ) {
		// Skeletons have no fixtures!!
		BodyDef skeletonBodyDef = new BodyDef( );
		skeletonBodyDef.type = BodyType.KinematicBody; // Kinematic so gravity
														// doesn't effect it
		skeletonBodyDef.position.set( pos.mul( Util.PIXEL_TO_BOX ) );
		body = world.createBody( skeletonBodyDef );
		body.setUserData( this );

		FixtureDef dynFixtureDef = new FixtureDef( );
		PolygonShape polygon = new PolygonShape( );
		polygon.setAsBox( 1 * Util.PIXEL_TO_BOX, 1 * Util.PIXEL_TO_BOX );
		dynFixtureDef.shape = polygon;
		dynFixtureDef.density = 100f;
		dynFixtureDef.filter.categoryBits = Util.CATEGORY_SUBPLATFORM;
		dynFixtureDef.filter.maskBits = Util.CATEGORY_NOTHING;
		body.createFixture( dynFixtureDef );
		polygon.dispose( );
	}

	/**
	 * Attach a platform to this skeleton that will freely rotate about the
	 * center. Make sure the platform is dynamic
	 * 
	 * @param platform
	 */
	public void addPlatformRotatingCenter( Platform platform ) {
		// Default values of the builder will allow rotation with anchor at
		// center of platform
		new RevoluteJointBuilder( world ).skeleton( this ).bodyB( platform )
				.build( );
		addDynamicPlatform( platform );

	}

	/**
	 * Attach a platform to this skeleton that rotates with a motor the platform
	 * must already be set as dynamic
	 * 
	 * @param platform
	 */
	public void addPlatformRotatingCenterWithMot( Platform platform,
			float rotSpeedInMeters ) {
		// Default values of the builder will allow rotation with anchor at
		// center of platform
		new RevoluteJointBuilder( world ).skeleton( this ).bodyB( platform )
				.motor( true ).motorSpeed( rotSpeedInMeters ).build( );

		addDynamicPlatform( platform );
	}

	/**
	 * Add a platform that will only move / rotate with skeleton Don't use this.
	 * if it's fixed, you might as well add it as kinematic
	 * 
	 * @param platform
	 */
	public void addDynamicPlatformFixed( Platform platform ) {
		new RevoluteJointBuilder( world ).skeleton( this ).bodyB( platform )
				.limit( true ).lower( 0 ).upper( 0 ).build( );
		addDynamicPlatform( platform );
	}

	/**
	 * Add a platform to this skeleton. Will determine what list to add it to
	 * for you!
	 * 
	 * @param platform
	 */
	public void addPlatform( Platform platform ) {
		if ( platform.body.getType( ) == BodyType.DynamicBody )
			addDynamicPlatform( platform );
		else
			addKinematicPlatform( platform );
	}

	public void addRope( Rope rope ) {
		new RevoluteJointBuilder( world ).skeleton( this )
				.bodyB( rope.getFirstLink( ) ).limit( true ).lower( 0 )
				.upper( 0 ).build( );
		// ropes.add( rope );
		ropeMap.put( rope.name, rope );
	}

	/**
	 * 
	 * @param ss
	 *            - add stripped screw onto the skeleton
	 */
	public void addStrippedScrew( StrippedScrew ss ) {
		addScrewForDraw( ss );
	}

	/**
	 * Add a screw to be drawn!
	 * 
	 * @param Screw
	 */
	public void addScrewForDraw( Screw s ) {
		// screws.add(s);
		entityCount++;
		screwMap.put( s.name + entityCount, s );
		//screwMap.add( s );
	}

	/**
	 * Simply adds a platform to the list, without explicitly attaching it to
	 * the skelington
	 * 
	 * @param Entity
	 *            platform
	 * @author stew
	 */
	public void addDynamicPlatform( Platform platform ) {
		entityCount++;
		// this.dynamicPlatforms.add( platform );
		if ( dynamicPlatformMap.containsKey( platform.name ) ) {
			platform.name = platform.name + "-CHANGE_MY_NAME" + entityCount;
		}
		dynamicPlatformMap.put( platform.name, platform );
	}

	/**
	 * Add Kinamatic platform to this Skeleton
	 * 
	 * @param Platform
	 *            that's already set as kinematic
	 */
	public void addKinematicPlatform( Platform platform ) {
		// kinematicPlatforms.add( platform );
		entityCount++;
		if ( kinematicPlatformMap.containsKey( platform.name ) ) {
			platform.name = platform.name + "-CHANGE_MY_NAME" + entityCount;
		}
		kinematicPlatformMap.put( platform.name, platform );
	}

	/**
	 * Add EventTrigger to this Skeleton
	 * 
	 * @param event
	 *            EventTrigger to be added to Skeleton
	 */
	public void addEventTrigger( EventTrigger event ) {
		entityCount++;
		if ( eventMap.containsKey( event.name ) ) {
			event.name = event.name + "-CHANGE_MY_NAME" + entityCount;
		}
		eventMap.put( event.name, event );
	}    
    
    /**
     * Add a skeleton to the sub skeleton list of this one.
     * @author stew
     */
    public void addSkeleton( Skeleton skeleton ) {
        //this.childSkeletons.add( skeleton );
    	childSkeletonMap.put( skeleton.name, skeleton );
    }
    
    /**
     * set skeleton to awake or not
     * TODO: Do kinamtic platforms need sleeping?
     */
    public void setSkeletonAwakeRec( boolean isAwake) {
		for ( Skeleton skeleton : childSkeletonMap.values( ) ){
			skeleton.setSkeletonAwakeRec( isAwake );
		}
		for ( Platform platform : dynamicPlatformMap.values( ) ) {
			platform.body.setAwake( isAwake );
		}
		for ( Platform platform : kinematicPlatformMap.values( ) ) {
			platform.body.setAwake( isAwake );
		}
		for ( Screw screw : screwMap.values( ) ) {
			screw.body.setAwake( isAwake );
		}

		// //TODO: add ropes to this function
		// Iterator< Map.Entry< String, Rope > > ropeIt = ropeMap.entrySet( )
		// .iterator( );
		// Map.Entry< String, Rope > ropeToUpdate;
		// while ( ropeIt.hasNext( ) ) {
		// ropeToUpdate = ropeIt.next( );
		// ropeToUpdate.getValue( ).body.setAwake( isAwake );
		// }
	}
    
    /**
     * setSkeletonActive() recursively sets all child skeletons active state to isActive\
     * @author stew
     */
    public void setSkeletonActiveRec( boolean isActive) {
    	setSkeletonActive(isActive);
		for ( Skeleton skeleton : childSkeletonMap.values( ) ){
			skeleton.setSkeletonActiveRec( isActive );
		}
	}

	/**
	 * Sets this skeleton & all associated entity's active state to isActive
	 * 
	 * @param isActive
	 * @author stew
	 */
	public void setSkeletonActive( boolean isActive ) {
		body.setActive( isActive );

		for ( Platform platform : dynamicPlatformMap.values( ) ) {
			platform.body.setActive( isActive );
		}
		for ( Platform platform : kinematicPlatformMap.values( ) ) {
			platform.body.setActive( isActive );
		}
		for ( Screw screw : screwMap.values( ) ) {
			screw.body.setActive( isActive );
		}
		/* TODO: add ropes */
	}

	/**
	 * translate the skeletons with specified values
	 * 
	 * @param x
	 *            - meters in X axis
	 * @param y
	 *            - meters in Y axis
	 */
	public void translateBy( float x, float y ) {
		body.setTransform( body.getPosition( ).x + x,
				body.getPosition( ).y + y, body.getAngle( ) );
		setSkeletonAwakeRec( true );
		// setSkeletonActive(true);
	}

	/**
	 * Rotate skeleton in radians
	 * 
	 * @author stew
	 */
	public void rotateBy( float angleRadians ) {
		/*
		 * body.setTransform( body.getTransform( ) .getPosition( ),
		 * body.getTransform( ).getRotation( )+angleRadians );
		 */
		body.setTransform( body.getPosition( ), body.getAngle( ) + angleRadians );
		setSkeletonAwakeRec( true );
	}

	/**
	 * This update function is ONLY called on the very root skeleton, it takes
	 * care of the child sksletons
	 * 
	 * @author stew
	 */
	@Override
	public void update( float deltaTime ) {
		// super.update( deltaTime );

		Vector2 bodyPos = body.getPosition( ).mul( Util.BOX_TO_PIXEL );

		if ( fgSprite != null ) {
			fgSprite.setPosition( bodyPos.x - offset.x, bodyPos.y - offset.y );
			fgSprite.setRotation( MathUtils.radiansToDegrees * body.getAngle( ) );
		}
		if ( bgSprite != null ) {
			bgSprite.setPosition( bodyPos.x - offset.x, bodyPos.y - offset.y );
			bgSprite.setRotation( MathUtils.radiansToDegrees * body.getAngle( ) );
		}

		// update root skeleton imover
		updateMover( deltaTime );
		// followed by children skeleton imovers

		updateChildSkeletonMovers( deltaTime );

		// update all children platform IMovers on their imover local coord
		// system
		updateEntityMovers( deltaTime );

		// recursively move all children skeletons by this moved updated
		// pos*rot.
		setPosRotChildSkeletons( deltaTime );

		// Now we can rotate all kinematic entities connected by updated
		// skeleton rot / position
		setPosRotAllKinematicPlatforms( deltaTime );

		// Update children animations and stuff
		updateChildren( deltaTime );
	}

	/**
	 * Update all sub skeleton movers recursively
	 * 
	 * @param deltaTime
	 * @author stew
	 */
	protected void updateChildSkeletonMovers( float deltaTime ) {
		for ( Skeleton skeleton : childSkeletonMap.values( ) ) {
			// Gdx.app.log( skeleton.name, childSkeletonMap.values( ).size( )+""
			// );
			// System.exit( 0 );
			skeleton.updateMover( deltaTime );
			skeleton.updateChildSkeletonMovers( deltaTime );
		}
	}

    
    /**
     * Update movers of all children platforms
     * @param deltaTime
     * @author stew
     */
    protected void updateEntityMovers( float deltaTime ){
    	for ( Skeleton skeleton : childSkeletonMap.values( ) ){
			skeleton.updateEntityMovers( deltaTime );
		}
		for ( Platform platform : dynamicPlatformMap.values( ) ) {
			platform.updateMover( deltaTime );
		}
		for ( Platform platform : kinematicPlatformMap.values( ) ) {
			platform.updateMover( deltaTime );
		}
		/* TODO: add ropes and loose entity */
	}

	/****
	 * Update all sub-skeleton and bones on this skeleton
	 * 
	 * @author stew
	 */
	protected void updateChildren( float deltaTime ) {
        // update sub skeleton and bones
    	//update( deltaTime );
        for ( Skeleton skeleton : childSkeletonMap.values( ) ) {
            skeleton.update( deltaTime );
        }
        for ( Platform p : dynamicPlatformMap.values( ) ) {
        	p.update( deltaTime );
        }
        for ( Platform p : kinematicPlatformMap.values( ) ) {
        	p.update( deltaTime );
        }    	
        //loop through screws update them
        //and then delete them if necessary
//        while ( screwMap.iterator( ).hasNext( ) ) {
//        	Screw s = screwMap.iterator( ).next( );
//    		s.update( deltaTime );
//    		if ( s.getRemoveNextStep( ) ) {
//    			screwMap.remove( s );
//    		}
//        }
		for ( Screw screw : screwMap.values( ) ) {
			screw.update( deltaTime );
		}
        for ( Rope rope : ropeMap.values() ){
        	rope.update( deltaTime);
        }
        
        for ( Rope rope : ropeMap.values() ){
        	rope.update( deltaTime);
        }
    }

	/**
	 * removes the bodies and joints of all the skeletons children
	 */
	@Override
	public void remove( ) {
		for ( Skeleton skeleton : childSkeletonMap.values( ) ) {
			skeleton.remove( );
		}
		for ( Platform p : dynamicPlatformMap.values( ) ) {
			p.remove( );
		}
		for ( Platform p : kinematicPlatformMap.values( ) ) {
			p.remove( );
		}
		for ( Screw screw : screwMap.values( ) ) {
			screw.remove( );
		}
		for ( JointEdge j : body.getJointList( ) ) {
			world.destroyJoint( j.joint );
		}
		world.destroyBody( body );
	}

	@Override
	public void draw( SpriteBatch batch ) {
		// super.draw( batch );
		if ( visible ) {
			// draw bg
			if ( bgSprite != null )
				bgSprite.draw( batch );
			drawChildren( batch );
			if ( fgSprite != null )
				fgSprite.draw( batch );
			// draw fg
		}
	}

	private void drawChildren( SpriteBatch batch ) {
		for ( Skeleton skeleton : childSkeletonMap.values( ) ) {
			skeleton.draw( batch );
		}
		for ( Platform p : dynamicPlatformMap.values( ) ) {
			drawPlatform( p, batch );
		}
		for ( Platform p : kinematicPlatformMap.values( ) ) {
			drawPlatform( p, batch );
		}
		for ( Screw screw : screwMap.values( ) ) {
			if ( !screw.getRemoveNextStep( ) ) {
				screw.draw( batch );
			}
		}
		for ( Rope rope : ropeMap.values( ) ) {
			rope.draw( batch );
		}
	}

	/**
	 * Draw each child. Tiled platforms have unique draw calls
	 */
	private void drawPlatform( Platform platform, SpriteBatch batch ) {
		switch ( platform.getPlatformType( ) ) {
		case TILED:
			( ( TiledPlatform ) platform ).draw( batch );
			break;
		default:
			platform.draw( batch );
		}
	}    
    
    /**
     * update child skeletons based on rotation & position of this skeleton
     * TODO: OPTIMIZATION only call this when the skeleton has moved / rotated
     */
    protected void setPosRotChildSkeletons( float deltaTime ) {
		for ( Skeleton skeleton : childSkeletonMap.values( ) ){
			if ( skeleton.isKinematic( ) )
				skeleton.setPosRotFromSkeleton( deltaTime, this );
			// now recursively apply this change to child skeletons
			skeleton.setPosRotChildSkeletons( deltaTime );
		}
	}

	/**
	 * @author stew
	 */
	protected void setPosRotAllKinematicPlatforms( float deltaTime ) {
		// first recursively set all kin platforms position
		for ( Skeleton skeleton : childSkeletonMap.values( ) ) {
			skeleton.setPosRotAllKinematicPlatforms( deltaTime );
		}
		// then set all kin platforms of this skeleton
		for ( Platform platform : kinematicPlatformMap.values( ) ) {
			platform.setPosRotFromSkeleton( deltaTime, this );
		}
	}

}
