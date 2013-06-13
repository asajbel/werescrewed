package com.blindtigergames.werescrewed.entity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;

public class RootSkeleton extends Skeleton {

	private ArrayList< Entity > looseEntity, entitiesToDelete;
	//private ArrayList< Skeleton > skeletonToSetActive, skeletonToSetInactive;
	private ArrayList< Skeleton > skeletonsToDelete;

	public RootSkeleton( String name, Vector2 positionPix, Texture tex,
			World world ) {
		super( name, positionPix, tex, world, BodyType.StaticBody );
		entityType = EntityType.ROOTSKELETON;
		looseEntity = new ArrayList< Entity >( );
		entitiesToDelete = new ArrayList< Entity >( );
		//skeletonToSetActive = new ArrayList< Skeleton >( );
		//skeletonToSetInactive = new ArrayList< Skeleton >( );
		skeletonsToDelete = new ArrayList< Skeleton >( );
		parentSkeleton = this;
		rootSkeleton = this;
		isMacroSkeleton = true;
	}

	/**
	 * Delete a skeleton and all it's associated entities.
	 * 
	 * @param skeleToDelete
	 */
	public void destroySkeleton( Skeleton skeleToDelete ) {
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
//	public void setSkeletonActiveState( Skeleton skeletonToChangeState,
//			boolean activeState ) {
//		if ( activeState ) {
//			skeletonToSetActive.add( skeletonToChangeState );
//		} else {
//			skeletonToSetInactive.add( skeletonToChangeState );
//		}
//	}
//
//	private void setSkeletonListActiveState( ArrayList< Skeleton > list,
//			boolean setToThisActiveState ) {
//		if ( list.size( ) > 0 ) {
//
//			for ( Skeleton s : list ) {
//				s.setSkeletonActive( setToThisActiveState );
//			}
//			list.clear( );
//		}
//	}

	private void deleteSkeletons( ) {
		if ( skeletonsToDelete.size( ) > 0 ) {
			for ( Skeleton s : skeletonsToDelete ) {
				Skeleton newParent = s.getParentSkeleton( );
				for ( Skeleton childsSkeleton : s.childSkeletonMap.values( ) ) {
					// childsSkeleton.setParentSkeleton( newParent );
					newParent.addSkeleton( childsSkeleton );
				}
				newParent.childSkeletonMap.remove( s.name );
				s.dispose( );
			}
		}
	}

	@Override
	public void update( float deltaTime ) {
		//setSkeletonListActiveState( skeletonToSetActive, true );
		//setSkeletonListActiveState( skeletonToSetInactive, false );
		deleteSkeletons( );
		super.update( deltaTime );
		for ( Entity entity : looseEntity ) {
			if ( entity.removeNextStep ) {
				entity.remove( );
				entitiesToDelete.add( entity );
			} else {
				entity.update( deltaTime );
				entity.updateMover( deltaTime );
			}
		}
		if ( entitiesToDelete.size( ) > 0 ) {
			for ( Entity entity : entitiesToDelete ) {
				looseEntity.remove( entity );
			}
			entitiesToDelete.clear( );
		}
	}

	/**
	 * adds a loose entity to this skeleton
	 * 
	 * @param e
	 */
	public void addLooseEntity( Entity e ) {
		looseEntity.add( e );
		e.setParentSkeleton( this );
	}

	/**
	 * finds the entity with this name
	 */
	public Entity getLooseEntity( String name ) {
		Entity entity = null;
		for ( Entity e : looseEntity ) {
			if ( e.name.equals( name ) ) {
				entity = e;
			}
		}
		return entity;
	}

	@Override
	public void draw( SpriteBatch batch, float deltaTime, Camera camera ) {
		// possible bug: the draw order
		for ( Entity entity : looseEntity ) {
			entity.draw( batch, deltaTime, camera );
		}
		super.draw( batch, deltaTime, camera );
	}

}
