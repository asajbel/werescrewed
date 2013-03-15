package com.blindtigergames.werescrewed.entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class RootSkeleton extends Skeleton {

	private ArrayList< Entity > looseEntity;
	private ArrayList< Skeleton > skeletonToSetActive, skeletonToSetInactive;
	private ArrayList<Skeleton> skeletonsToDelete;

	public RootSkeleton( String name, Vector2 positionPix, Texture tex,
			World world ) {
		super( name, positionPix, tex, world );
		entityType = EntityType.ROOTSKELETON;
		looseEntity = new ArrayList< Entity >( );
		skeletonToSetActive = new ArrayList< Skeleton >( );
		skeletonToSetInactive = new ArrayList< Skeleton >( );
		skeletonsToDelete = new ArrayList< Skeleton >( );
	}

	/**
	 * Delete a skeleton and all it's associated entities.
	 * @param skeleToDelete
	 */
	public void destroySkeleton(Skeleton skeleToDelete){
		skeletonsToDelete.add( skeleToDelete );
	}
	
	/**
	 * Never directly do skeleton.setSkeletonActive() because you may activate
	 * it in the middle of a world.step() which crashes box2d. Instead, use this
	 * method which will safely activate a skeleton.
	 * 
	 * @param skeletonToChangeState
	 *            skeleton in which to de/activate the skeleton and all it's
	 *            associated entities
	 * @param activeState
	 *            true/false, true to activate the body, false to deactivate it
	 */
	public void setSkeletonActiveState( Skeleton skeletonToChangeState,
			boolean activeState ) {
		if ( activeState ) {
			skeletonToSetActive.add( skeletonToChangeState );
		} else {
			skeletonToSetInactive.add( skeletonToChangeState );
		}
	}

	private void setSkeletonListActiveState( ArrayList< Skeleton > list,
			boolean setToThisActiveState ) {
		if ( list.size( ) > 0 ) {

			for ( Skeleton s : list ) {
				s.setSkeletonActive( setToThisActiveState );
			}
			list.clear( );
		}
	}
	
	private void deleteSkeletons(){
		if ( skeletonsToDelete.size( ) > 0 ){
			for ( Skeleton s : skeletonsToDelete ){
				Skeleton newParent = s.getParentSkeleton( );
				for ( Skeleton childsSkeleton : s.childSkeletonMap.values( ) ){
					//childsSkeleton.setParentSkeleton( newParent );
					newParent.addSkeleton( childsSkeleton );
				}
				newParent.childSkeletonMap.remove( s.name );
				s.dispose();
			}
		}
	}

	@Override
	public void update( float deltaTime ) {

		setSkeletonListActiveState( skeletonToSetActive, true );
		setSkeletonListActiveState( skeletonToSetInactive, false );
		deleteSkeletons( );

		// recursively update all skeleton movers
		updateChildSkeletonMovers( deltaTime );

		// update all children platform IMovers on their imover local coord
		// system
		updateEntityMovers( deltaTime );

		// recursively move all children skeletons by this moved updated
		// pos*rot.
		// setPosRotChildSkeletons( deltaTime );
		for ( Skeleton childSkeleton : childSkeletonMap.values( ) ) {
			// this could
			if ( childSkeleton.isActive( ) ) {
				childSkeleton.setPosRotFromSkeleton( deltaTime, this );
			}
			childSkeleton.setPosRotChildSkeletons( deltaTime );

		}

		// Now we can rotate all kinematic entities connected by updated
		// skeleton rot / position
		for ( Skeleton childSkeleton : childSkeletonMap.values( ) ) {

			if ( childSkeleton.fgSprite != null ) {
				childSkeleton.fgSprite.setPosition(
						childSkeleton.getPositionPixel( ).x,
						childSkeleton.getPositionPixel( ).y );
				childSkeleton.fgSprite.setRotation( MathUtils.radiansToDegrees
						* childSkeleton.body.getAngle( ) );
			}
			if ( childSkeleton.bgSprite != null ) {
				childSkeleton.bgSprite.setPosition(
						childSkeleton.getPositionPixel( ).x,
						childSkeleton.getPositionPixel( ).y );
				childSkeleton.bgSprite.setRotation( MathUtils.radiansToDegrees
						* childSkeleton.body.getAngle( ) );
			}
			childSkeleton.setPosRotAllKinematicPlatforms( deltaTime );
			// childSkeleton.update( deltaTime );
			childSkeleton.updateChildren( deltaTime );
		}

		// Update children animations and stuff
		// for ( Skeleton childSkeleton : childSkeletonMap.values( ) ){
		// childSkeleton.updateChildren( deltaTime );
		// }

		for ( Entity entity : looseEntity ) {
			entity.updateMover( deltaTime );
			entity.update( deltaTime );
		}
		// super.update( deltaTime );
	}

	/**
	 * adds a loose entity to this skeleton
	 * 
	 * @param e
	 */
	public void addLooseEntity( Entity e ) {
		looseEntity.add( e );
	}

	/**
	 * finds the entity with this name
	 */
	public Entity getSubEntity( String name ) {
		Entity entity = null;
		for ( Entity e : looseEntity ) {
			if ( e.name.equals( name ) ) {
				entity = e;
			}
		}
		return entity;
	}

	@Override
	public void draw( SpriteBatch batch ) {
		// possible bug: the draw order
		for ( Entity entity : looseEntity ) {
			entity.draw( batch );
		}
		super.draw( batch );
	}
}
