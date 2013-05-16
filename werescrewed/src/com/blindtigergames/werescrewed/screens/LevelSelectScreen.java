package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Falling;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.animator.SingleSpinemator;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.gui.TextButton;

public class LevelSelectScreen extends Screen {

	public ScreenType screenType;
	// private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont = null;
	private Sprite menuBG = null;
	private Sprite transition = null;
	private Label screenLabel = null;
	private TextButton resurrectButton = null;
	private TextButton hazardButton = null;
	private TextButton physicsButton = null;
	private int lineHeight = 0;
	private TextButton level1Button;
	private TextButton backButton = null;
	private TextButton dragonButton = null;
	private SingleSpinemator man = null;
	private SingleSpinemator lady = null;
	private Array< Falling > debris = null;
	private Array< Falling > gears = null;
	private int width, height;
	private float time;
	private float manDir = 1;
	private float ladyDir = -1;

	public LevelSelectScreen( ) {
		super( );
		font = new BitmapFont( );
		fancyFont = WereScrewedGame.manager.getFont( "longdon" );
		// fancyFont = WereScrewedGame.manager.getFont( "Screwball" );
		// fancyFont = WereScrewedGame.manager.getFont( "ornatique" );
		Texture back = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/menu/menu.png", Texture.class );
		Texture trans = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/menu/transition.png", Texture.class );
		menuBG = new Sprite( back );
		transition = new Sprite( trans );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) + 40 );
		screenLabel = new Label( "Level Select", fancyFont );
		man = new SingleSpinemator( "red_male_atlas", "male", "fall_idle" );
		lady = new SingleSpinemator( "red_female_atlas", "female", "fall_idle" );
		gears = new Array< Falling >( );
		debris = new Array< Falling >( );
		TextureAtlas gearsAtlas = WereScrewedGame.manager.getAtlas( "gears" );
		TextureAtlas common = WereScrewedGame.manager
				.getAtlas( "common-textures" );
		for ( int i = 0; i < 5; i++ )
			createDebris( gearsAtlas, common );
		loadButtons( );
	}

	@Override
	public void dispose( ) {
	}

	@Override
	public void hide( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render( float delta ) {
		super.render( delta );

		Gdx.gl.glClearColor( 0.4f, 0.6f, 1.0f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );

		moveCharacters( delta );
		updateDebris( );
		batch.begin( );for ( Falling g : gears ) {
			g.sprite.draw( batch );
		}
		for ( Falling d : debris ) {
			d.sprite.draw( batch );
		}
		menuBG.draw( batch );
		// screenLabel.draw( batch );
		physicsButton.draw( batch, camera );
		resurrectButton.draw( batch, camera );
		hazardButton.draw( batch, camera );
		level1Button.draw( batch, camera );
		dragonButton.draw( batch, camera );

		backButton.draw( batch, camera );

		if ( !finish )
			setAlpha( -0.02f );
		man.draw( batch );
		lady.draw( batch );
		transition.draw( batch, alpha );
		batch.end( );

	}

	@Override
	public void resize( int width, int height ) {
		this.width = width;
		this.height = height;

		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int leftX = ( int ) menuBG.getWidth( ) / 2;// / 5 - 20;
		int centerY = height / 2;
		@SuppressWarnings( "unused" )
		float scaleX = width / 1280f;
		@SuppressWarnings( "unused" )
		float scaleY = height / 720f;

		transition.setPosition( width/2 - transition.getWidth( )/2, height/2 - transition.getHeight( )/2 );
		transition.setScale( width/transition.getWidth( ), height/transition.getHeight( ) );
		// menuBG.setScale( width / menuBG.getWidth( ), width / menuBG.getWidth(
		// ) );
		menuBG.setPosition( 0, height / 2 - menuBG.getHeight( ) / 2 );
		// menuBG.setPosition( width / 2 - menuBG.getWidth( ) / 2, height / 2 -
		// menuBG.getHeight( ) / 2 );
		screenLabel.setX( leftX - screenLabel.getWidth( ) / 2 );
		screenLabel.setY( centerY + 3 * lineHeight );

		// quick fix
		physicsButton.setX( leftX - physicsButton.getWidth( ) / 2 );
		physicsButton.setY( centerY + lineHeight * 3 );
		resurrectButton.setX( leftX - resurrectButton.getWidth( ) / 2 );
		resurrectButton.setY( centerY + lineHeight * 2 );
		hazardButton.setX( leftX - hazardButton.getWidth( ) / 2 );
		hazardButton.setY( centerY + lineHeight * 1 );
		level1Button.setX( leftX - level1Button.getWidth( ) / 2 );
		level1Button.setY( centerY + lineHeight * 0 );
		dragonButton.setX( leftX - dragonButton.getWidth( ) / 2 );
		dragonButton.setY( centerY + lineHeight * -1 );

		backButton.setX( leftX - backButton.getWidth( ) / 2 );
		backButton.setY( centerY + lineHeight * -2 );

		man.setPosition( width / 2 - 50, height / 2 + 50 );
		lady.setPosition( width / 2 + 200, height / 2 - 200 );
		for ( Falling g : gears ) {
			resizeGears( g );
		}
		for ( Falling d : debris ) {
			resizeDebris( d );
		}

	}

	@Override
	public void resume( ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show( ) {

	}

	/**
	 * loads buttons appropriately
	 */
	private void loadButtons( ) {
		physicsButton = new TextButton( "Physics Test Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.PHYSICS ) );
		resurrectButton = new TextButton( "Parallax Test Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.RESURRECT ) );
		hazardButton = new TextButton( "Hazard Test Screen", fancyFont,
				new ScreenSwitchHandler( ScreenType.HAZARD ) );
		level1Button = new TextButton( "AlphaBot", fancyFont,
				new ScreenSwitchHandler( ScreenType.LOADING_1 ) );
		dragonButton = new TextButton( "Dragon", fancyFont,
				new ScreenSwitchHandler( ScreenType.LOADING_2 ) );

		backButton = new TextButton( "Back", fancyFont,
				new ScreenSwitchHandler( ScreenType.MAIN_MENU ) );
		physicsButton.setColored( true );

		Buttons.add( physicsButton );
		Buttons.add( resurrectButton );
		Buttons.add( hazardButton );
		Buttons.add( level1Button );
		Buttons.add( dragonButton );
		Buttons.add( backButton );

	}
	
	private void updateDebris( ) {
		for ( Falling g : gears ) {
			float y = g.sprite.getY( );
			if ( y < -g.sprite.getHeight( ) ) {
				resetGears( g );
				continue;
			}
			g.sprite.setY( y - g.fallSpeed );
			g.sprite.rotate( g.rotateSpeed );
		}
		for ( Falling d : debris ) {
			float y = d.sprite.getY( );
			if ( y < -d.sprite.getHeight( ) ) {
				resetDebris( d );
				continue;
			}
			d.sprite.setY( y - d.fallSpeed );
			d.sprite.rotate( d.rotateSpeed );
		}
	}

	private void createDebris( TextureAtlas gearAtlas, TextureAtlas common ) {
		for ( int i = 1; i <= 5; i++ ) {
			Sprite s = gearAtlas.createSprite( "gear" + i );
			s.setOrigin( s.getWidth( ) / 2, s.getHeight( ) / 2 );
			Falling temp = new Falling( s );
			gears.add( temp );
		}
		addCommon( common, "hex_screw" );
		addCommon( common, "pipeLR" );
		addCommon( common, "pipeUR" );
		addCommon( common, "flat_head_circular2" );
		addCommon( common, "power_screw" );
		addCommon( common, "pipeEndL" );
		addCommon( common, "pipeEndD" );
		addCommon( common, "flat_head_circular" );
		addCommon( common, "pipeEndU" );
		addCommon( common, "switch_on" );
		addCommon( common, "switch_off" );
		addCommon( common, "pipeEndR" );
		addCommon( common, "pipeDL" );
		addCommon( common, "pipeUD" );
		addCommon( common, "pipeUL" );
		addCommon( common, "pipeDR" );
		for ( int i = 0; i < 5; i++ ) {
			addCommon( common, "chainlink" );
			addCommon( common, "rivet1" );
			addCommon( common, "rivet2" );
			addCommon( common, "rivet3" );
			addCommon( common, "rivet4" );
		}
	}

	private void addCommon( TextureAtlas common, String name ) {
		Sprite s = common.createSprite( name );
		s.setOrigin( s.getWidth( ) / 2, s.getHeight( ) / 2 );
		Falling temp = new Falling( s );
		debris.add( temp );
	}

	private void resetGears( Falling d ) {
		resetDebris( d );
		d.sprite.setScale( 0.1f + ( float ) Math.random( ) * 0.4f );
	}

	private void resetDebris( Falling d ) {
		double newX = width * Math.random( );
		double newY = height + Math.random( ) * height / 2;
		d.sprite.setPosition( ( float ) newX, ( float ) newY );
		d.fallSpeed = ( float ) ( 0.5f + Math.random( ) * 1.5f );
		d.rotateSpeed = ( float ) ( Math.random( ) * 0.7 );
		d.sprite.setScale( 0.3f + ( float ) Math.random( ) * 0.7f );
	}

	private void resizeGears( Falling d ) {
		resizeDebris( d );
		d.sprite.setScale( 0.1f + ( float ) Math.random( ) * 0.4f );
	}

	private void resizeDebris( Falling d ) {
		resetDebris( d );
		double newX = width * Math.random( );
		double newY = Math.random( ) * height;
		d.sprite.setPosition( ( float ) newX, ( float ) newY );
	}

	private void moveCharacters( float delta ) {
		time += delta / 3;
		man.update( delta / 2 );
		lady.update( delta / 2 );
		float mx = man.getX( ) - ( float ) Math.cos( time - delta ) * 0.25f; 
		float my = man.getY( ) - ( float ) Math.sin( time ) * 0.6f;
		mx = mx + manDir * 0.5f;
		if ( mx > width * 5 / 6 )
			manDir -= 0.05;
		if ( mx < menuBG.getWidth( ) * 1.5)
			manDir += 0.05;
		man.setPosition( mx, my );
		float fx = lady.getX( ) + ( float ) ( Math.cos( time + delta ) * 0.25f );
		float fy = lady.getY( ) + ( float ) Math.sin( time + delta ) * 1.1f;
		fx = fx + ladyDir * 0.5f;
		if ( fx > width * 5 / 6 ) {
			ladyDir -= 0.15;
		}
		if ( fx < menuBG.getWidth( ) * 1.5 ) {
			ladyDir += 0.15;
		}
		lady.setPosition( fx, fy );
	}
}

