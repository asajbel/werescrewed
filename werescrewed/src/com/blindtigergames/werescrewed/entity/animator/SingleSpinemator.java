package com.blindtigergames.werescrewed.entity.animator;
/**
 * Animates an object with a single spine animation
 * 
 * @author Anders Sajbel
 */
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonBinary;
import com.esotericsoftware.spine.SkeletonData;

public class SingleSpinemator implements ISpinemator {

	protected Animation anim;
	protected Skeleton skel;
	protected Bone root;
	protected Vector2 position = null;
	protected Vector2 scale = null;
	protected float time = 0f;
	
	/**
	 * Constructor
	 * 
	 * @param type EntityDef containing an atlas, skeleton and intial animation
	 */
	public SingleSpinemator ( EntityDef type ) {
		TextureAtlas atlas = WereScrewedGame.manager.getAtlas( type
				.getAtlasName( ) );
		SkeletonBinary sb = new SkeletonBinary( atlas );
		SkeletonData sd = sb.readSkeletonData( Gdx.files
				.internal( "data/common/spine/" + type.getSkeleton( )
						+ ".skel" ) );
		anim = sd.findAnimation( type.getInitialAnimation( ) );
		skel = new com.esotericsoftware.spine.Skeleton( sd );
		skel.setToBindPose( );
		root = skel.getRootBone( );
		skel.updateWorldTransform( );
	}
	
	@Override
	public void draw( SpriteBatch b ) {
		skel.draw( b );
	}

	@Override
	public void update( float delta ) {
		time += delta;
		anim.apply( skel, time, true );
		if ( position != null ) {
			root.setX( position.x );
			root.setY( position.y );
		}
		if ( scale != null ) {
			root.setScaleX( scale.x );
			root.setScaleY( scale.y );
		} else {
			root.setScaleX( 1f );
			root.setScaleY( 1f );
		}
		skel.updateWorldTransform( );
		position = null;
	}

	@Override
	public void setPosition( Vector2 pos ) {
		position = pos;
	}

	@Override
	public void setScale( Vector2 scale ) {
		this.scale = scale;
	}

}
