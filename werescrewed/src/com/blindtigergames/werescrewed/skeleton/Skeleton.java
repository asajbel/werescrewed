package com.blindtigergames.werescrewed.skeleton;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.joint.RevoluteJointBuilder;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.screws.Screw;
import com.blindtigergames.werescrewed.screws.StrippedScrew;
import com.blindtigergames.werescrewed.util.Util;

/**
 * Bone is an entity which is placed onto the Skeleton then when the Skeleton
 * moves, the Bones should follow
 * 
 * @author Ranveer/Stewart
 * 
 *         TODO: Perhaps change skeleton name, make skeleton more like a tree It
 *         should have a list of non-jointed entities too.
 */

public class Skeleton extends Entity {

    private ArrayList<Skeleton> childSkeletons;
    private ArrayList<Platform> dynamicPlatforms;
    private ArrayList<Platform> kinematicPlatforms;
    private Texture foregroundTex;
    private ArrayList< Screw > screws;

    // private Skeleton(){};

    public Skeleton( String n, Vector2 pos, Texture tex, World world ) {
        super( n, pos, tex, null, false); // not constructing body class
        this.world = world;
        this.dynamicPlatforms = new ArrayList<Platform>();
        constructSkeleton( pos );
        childSkeletons = new ArrayList<Skeleton>();
        
        kinematicPlatforms = new ArrayList< Platform >( );
    }

    public void constructSkeleton( Vector2 pos ) {
        // Skeletons have no fixtures!!
        BodyDef skeletonBodyDef = new BodyDef();
        skeletonBodyDef.type = BodyType.StaticBody; // Kinematic so gravity
                                                    // doesn't effect it
        skeletonBodyDef.position.set( pos.mul( Util.PIXEL_TO_BOX ) );
        body = world.createBody( skeletonBodyDef );
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
        RevoluteJoint joint = new RevoluteJointBuilder( world ).skeleton( this ).bodyB( platform )
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
        RevoluteJoint joint = new RevoluteJointBuilder( world )
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
        RevoluteJoint joint = new RevoluteJointBuilder( world ).skeleton( this ).bodyB( platform )
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
        RevoluteJoint joint = new RevoluteJointBuilder( world ).skeleton( this ).bodyB( ss )
                 .limit( true ).lower( 0 ).upper( 0 ).build();
     	//addDynamicPlatform( ss );
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
     * Sleep dynamic platforms on this skeleton. 
     * TODO: Do kinamtic platforms need sleeping?
     */
    public void sleepSkeleton() {
        body.setActive( false );
        for ( Skeleton skeleton : childSkeletons ) {
            skeleton.sleepSkeleton();
        }
        for ( Entity e : dynamicPlatforms ) {
            // boneJoint.bone.body.setActive(false);
            e.body.setAwake( false );
        }
    }

    /**
     * Wake dynamic platofrms on skeleton.
     * TODO: Do kinematic platoforms need sleeping?
     * @author stew
     */
    public void wakeSkeleton() {
        body.setActive( true );
        for ( Skeleton skeleton : childSkeletons ) {
            skeleton.wakeSkeleton();
        }
        for ( Platform p : dynamicPlatforms ) {
           p.body.setAwake( true );
        }
    }
    
    /**
     * translate the skeletons with specified values
     * @param x - float in X axis
     * @param y - float in Y axis
     */
    public void translate( float x, float y ){
    	body.setTransform( body.getTransform( )
				.getPosition( ).add( x, y ), body
				.getTransform( ).getRotation( ) );
		wakeSkeleton( );
    }
    
    /**
     * Rotate skeleton in radians
     * @author stew
     */
    public void rotate( float angleRadians ){
    	body.setTransform( body.getTransform( ) .getPosition( ),
    			body.getTransform( ).getRotation( )+angleRadians );
		wakeSkeleton( );
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
            p.update( deltaTime );
        }
        for ( Platform p : kinematicPlatforms ) {
    		p.update( deltaTime );
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
            p.draw( batch );
        }
        for (  Platform p : kinematicPlatforms ) {
            p.draw( batch );
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
    
    //Deprecated subclass for storing joints of platforms
    /*protected class BoneAndJoints {
        protected ArrayList<Joint> joints;
        protected Entity bone;

        protected BoneAndJoints( Entity bone, ArrayList<Joint> joints ) {
            this.bone = bone;
            this.joints = joints;
        }

        protected BoneAndJoints( Entity bone, Joint joint ) {
            this.bone = bone;
            joints = new ArrayList<Joint>();
            this.joints.add( joint );
        }
    }*/
