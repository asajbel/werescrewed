package com.blindtigergames.werescrewed.entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.joint.RevoluteJointBuilder;
import com.blindtigergames.werescrewed.platforms.Platform;
import com.blindtigergames.werescrewed.screens.GameScreen;
import com.blindtigergames.werescrewed.screws.StrippedScrew;

/**
 * Bone is an entity which is placed onto the Skeleton then when the Skeleton
 * moves, the Bones should follow
 * 
 * @author Ranveer/Stewart
 * 
 * TODO: Perhaps change skeleton name, make skeleton more like a tree
 * It should have a list of non-jointed entities too.
 */

public class Skeleton extends Entity {

    ArrayList<Skeleton> subSkeletons;
    public ArrayList<BoneAndJoints> boneAndJoints;

    // private Skeleton(){};

    public Skeleton( String n, Vector2 pos, Texture tex, World world ) {
        super( n, pos, tex, null, false); // not constructing body class
        this.world = world;
        this.boneAndJoints = new ArrayList<BoneAndJoints>();
        constructSkeleton( pos );
        subSkeletons = new ArrayList<Skeleton>();
    }

    public void constructSkeleton( Vector2 pos ) {
        // Skeletons have no fixtures!!
        BodyDef skeletonBodyDef = new BodyDef();
        skeletonBodyDef.type = BodyType.StaticBody; // Kinematic so gravity
                                                    // doesn't effect it
        skeletonBodyDef.position.set( pos.mul( GameScreen.PIXEL_TO_BOX ) );
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
        
        addBoneAndJoint( platform, joint );
    }
    
    /**
     * Attach a platform to this skeleton that rotates with a motor
     * 
     * @param platform
     */
    public void addPlatformRotatingCenterWithRot( Platform platform, float rotSpeedInMeters ) {
        // Default values of the builder will allow rotation with anchor at
        // center of platform
        RevoluteJoint joint = new RevoluteJointBuilder( world )
        						.skeleton( this )
        						.bodyB( platform )
        						.motor( true )
        						.motorSpeed( rotSpeedInMeters )
        						.build();
        
        addBoneAndJoint( platform, joint );
    }

    /**
     * Add a platform that will only move / rotate with skeleton
     * 
     * @param platform
     */
    public void addPlatformFixed( Platform platform ) {
        RevoluteJoint joint = new RevoluteJointBuilder( world ).skeleton( this ).bodyB( platform )
                .limit( true ).lower( 0 ).upper( 0 ).build();
        addBoneAndJoint( platform, joint );
    }
    
    
     public void addPlatform( Platform platform ){
     	addBoneAndJoint( platform, null );
     }
     
     public void addStrippedScrew ( StrippedScrew ss ){
        RevoluteJoint joint = new RevoluteJointBuilder( world ).skeleton( this ).bodyB( ss )
                 .limit( true ).lower( 0 ).upper( 0 ).build();
     	addBoneAndJoint( ss, joint );
    }


    public void addBoneAndJoint( Entity bone, Joint joint ) {
        this.boneAndJoints.add( new BoneAndJoints( bone, joint ) );

    }

    public void addBoneAndJointList( Entity bone, ArrayList<Joint> joints ) {
        this.boneAndJoints.add( new BoneAndJoints( bone, joints ) );
    }

    /**
     * Add a skeleton to the sub skeleton list of this one.
     */
    public void addSkeleton( Skeleton skeleton ) {
        this.subSkeletons.add( skeleton );
    }

    public void sleepSkeleton() {
        body.setActive( false );
        for ( Skeleton skeleton : subSkeletons ) {
            skeleton.sleepSkeleton();
        }
        for ( BoneAndJoints boneJoint : boneAndJoints ) {
            // boneJoint.bone.body.setActive(false);
            boneJoint.bone.body.setAwake( false );
        }
    }

    public void wakeSkeleton() {
        body.setActive( true );
        for ( Skeleton skeleton : subSkeletons ) {
            skeleton.wakeSkeleton();
        }
        for ( BoneAndJoints boneJoint : boneAndJoints ) {
           boneJoint.bone.setAwake( );
        }
    }
    
    public void translate( float x, float y ){
    	body.setTransform( body.getTransform( )
				.getPosition( ).add( x, y ), body
				.getTransform( ).getRotation( ) );
		wakeSkeleton( );
		
		for( Skeleton s: subSkeletons )
			s.translate( x, y );
    }

    @Override
    public void update( float deltaTime ) {
        if ( mover != null ) {
            mover.move( deltaTime, body );
        }
        updateChildren( deltaTime );
    }

    /****
     * Update all sub-skeleton and bones on this skeleton
     */
    private void updateChildren( float deltaTime ) {
        // update sub skeleton and bones
        for ( Skeleton skeleton : subSkeletons ) {
            skeleton.update( deltaTime );
        }
        for ( BoneAndJoints boneJoint : boneAndJoints ) {
            boneJoint.bone.update( deltaTime );
        }
    }
    
    @Override
    public void draw( SpriteBatch batch ){
    	super.draw( batch );
    	drawChildren( batch );
    }
    
    private void drawChildren( SpriteBatch batch ){
        for ( Skeleton skeleton : subSkeletons ) {
            skeleton.draw( batch );
        }
        for ( BoneAndJoints boneJoint : boneAndJoints ) {
            boneJoint.bone.draw( batch );
        }
    }
    protected class BoneAndJoints {
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
    }
}