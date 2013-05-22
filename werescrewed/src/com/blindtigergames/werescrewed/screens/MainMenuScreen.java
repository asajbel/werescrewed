package com.blindtigergames.werescrewed.screens;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Falling;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.animator.SingleSpinemator;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.gui.TextButton;
import com.blindtigergames.werescrewed.gui.TextButton.ButtonHandler;

class MainMenuScreen extends Screen {

	public ScreenType screenType;
	private SpriteBatch batch = null;
	private Sprite menuBG = null;
	private Sprite fade = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont;
	// private Label headingLabel = null;
	private TextButton exitButton = null;
	private int lineHeight = 0;

	private TextButton storyButton = null;
	private TextButton levelSelectButton = null;
	private TextButton optionsButton = null;
	private SingleSpinemator man = null;
	private SingleSpinemator lady = null;
	private Array< Falling > debris = null;
	private Array< Falling > gears = null;
	private int width, height;
	private float time;
	private float manDir = 1;
	private float ladyDir = -1;

	TweenManager manager = new TweenManager( );

	public MainMenuScreen( ) {
		batch = new SpriteBatch( );
		font = new BitmapFont( );
		fancyFont = WereScrewedGame.manager.getFont( "longdon" );
		man = new SingleSpinemator( "red_male_atlas", "male", "fall_idle" );
		lady = new SingleSpinemator( "red_female_atlas", "female", "fall_idle" );
		gears = new Array< Falling >( );
		debris = new Array< Falling >( );
		TextureAtlas gearsAtlas = WereScrewedGame.manager.getAtlas( "gears" );
		TextureAtlas common = WereScrewedGame.manager
				.getAtlas( "common-textures" );
		Texture fadeScreen = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/menu/transition.png", Texture.class );
		Texture trans = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/transition/trans-gear.png", Texture.class );
		fade = new Sprite( fadeScreen );
		transition = new Sprite( trans );
		for ( int i = 0; i < 5; i++ )
			createDebris( gearsAtlas, common );
		loadButtons( );
	}

	@Override
	public void render( float delta ) {
		super.render( delta );
		Gdx.gl.glClearColor( 0.4f, 0.6f, 1.0f, 1f );
		// Gdx.gl.glClearColor( 79.0f / 255.0f, 82.0f / 255.0f, 104.0f / 255.0f,
		// 1.0f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		manager.update( delta );
		moveCharacters( delta );
		updateDebris( );
		batch.begin( );
		for ( Falling g : gears ) {
			g.sprite.draw( batch );
		}
		for ( Falling d : debris ) {
			d.sprite.draw( batch );
		}
		menuBG.draw( batch );

		storyButton.draw( batch, camera );
		levelSelectButton.draw( batch, camera );
		optionsButton.draw( batch, camera );

		exitButton.draw( batch, camera );

		if ( !alphaFinish )
			setAlpha( -0.02f );

		man.draw( batch );
		lady.draw( batch );
		fade.draw( batch, alpha );
		transition.draw( batch );
		batch.end( );

		if ( Gdx.input.isKeyPressed( Keys.P ) ) {
			System.exit( 1 );
		}

		if ( Gdx.input.isKeyPressed( Keys.Z ) ) {
			ScreenManager.getInstance( ).show( ScreenType.PHYSICS );
		}

		if ( Gdx.input.isKeyPressed( Keys.D ) ) {
			ScreenManager.getInstance( ).show( ScreenType.LOADING_2 );
		}

		if ( Gdx.input.isKeyPressed( Keys.A ) ) {
			ScreenManager.getInstance( ).show( ScreenType.LOADING_1 );
		}
		if ( Gdx.input.isKeyPressed( Keys.H ) ) {
			ScreenManager.getInstance( ).show( ScreenType.HAZARD );
		}
		if ( Gdx.input.isKeyPressed( Keys.T ) ) {
			setTransFinish( !isTransFinished( ) );
		}

	}

	@Override
	public void resize( int width, int height ) {
		this.width = width;
		this.height = height;
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int leftX = ( int ) menuBG.getWidth( ) / 2;
		int centerY = height / 2;
		@SuppressWarnings( "unused" )
		float scaleX = width / 1280f;
		@SuppressWarnings( "unused" )
		float scaleY = height / 720f;

		fade.setPosition( width / 2 - fade.getWidth( ) / 2, height
				/ 2 - fade.getHeight( ) / 2 );
		fade.setScale( width / fade.getWidth( ), height
				/ fade.getHeight( ) );
		// menuBG.setScale( width / menuBG.getWidth( ), width / menuBG.getWidth(
		// ) );
		menuBG.setPosition( 0, height / 2 - menuBG.getHeight( ) / 2 );
		// menuBG.setPosition( width / 2 - menuBG.getWidth( ) / 2, height / 2 -
		// menuBG.getHeight( ) / 2 );
		// headingLabel.setX( leftX - headingLabel.getWidth( ) / 2 );
		// headingLabel.setY( centerY - 0 * lineHeight );
		storyButton.setX( leftX - storyButton.getWidth( ) / 2 );
		storyButton.setY( height / 2 + 2 * lineHeight );
		levelSelectButton.setX( leftX - levelSelectButton.getWidth( ) / 2 );
		levelSelectButton.setY( centerY + 1 * lineHeight );
		optionsButton.setX( leftX - optionsButton.getWidth( ) / 2 );
		optionsButton.setY( centerY + 0 * lineHeight );
		// imoverButton.setX( centerX - imoverButton.getWidth( )/2 );
		// imoverButton.setY( centerY - lineHeight );
		exitButton.setX( leftX - exitButton.getWidth( ) / 2 );
		exitButton.setY( centerY + -1 * lineHeight );
		man.setPosition( width / 2 - 50, height / 2 + 50 );
		lady.setPosition( width / 2 + 200, height / 2 - 200 );
		for ( Falling g : gears ) {
			resizeGears( g );
		}
		for ( Falling d : debris ) {
			resizeDebris( d );
		}

		// Tween.to( storyButton, ButtonTweenAccessor.POSITION_X, 1 ).ease(
		// TweenEquations.easeInBounce ).target( leftX - storyButton.getWidth(
		// )/2 ).start( manager );
	}

	/**
	 * loads all button related content appropriately
	 */
	private void loadButtons( ) {
		// font = WereScrewedGame.manager.getFont( "ornatique" );
		Texture back = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/menu/menu.png", Texture.class );
		menuBG = new Sprite( back );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) + 50 );
		// headingLabel = new Label( "We're Screwed!!", fancyFont );

		storyButton = new TextButton( "Start", fancyFont,
				new ScreenSwitchHandler( ScreenType.LOADING_1 ) );
		levelSelectButton = new TextButton( "Level Select", fancyFont,
				new ScreenSwitchHandler( ScreenType.LEVEL_SELECT ) );
		optionsButton = new TextButton( "Options", fancyFont,
				new ScreenSwitchHandler( ScreenType.OPTIONS ) );
		exitButton = new TextButton( "Exit", fancyFont, new ButtonHandler( ) {
			@Override
			public void onClick( ) {
				Gdx.app.exit( );
			}
		} );
		storyButton.setColored( true );
		Buttons.add( storyButton );
		Buttons.add( levelSelectButton );
		Buttons.add( optionsButton );
		Buttons.add( exitButton );
	}

	@Override
	public void show( ) {

	}

	@Override
	public void hide( ) {

	}

	@Override
	public void pause( ) {

	}

	@Override
	public void resume( ) {

	}

	@Override
	public void dispose( ) {
	}

	/*
	 * levelSelectButton = new Button( "Level Select", fancyFont, new
	 * ScreenSwitchHandler(ScreenType.LEVEL_SELECT)); optionsButton = new
	 * Button("Options", fancyFont, new ScreenSwitchHandler(
	 * ScreenType.OPTIONS)); exitButton = new Button( "Exit", fancyFont, new
	 * ButtonHandler( ) {
	 * 
	 * @Override public void onClick( ) { Gdx.app.exit( ); } } );
	 */

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
		double dir = Math.random( );
		if ( dir > 0.5 )
			dir = 1;
		else
			dir = -1;
		d.rotateSpeed = ( float ) ( dir * Math.random( ) * 0.7 );
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
		time += delta;
		man.update( delta / 2 );
		lady.update( delta / 2 );
		float mx = man.getX( );// - ( float ) Math.cos( time - delta ) * 0.25f;
		float my = man.getY( ) - ( float ) Math.sin( time - delta / 6 ) * 0.6f;
		mx = mx + manDir * 0.3f;
		if ( mx > width * 5 / 6 )
			manDir -= 0.15;
		if ( mx < menuBG.getWidth( ) * 1.2 )
			manDir += 0.15;
		man.setPosition( mx, my );
		float fx = lady.getX( );// + ( float ) ( Math.cos( time + delta ) *
								// 0.25f );
		float fy = lady.getY( ) + ( float ) Math.sin( time + delta / 6 ) * 0.6f;
		fx = fx + ladyDir * 0.3f;
		if ( fx > width * 5 / 6 ) {
			ladyDir -= 0.15;
		}
		if ( fx < menuBG.getWidth( ) * 1.2 ) {
			ladyDir += 0.15;
		}
		lady.setPosition( fx, fy );
	}
}
