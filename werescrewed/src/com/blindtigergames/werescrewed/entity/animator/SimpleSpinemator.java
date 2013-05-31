package com.blindtigergames.werescrewed.entity.animator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.esotericsoftware.spine.Animation;
import com.esotericsoftware.spine.Bone;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;

public class SimpleSpinemator implements ISpinemator {

	protected Animation anim;
	protected Skeleton skel;
	protected SkeletonRenderer skelDraw = new SkeletonRenderer( );
	protected Bone root;
	protected Vector2 position = null;
	protected Vector2 scale = null;
	protected float time = 0f;
	protected float mixTime = 0f;
	protected boolean flipX = false;
	protected boolean flipY = false;
	protected boolean loop = false;
	protected float mixRatio = 0f;
	protected SkeletonData sd;
	
	public SimpleSpinemator( SkeletonData data, String initialAnimationName, boolean loop  ) {
		sd = data; 
		anim = sd.findAnimation( initialAnimationName );
		skel = new com.esotericsoftware.spine.Skeleton( sd );
		skel.setToBindPose( );
		root = skel.getRootBone( );
		skel.updateWorldTransform( );
		this.loop = loop; 
	}

	public SimpleSpinemator( String atlasName, String skeletonName,
			String initialAnimationName, boolean loop ) {
		TextureAtlas atlas = WereScrewedGame.manager.getAtlas( atlasName );
		SkeletonJson sb = new SkeletonJson( atlas );
		sd = sb.readSkeletonData( Gdx.files.internal( "data/common/spine/"
				+ skeletonName + ".json" ) );
		anim = sd.findAnimation( initialAnimationName );
		skel = new com.esotericsoftware.spine.Skeleton( sd );
		skel.setToBindPose( );
		root = skel.getRootBone( );
		skel.updateWorldTransform( );
		this.loop = loop; 
	}

	@Override
	public void draw( SpriteBatch b ) {
		skelDraw.draw( b, skel );
	}

	@Override
	public void update( float delta ) {
		time += delta;
		mixTime += delta;
		mixRatio = mixTime / anim.getDuration( );
		anim.apply( skel, time, loop );
//		anim.mix( skel, time, loop, mixRatio );
		if ( mixTime >= anim.getDuration( ) / 2 ) {
			mixTime = anim.getDuration( );
		}
		if (position != null) {
			root.setX( position.x );
			root.setY( position.y ); 
		}
		skel.updateWorldTransform( );
		position = null; 
	}

	@Override
	public void setPosition( Vector2 pos ) {
		position = new Vector2 ( pos ); 
	}

	@Override
	public void setPosition( float x, float y ) {
		position = new Vector2(x, y);
		root.setX( x );
		root.setY( y );
	}

	@Override
	public void setScale( Vector2 scale ) {
		root.setScaleX( scale.x );
		root.setScaleY( scale.y );
	}

	@Override
	public TextureAtlas getBodyAtlas( ) {
		return null;
	}

	@Override
	public Vector2 getPosition( ) {
		float x = root.getWorldX( );
		float y = root.getWorldY( );

		return new Vector2( x, y );
	}

	@Override
	public float getX( ) {
		return root.getWorldX( );
	}

	@Override
	public float getY( ) {
		return root.getWorldY( );
	}

	@Override
	public void flipX( boolean flipX ) {
		skel.setFlipX( flipX );
	}

	@Override
	public void flipY( boolean flipY ) {
		skel.setFlipY( flipY );
	}

	@Override
	public void changeAnimation( String animName, boolean loop ) {
		anim = sd.findAnimation( animName );
		this.loop = loop;
		time = 0f;
		mixTime = 0f;
	}

	@Override
	public String getCurrentAnimation( ) {
		return anim.getName( );
	}

	@Override
	public float getAnimationDuration( ) {
		return anim.getDuration( ); 
	}

	@Override
	public void setRotation( float angle ) {
		root.setRotation( angle );
		
	}

	@Override
	public SkeletonData getSkeletonData( ) {
		return sd;
	}

}
