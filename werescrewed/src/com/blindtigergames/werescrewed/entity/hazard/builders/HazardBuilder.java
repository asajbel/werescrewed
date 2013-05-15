package com.blindtigergames.werescrewed.entity.hazard.builders;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.entity.builders.GenericEntityBuilder;
import com.blindtigergames.werescrewed.entity.hazard.Spikes;

public class HazardBuilder extends GenericEntityBuilder< HazardBuilder > {

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
	public HazardBuilder( World world ) {
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
	public HazardBuilder height( float h ) {
		this.height = h;
		return this;
	}

	/**
	 * 
	 * @param w
	 *            - set width with a float, default is 4
	 * @return SpikesBuilder
	 */
	public HazardBuilder width( float w ) {
		this.width = w;
		return this;
	}

	/**
	 * 
	 * @param dimension
	 *            - set width/height with Vector2, default is (4,1)
	 * @return SpikesBuilder
	 */
	public HazardBuilder dimensions( Vector2 dimension ) {
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
	public HazardBuilder dimensions( float width, float height ) {
		return this.width( width ).height( height );
	}

	public HazardBuilder active( ) {
		this.active = true;
		return this;
	}

	public HazardBuilder inactive( ) {
		this.active = false;
		return this;
	}

	public HazardBuilder up( ) {
		this.horizontal = true;
		this.inverted = false;
		return this;
	}

	public HazardBuilder down( ) {
		this.horizontal = true;
		this.inverted = true;
		return this;
	}

	public HazardBuilder left( ) {
		this.horizontal = false;
		this.inverted = true;
		return this;
	}

	public HazardBuilder right( ) {
		this.horizontal = false;
		this.inverted = false;
		return this;
	}

	@Override
	public HazardBuilder reset( ) {
		super.resetInternal( );
		this.width = 2;
		this.height = 1;
		this.horizontal = true;
		this.inverted = false;
		return this;
	}

	public Spikes buildSpikes( ) {
		Spikes spikes = new Spikes( this.name, this.pos, this.width,
				this.height, this.world, this.active, this.inverted,
				this.horizontal );
		return spikes;
	}
}
