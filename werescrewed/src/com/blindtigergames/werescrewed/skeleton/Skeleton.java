package com.blindtigergames.werescrewed.skeleton;

import java.util.ArrayList;

import javax.management.loading.PrivateClassLoader;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.joint.RevoluteJointBuilder;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.screws.Screw;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.util.Util;

/**
 * A Skeleton is a node in the level tree structure. It moves platforms under it
 * as well as skeletons attached.
 * 
 * @author Stewart
 * 
 *         TODO: Perhaps change skeleton name, make skeleton more like a tree It
 *         should have a list of non-jointed entities too.
 */

public class Skeleton extends Entity {

    private ArrayList<Skeleton> childSkeletons;
    private ArrayList<Platform> dynamicPlatforms;
    private ArrayList<Platform> kinematicPlatforms;
    private ArrayList<Entity>   looseEntity; 
    private Texture foregroundTex;
    private ArrayList< Screw > screws; //add all screws you want drawn

    // private Skeleton(){};

    public Skeleton( String n, Vector2 pos, Texture tex, World world ) {
        super( n, pos, tex, null, false); // not constructing body class
        this.world = world;
        constructSkeleton( pos );
        this.dynamicPlatforms = new ArrayList<Platform>();
        childSkeletons = new ArrayList<Skeleton>();
        kinematicPlatforms = new ArrayList< Platform >( );
        looseEntity = new ArrayList< Entity >();
        screws = new ArrayList<Screw>();
    }

    public void constructSkeleton( Vector2 pos ) {
        // Skeletons have no fixtures!!
        BodyDef skeletonBodyDef = new BodyDef();
        skeletonBodyDef.type = BodyType.KinematicBody; // Kinematic so gravity
                                                    // doesn't effect it
        skeletonBodyDef.position.set( pos.mul( Util.PIXEL_TO_BOX ) );
        body = world.createBody( skeletonBodyDef );
        body.setUserData( this );
    }

    /**
     * Attach a platform to this skeleton that will freely rotate about the
     * center
     * 
     * @param platform
     */
    public void addPlatformRotatingCenter( Platform platform ) {
        // Default values of the builder will allow rotation with anchor at
        // center of platform
        new RevoluteJointBuilder( world ).skeleton( this ).bodyB( platform )
                .build();
        
        addDynamicPlatform( platform );
    }
    
    /**
     * Add Kinamatic platform to this Skeleton
     */
    public void addKinematicPlatform( Platform platform ){
    	kinematicPlatforms.add( platform );
    }
    
    /**
     * Attach a platform to this skeleton that rotates with a motor
     * 
     * @param platform
     */
    public void addPlatformRotatingCenterWithMot( Platform platform, float rotSpeedInMeters ) {
        // Default values of the builder will allow rotation with anchor at
        // center of platform
        new RevoluteJointBuilder( world )
        						.skeleton( this )
        						.bodyB( platform )
        						.motor( true )
        						.motorSpeed( rotSpeedInMeters )
        						.build();
        
        addDynamicPlatform( platform );
    }

    /**
     * Add a platform that will only move / rotate with skeleton
     * Don't use this. if it's fixed, you might as well add it as kinematic
     * @param platform
     */
    public void addDynamicPlatformFixed( Platform platform ) {
        new RevoluteJointBuilder( world ).skeleton( this ).bodyB( platform )
                .limit( true ).lower( 0 ).upper( 0 ).build();
        addDynamicPlatform( platform );
    }
    
    /**
     * 
     * @param platform - add platform that has structure screws already
     */
     public void addPlatform( Platform platform ){
    	 if ( platform.body.getType( ) == BodyType.DynamicBody )
    		 addDynamicPlatform( platform );
    	 else 
    		 addKinematicPlatform( platform );
     }
     
     /**
      * 
      * @param ss -  add stripped screw onto the skeleton
      */
     public void addStrippedScrew ( StrippedScrew ss ){
        new RevoluteJointBuilder( world ).skeleton( this ).bodyB( ss )
                 .limit( true ).lower( 0 ).upper( 0 ).build();
     	//addDynamicPlatform( ss );
        screws.add( ss );
    }
     
     /**
      * Add a screw to be drawn!
      * @param Screw
      */
     public void addScrewForDraw(Screw s){
    	 screws.add(s);
     }

     /**
      * Simply adds a platform to the list, without explicitly
      * attaching it to the skelington
      * @param Entity platform
      * @author stew
      */
    public void addDynamicPlatform( Platform platform ) {
        this.dynamicPlatforms.add( platform );

    }

    /**
     * Add a skeleton to the sub skeleton list of this one.
     * @author stew
     */
    public void addSkeleton( Skeleton skeleton ) {
        this.childSkeletons.add( skeleton );
    }

    /**
     * set skeleton to awake or not
     * TODO: Do kinamtic platforms need sleeping?
     */
    public void setSkeletonAwake( boolean isAwake) {
        //body.setActive( true );
        for ( Skeleton skeleton : childSkeletons ) {
            skeleton.setSkeletonAwake(isAwake);
        }
        for ( Platform p : dynamicPlatforms ) {
            p.body.setAwake( isAwake );
        }
        for( Entity e: looseEntity ){
        	e.body.setAwake( isAwake );
        }
        for( Platform platform : kinematicPlatforms ){
        	platform.body.setAwake( isAwake );
        }
    }
    
    /**
     * setSkeletonActive() recursively sets all child skeletons active state to isActive\
     * @author stew
     */
    public void setSkeletonActiveRec( boolean isActive) {
        for ( Skeleton skeleton : childSkeletons ) {
            skeleton.setSkeletonActiveRec(isActive);
        }
        setSkeletonActive(isActive);
    }
    
    /**
     * Sets this skeleton & all associated entity's active state to isActive
     * @param isActive
     * @author stew
     */
    public void setSkeletonActive( boolean isActive ){
    	body.setActive( isActive );
    	for ( Platform p : dynamicPlatforms ) {
            p.body.setActive( isActive );
        }
        for( Entity e: looseEntity ){
        	e.body.setActive( isActive );
        }
        for( Platform platform : kinematicPlatforms ){
        	platform.body.setActive( isActive );
        }
    }
    
    /**
     * translate the skeletons with specified values
     * @param x - float in X axis
     * @param y - float in Y axis
     */
    public void translate( float x, float y ){
    	body.setTransform(body.getPosition().x+x, body.getPosition().y+y, body.getAngle());
    	setSkeletonAwake( true );
    	setSkeletonActive(true);
    }
    
    /**
     * Rotate skeleton in radians
     * @author stew
     */
    public void rotate( float angleRadians ){
    	/*body.setTransform( body.getTransform( ) .getPosition( ),
    			body.getTransform( ).getRotation( )+angleRadians );*/
    	body.setTransform( body.getPosition(), body.getAngle( )+angleRadians );
    	setSkeletonAwake( true );
    }

    /**
     * This update function is ONLY called on the very root skeleton, it takes care
     * of the child sksletons
     * @author stew
     */
    @Override
    public void update( float deltaTime ) {
    	//update root skeleton imover
        updateMover( deltaTime );
        //followed by children skeleton imovers
        updateChildSkeletonMovers( deltaTime );
        //update all children platform IMovers on their imover local coord system
        updateEntityMovers( deltaTime );
        
        //recursively move all children skeletons by this moved updated pos*rot.
        setPosRotChildSkeletons( );
        
        //Now we can rotate all kinematic entities connected by updated skeleton rot / position
        setPosRotAllKinematicPlatforms();
        
        //Update children animations and stuff
        updateChildren( deltaTime );
    }
    
    /**
     * Update all sub skeleton movers recursively
     * @param deltaTime
     * @author stew
     */
    protected void updateChildSkeletonMovers( float deltaTime ){
    	for ( Skeleton skeleton : childSkeletons ){
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
    	for ( Skeleton skeleton : childSkeletons ) {
            skeleton.updateEntityMovers( deltaTime );
        }
    	for ( Platform p : dynamicPlatforms ) {
    		p.updateMover( deltaTime );
        }
    	//update kinamatic platforms on their local imover coordinate system
    	for ( Platform p : kinematicPlatforms ) {
    		p.updateMover( deltaTime );
        }
    }

    /****
     * Update all sub-skeleton and bones on this skeleton
     * @author stew
     */
    private void updateChildren( float deltaTime ) {
        // update sub skeleton and bones
        for ( Skeleton skeleton : childSkeletons ) {
            skeleton.update( deltaTime );
        }
        for ( Platform p : dynamicPlatforms ) {
        	updatePlatform(p,deltaTime);
        }
        for ( Platform p : kinematicPlatforms ) {
        	updatePlatform(p,deltaTime);
        }
    }
    
    /**
     * Update a single platform, casting if necessary
     */
    private void updatePlatform( Platform platform, float deltaTime ){
    	switch (platform.getPlatformType( )){
    	case TILED:
    		((TiledPlatform)platform).update( deltaTime );
    		break;
    	default:
    		platform.update( deltaTime );
    	}
    }
    
    @Override
    public void draw( SpriteBatch batch ){
    	super.draw( batch );
    	
    	drawChildren( batch );
    }
    
    private void drawChildren( SpriteBatch batch ){
        for ( Skeleton skeleton : childSkeletons ) {
            skeleton.draw( batch );
        }
        for (  Platform p : dynamicPlatforms ) {
        	drawPlatform(p,batch);
        }
        for (  Platform p : kinematicPlatforms ) {
        	drawPlatform(p,batch);
        }
        for ( Screw screw : screws ){
        	screw.draw( batch );
        }
    }
    
    /**
     * Draw each child. Tiled platforms have unique draw calls
     */
    private void drawPlatform( Platform platform, SpriteBatch batch ){
    	switch (platform.getPlatformType( )){
    	case TILED:
    		((TiledPlatform)platform).draw( batch );
    		break;
    	default:
    		platform.draw( batch );
    	}
    }
    
    
    /**
     * update child skeletons based on rotation & position of this skeleton
     * TODO: OPTIMIZATION only call this when the skeleton has moved / rotated
     */
    private void setPosRotChildSkeletons( ) {
		for ( Skeleton skeleton : childSkeletons ){
			skeleton.body.setTransform( Util.PointOnCircle(
							this.body.getPosition( ).dst( skeleton.body.getPosition( ) ), //radius
							this.body.getAngle( ), this.body.getWorldCenter( ) ), //angle on circle, origin
					this.body.getAngle( ) ); //set angle of child skeleton
			//now recursively apply this change to child skeletons
			skeleton.setPosRotChildSkeletons( );
		}
	}
    
    /**
     * @author stew
     */
    private void setPosRotAllKinematicPlatforms(){
    	//first recursively set all kin platforms position
    	for ( Skeleton skeleton : childSkeletons ){
    		skeleton.setPosRotAllKinematicPlatforms( );
    	}
    	//then set all kin platforms of this skeleton
    	for ( Platform platform : kinematicPlatforms ){
    		platform.setPosRotFromSkeleton( this );
    	}
    }
    
}
