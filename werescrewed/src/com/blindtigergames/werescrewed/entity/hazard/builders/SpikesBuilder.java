package com.blindtigergames.werescrewed.entity.hazard.builders;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.builders.GenericEntityBuilder;
import com.blindtigergames.werescrewed.entity.hazard.Spikes;

public class SpikesBuilder extends GenericEntityBuilder< SpikesBuilder > {

	protected float width;
	protected float height;
	protected boolean horizontal;
	protected boolean inverted;
	protected boolean active;

	/**
	 * 
	 * @param world
	 *            - Box2d world, only needs to be set once
	 * @return SpikesBuilder
	 */
	public SpikesBuilder( World world ) {
		super( );
		reset( );
		super.world( world );
	}

	/**
	 * 
	 * @param h
	 *            - set height with a float, default is 1
	 * @return SpikesBuilder
	 */
	public SpikesBuilder height( float h ) {
		this.height = h;
		return this;
	}

	/**
	 * 
	 * @param w
	 *            - set width with a float, default is 4
	 * @return SpikesBuilder
	 */
	public SpikesBuilder width( float w ) {
		this.width = w;
		return this;
	}

	/**
	 * 
	 * @param dimension
	 *            - set width/height with Vector2, default is (4,1)
	 * @return SpikesBuilder
	 */
	public SpikesBuilder dimensions( Vector2 dimension ) {
		return this.width( dimension.x ).height( dimension.y );
	}

	/**
	 * 
	 * @param width
	 *            - float width of platform
	 * @param height
	 *            - float height of platform
	 * @return SpikesBuilder
	 */
	public SpikesBuilder dimensions( float width, float height ) {
		return this.width( width ).height( height );
	}

	public SpikesBuilder active( ) {
		this.active = true;
		return this;
	}

	public SpikesBuilder inactive( ) {
		this.active = false;
		return this;
	}

	public SpikesBuilder up( ) {
		this.horizontal = true;
		this.inverted = false;
		return this;
	}

	public SpikesBuilder down( ) {
		this.horizontal = true;
		this.inverted = true;
		return this;
	}

	public SpikesBuilder left( ) {
		this.horizontal = false;
		this.inverted = true;
		return this;
	}

	public SpikesBuilder right( ) {
		this.horizontal = false;
		this.inverted = false;
		return this;
	}

	@Override
	public SpikesBuilder reset( ) {
		super.resetInternal( );
		this.width = 2;
		this.height = 1;
		this.horizontal = true;
		this.inverted = false;
		return this;
	}

	public Spikes buildSpikes( ) {
		Spikes spikes = new Spikes( this.name, this.pos, this.height,
				this.width, this.world, this.active, this.inverted,
				this.horizontal );
		return spikes;
	}
}
