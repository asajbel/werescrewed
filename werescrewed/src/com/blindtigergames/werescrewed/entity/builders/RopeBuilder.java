package com.blindtigergames.werescrewed.entity.builders;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.rope.Rope;
import com.blindtigergames.werescrewed.util.Util;

public class RopeBuilder extends GenericEntityBuilder< RopeBuilder > {
	protected float linkWidth;
	protected float linkHeight;
	protected int links;
	protected boolean createScrew, createScrewSecondToLastLink,
			createScrewThirdToLastLink, moveToEntity, createScrewAll;
	protected Entity attachToTop;

	public RopeBuilder( World world ) {
		super( );
		reset( );
		super.world( world );
	}

	public RopeBuilder attachTo( Entity entity ) {
		this.pos.x = entity.getPosition( ).x * Util.BOX_TO_PIXEL;
		this.pos.y = entity.getPosition( ).y * Util.BOX_TO_PIXEL;
		return this;
	}

	public RopeBuilder width( float width ) {
		this.linkWidth = width;
		return this;
	}

	public RopeBuilder height( float height ) {
		this.linkHeight = height;
		return this;
	}

	public RopeBuilder links( int links ) {
		this.links = links;
		return this;
	}

	public RopeBuilder texture( Texture texture ) {
		this.tex = texture;
		return this;
	}

	public RopeBuilder createScrew( ) {
		createScrew = true;
		createScrewThirdToLastLink = false;
		return this;
	}

	public RopeBuilder createScrewThirdToLastLink( ) {
		createScrewThirdToLastLink = true;
		createScrewSecondToLastLink = false;
		createScrew = false;
		return this;
	}

	public RopeBuilder createScrewSecondToLastLink( ) {
		createScrewSecondToLastLink = true;
		createScrewThirdToLastLink = false;
		createScrew = false;
		return this;
	}

	public RopeBuilder createScrewAll( ) {
		createScrewAll = true;
		createScrew = false;
		createScrewThirdToLastLink = false;
		createScrewSecondToLastLink = false;
		return this;
	}

	public RopeBuilder moveToEntity( ) {
		moveToEntity = true;
		return this;
	}

	public RopeBuilder attachToTop( Entity entity ) {
		this.attachToTop = entity;
		return this;
	}

	public Rope buildRope( ) {
		Rope rope = new Rope( this.name, new Vector2( this.pos.x
				* Util.PIXEL_TO_BOX, this.pos.y * Util.PIXEL_TO_BOX ),
				new Vector2( linkWidth, linkHeight ), this.links, this.tex,
				this.world );
		if ( createScrew )
			rope.createScrew( );
		else if ( createScrewSecondToLastLink )
			rope.createScrewSecondToLastLink( );
		else if ( createScrewThirdToLastLink )
			rope.createScrewThirdToLastLink( );
		else if ( createScrewAll )
			rope.createScrewAll( );
		if ( this.attachToTop != null )
			rope.attachEntityToTop( attachToTop, moveToEntity );
		return rope;
	}

	public RopeBuilder reset( ) {
		super.resetInternal( );
		this.linkWidth = 16.0f;
		this.linkHeight = 64.0f;
		this.links = 5;
		this.createScrew = false;
		this.createScrewThirdToLastLink = false;
		this.createScrewSecondToLastLink = false;
		this.createScrewAll = false;
		this.attachToTop = null;
		this.moveToEntity = false;
		return this;
	}

}
