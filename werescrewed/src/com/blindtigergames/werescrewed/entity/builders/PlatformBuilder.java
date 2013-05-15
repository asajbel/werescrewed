package com.blindtigergames.werescrewed.entity.builders;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.PolySprite;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.platforms.PlatformType;
import com.blindtigergames.werescrewed.entity.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.util.ArrayHash;

/**
 * PlatformBuilder should make building platforms a lot simpler and clearer Work
 * in progress
 * 
 * @author Ranveer, Stew
 * 
 */

// Later should be loaded in by file
public class PlatformBuilder extends GenericEntityBuilder< PlatformBuilder > {
	protected float width;
	protected float height;
	protected float thickX;
	protected float thickY;
	protected float scale;
	protected float density;
	protected float friction;
	protected float restitution;
	protected float gravScale;
	protected boolean flipHorizonal;
	protected boolean flipVertical;
	protected boolean isOneSided;
	protected boolean moveable;
	protected BodyType bodyType;
	protected String tileSet;
	protected Array< Vector2 > verts;

	/**
	 * 
	 * @param world
	 *            - Box2d world, only needs to be set once
	 * @return PlatformBuilder
	 */

	public PlatformBuilder( World world ) {
		super( );
		reset( );
		super.world( world );
	}

	/**
	 * 
	 * @param scale
	 *            - set scale of the whole platform, default is 1
	 * @return PlatformBuilder
	 */
	public PlatformBuilder setScale( float scale ) {
		this.scale = scale;
		return this;
	}

	/**
	 * 
	 * @param w
	 *            - set width with a float, default is 1
	 * @return PlatformBuilder
	 */
	public PlatformBuilder width( float w ) {
		this.width = w;
		return this;
	}

	/**
	 * 
	 * @param h
	 *            - set height with a float, default is 1
	 * @return PlatformBuilder
	 */
	public PlatformBuilder height( float h ) {
		this.height = h;
		return this;
	}

	/**
	 * 
	 * @param dimension
	 *            - set width/height with Vector2, default is (1,1)
	 * @return PlatformBuilder
	 */
	public PlatformBuilder dimensions( Vector2 dimension ) {
		return this.width( dimension.x ).height( dimension.y );
	}

	/**
	 * 
	 * @param width
	 *            - float width of platform
	 * @param height
	 *            - float height of platform
	 * @return PlatformBuilder
	 */
	public PlatformBuilder dimensions( float width, float height ) {
		return this.width( width ).height( height );
	}

	/**
	 * 
	 * @param density
	 *            - float used for density, default is 1.0f
	 * @return PlatformBuilder
	 */
	public PlatformBuilder density( float density ) {
		this.density = density;
		return this;
	}

	/**
	 * 
	 * @param friction
	 *            - float friction, default is 0.5f
	 * @return PlatformBuilder
	 */
	public PlatformBuilder friction( float friction ) {
		this.friction = friction;
		return this;
	}

	/**
	 * 
	 * @param restitution
	 *            - float restitution, default is 0.0f
	 * @return PlatformBuilder
	 */
	public PlatformBuilder restitution( float restitution ) {
		this.restitution = restitution;
		return this;
	}

	/**
	 * 
	 * @param gravscale
	 *            - float gravity scale, default is 0.1f
	 * @return PlatformBuilder
	 */
	public PlatformBuilder gravityScale( float gravscale ) {
		this.gravScale = gravscale;
		return this;
	}

	public PlatformBuilder flipHorizontal( boolean flipHori ) {
		this.flipHorizonal = flipHori;
		return this;
	}

	public PlatformBuilder flipVertical( boolean flipVert ) {
		this.flipVertical = flipVert;
		return this;
	}

	/**
	 * Set name of tileset you want
	 * 
	 * @param tileSetName
	 *            , don't set for default tileset
	 * @return
	 */
	public PlatformBuilder tileSet( String tileSetName ) {
		this.tileSet = tileSetName;
		return this;
	}

	/**
	 * 
	 * @param oneSide
	 *            - boolean set platform to oneside, default false
	 * @return PlatformBuilder
	 */
	public PlatformBuilder oneSided( boolean oneSide ) {
		this.isOneSided = oneSide;
		return this;
	}

	/**
	 * 
	 * @param moving
	 *            - boolean tells if platform could move, default false
	 * @return PlatformBuilder
	 */

	public PlatformBuilder moveable( boolean moving ) {
		this.moveable = moving;
		return this;
	}

	/**
	 * resets all the values to its default, use between builds
	 */
	@Override
	public PlatformBuilder reset( ) {
		super.resetInternal( );
		this.width = 1.0f;
		this.height = 1.0f;
		this.thickX = 1.0f;
		this.thickY = 1.0f;
		this.scale = 1.0f;
		this.density = 1.0f;
		this.friction = 1.0f;
		this.restitution = 0.0f;
		this.gravScale = 0.1f;
		this.flipHorizonal = false;
		this.flipVertical = false;
		this.isOneSided = false;
		this.moveable = false;
		this.tex = null;
		this.name = "No name";
		this.bodyType = BodyType.KinematicBody;
		this.tileSet = "TilesetTest";
		return this;
	}

	public PlatformBuilder dynamic( boolean d ) {
		if ( d ) {
			return this.dynamic( );
		}
		return this.kinematic( );
	}

	public PlatformBuilder dynamic( ) {
		bodyType = BodyType.DynamicBody;
		return this;
	}

	public PlatformBuilder staticBody( ) {
		bodyType = BodyType.StaticBody;
		return this;
	}

	public PlatformBuilder kinematic( ) {
		bodyType = BodyType.KinematicBody;
		return this;
	}

	@Override
	public PlatformBuilder properties( ArrayHash< String, String > props ) {
		super.properties( props );

		return this;
	}

	public PlatformBuilder setVerts( Array< Vector2 > verts ) {
		this.verts = verts;

		return this;
	}

	/**
	 * builds tile platform with specified numbers
	 * 
	 * @return TiledPlatform
	 */
	public TiledPlatform buildTilePlatform( ) {
		// TileSet ts = WereScrewedGame.manager.getTileSet( tileSet );

		// Tileset ts = ;

		TiledPlatform tp = new TiledPlatform( this.name, this.pos,
				WereScrewedGame.manager
						.getTileSet( ( isOneSided ) ? "common-textures"
								: tileSet ), this.width, this.height,
				this.isOneSided, this.moveable, this.world );

		tp.body.setType( bodyType );
		tp.setDensity( this.density );
		tp.setFriction( this.friction );
		tp.setRestitution( this.restitution );
		tp.setGravScale( this.gravScale );
		tp.body.setFixedRotation( false );
		prepareEntity( tp );
		return tp;

	}

	/**
	 * builds complex platform from available data.
	 * 
	 * @return ComplexPlatform
	 */
	public Platform buildComplexPlatform( ) {
		Platform cp = new Platform( this.name, this.type, this.world, this.pos,
				this.rot, new Vector2( this.scale, this.scale ) );

		cp.setPlatformType( PlatformType.COMPLEX );
		cp.body.setType( bodyType );
		cp.setDensity( this.density );
		cp.setFriction( this.friction );
		cp.setRestitution( this.restitution );
		cp.setGravScale( this.gravScale );
		cp.body.setFixedRotation( false );
		prepareEntity( cp );
		return cp;
	}

	public Platform buildCustomPlatform( ) {
		Platform customPlat = new Platform( this.name, this.pos, null,
				this.world );

		customPlat.constructBodyFromVerts( verts, this.pos );

		customPlat.setPlatformType( PlatformType.COMPLEX );
		customPlat.body.setType( bodyType );
		customPlat.setDensity( this.density );
		customPlat.setFriction( this.friction );
		customPlat.setRestitution( this.restitution );
		customPlat.setGravScale( this.gravScale );
		customPlat.body.setFixedRotation( false );

		if ( tex == null )// border texture
			tex = WereScrewedGame.manager
					.get( "data/levels/alphabot/sheetmetaltexture.png",
							Texture.class );
		PolySprite polySprite = new PolySprite( tex, verts );
		customPlat.changeSprite( polySprite );

		prepareEntity( customPlat );
		return customPlat;
	}

}