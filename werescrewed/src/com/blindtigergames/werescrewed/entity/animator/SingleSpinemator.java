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
import com.esotericsoftware.spine.SkeletonRenderer;

public class SingleSpinemator implements ISpinemator {

	protected Animation anim;
	protected Skeleton skel;
	protected SkeletonRenderer skelDraw = new SkeletonRenderer( );
	protected Bone root;
	protected Vector2 position = null;
	protected Vector2 scale = null;
	protected float time = 0f;

	/**
	 * Constructor
	 * 
	 * @param type
	 *            EntityDef containing an atlas, skeleton and intial animation
	 */
	public SingleSpinemator( EntityDef type ) {
		this( type.getAtlasName( ), type.getSkeleton( ), type
				.getInitialAnimation( ) );
	}

	/**
	 * Constructor
	 * 
	 * @param atlasName
	 *            Name of the atlas for the animation
	 * @param skeletonName
	 *            Name of the skeleton containing the animation
	 * @param animationName
	 *            Name of the animation
	 */
	public SingleSpinemator( String atlasName, String skeletonName,
			String animationName ) {
		TextureAtlas atlas = WereScrewedGame.manager.getAtlas( atlasName );
		SkeletonBinary sb = new SkeletonBinary( atlas );
		SkeletonData sd = sb.readSkeletonData( Gdx.files
				.internal( "data/common/spine/" + skeletonName + ".skel" ) );
		anim = sd.findAnimation( animationName );
		skel = new com.esotericsoftware.spine.Skeleton( sd );
		skel.setToBindPose( );
		root = skel.getRootBone( );
		skel.updateWorldTransform( );
	}

	@Override
	public void draw( SpriteBatch b ) {
		skelDraw.draw( b, skel );
	}

	@Override
	public void update( float delta ) {
		time += delta;
		anim.apply( skel, time, true );
		skel.updateWorldTransform( );
	}

	@Override
	public void setPosition( Vector2 pos ) {
		root.setX( pos.x );
		root.setY( pos.y );
	}

	@Override
	public void setScale( Vector2 scale ) {
		root.setScaleX( scale.x );
		root.setScaleY( scale.y );
	}

	@Override
	public TextureAtlas getBodyAtlas( ) {
		// TODO Auto-generated method stub
		return null;
	}

}
