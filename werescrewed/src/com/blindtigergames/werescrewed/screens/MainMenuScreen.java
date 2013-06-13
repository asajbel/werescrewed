package com.blindtigergames.werescrewed.screens;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Falling;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.animator.SimpleSpinemator;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.gui.TextButton;
import com.blindtigergames.werescrewed.gui.TextButton.ButtonHandler;
import com.blindtigergames.werescrewed.sound.SoundManager;

class MainMenuScreen extends MenuScreen {

	public ScreenType screenType;
	private SpriteBatch batch = null;
	private Sprite menuBG = null;
	//private Sprite fade = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont;
	// private Label headingLabel = null;
	private int lineHeight = 0;

	private TextButton storyButton = null;
	private TextButton levelSelectButton = null;
	private TextButton optionsButton = null;
	private TextButton creditsButton = null;
	private TextButton exitButton = null;
	
	private SimpleSpinemator man = null;
	private SimpleSpinemator lady = null;
	private Array< Falling > debris = null;
	private Array< Falling > gears = null;
	private float time;
	private float manDir = 1;
	private float ladyDir = -1;

	TweenManager manager = new TweenManager( );

	public MainMenuScreen( ) {
		super( );
		batch = new SpriteBatch( );
		font = new BitmapFont( );
	}

	@Override
	public void load( ){
		super.load( );
		fancyFont = WereScrewedGame.manager.getFont( "longdon" );
		TextureRegion back = WereScrewedGame.manager.getAtlas( "menu-textures" ).findRegion( "menu" );
		menuBG = new Sprite( back );
		//Texture fadeScreen = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
		//		+ "/menu/transition.png", Texture.class );
		//fade = new Sprite( fadeScreen );
				
		Texture transition = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/transitions/trans-gear.png", Texture.class );
		trans = new Sprite( transition );
		maxScale = trans.getHeight( ) * SCALE_SIZE;
		scale = 1.0f;
		transInEnd = false;
		
		man = new SimpleSpinemator( "red_male_atlas", "male", "fall_idle", true );
		lady = new SimpleSpinemator( "red_female_atlas", "female", "fall_idle", true );
		gears = new Array< Falling >( );
		debris = new Array< Falling >( );
		TextureAtlas gearsAtlas = WereScrewedGame.manager.getAtlas( "gears" );
		TextureAtlas common = WereScrewedGame.manager
				.getAtlas( "common-textures" );
		
		for ( int i = 0; i < 5; i++ )
			createDebris( gearsAtlas, common );
		
		loadButtons( );
		setClearColor( 105f/255f, 208f/255f, 255f/255f, 1f );
		bgm = WereScrewedGame.manager.get( WereScrewedGame.dirHandle + "/menu/menuTheme.mp3" ,Music.class);
	}
	
	@Override
	public void render( float delta ) {
		super.render( delta );
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
		creditsButton.draw( batch, camera );
		exitButton.draw( batch, camera );

		//if ( !alphaFinish )
			//setAlpha( -0.02f );

		man.draw( batch );
		lady.draw( batch );
		//fade.draw( batch, alpha );
		
		if ( !transInEnd ) {
			drawTransIn( batch );
		}
		
		if ( !transOutEnd ) {
			drawTransOut( batch );
		}
		
		batch.end( );

		if (  WereScrewedGame.debug && Gdx.input.isKeyPressed( Keys.P ) ) {
			System.exit( 1 );
		}
		
		if (  WereScrewedGame.debug && Gdx.input.isKeyPressed( Keys.Z ) ) {
			ScreenManager.getInstance( ).show( ScreenType.PHYSICS );
		}

		if (  WereScrewedGame.debug && Gdx.input.isKeyPressed( Keys.D ) ) {
			ScreenManager.getInstance( ).show( ScreenType.LOADING_2 );
		}

		if ( WereScrewedGame.debug &&  Gdx.input.isKeyPressed( Keys.A ) ) {
			ScreenManager.getInstance( ).show( ScreenType.LOADING_1 );
		}
		
		/*if ( Gdx.input.isKeyPressed( Keys.NUM_8 ) ) {
			ScreenManager.getInstance( ).show( ScreenType.LOADING_TROPHY_2 );
		}*/
		
		if ( WereScrewedGame.debug &&  Gdx.input.isKeyPressed( Keys.H ) ) {
			ScreenManager.getInstance( ).show( ScreenType.HAZARD );
		}
	}

	@Override
	public void resize( int _width, int _height ) {
		super.resize( _width, _height );
		setClearColor( 105f/255f, 208f/255f, 255f/255f, 1f );
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, WereScrewedGame.getWidth(), WereScrewedGame.getHeight() );
		batch.setProjectionMatrix( camera.combined );
		int leftX = ( int ) menuBG.getWidth( ) / 2;
		int centerY = height / 2;

		//fade.setPosition( width / 2 - fade.getWidth( ) / 2, height
		//		/ 2 - fade.getHeight( ) / 2 );
		//fade.setScale( width / fade.getWidth( ), height
		//		/ fade.getHeight( ) );
		// menuBG.setScale( width / menuBG.getWidth( ), width / menuBG.getWidth(
		// ) );
		menuBG.setPosition( 0, WereScrewedGame.getHeight() / 2 - menuBG.getHeight( ) / 2 );
		// menuBG.setPosition( width / 2 - menuBG.getWidth( ) / 2, height / 2 -
		// menuBG.getHeight( ) / 2 );
		// headingLabel.setX( leftX - headingLabel.getWidth( ) / 2 );
		// headingLabel.setY( centerY - 0 * lineHeight );
		storyButton.setX( leftX - storyButton.getWidth( ) / 2 );
		storyButton.setY( height / 2 + 3 * lineHeight );
		levelSelectButton.setX( leftX - levelSelectButton.getWidth( ) / 2 );
		levelSelectButton.setY( centerY + 2 * lineHeight );
		optionsButton.setX( leftX - optionsButton.getWidth( ) / 2 );
		optionsButton.setY( centerY + 1 * lineHeight );
		creditsButton.setX( leftX - creditsButton.getWidth( ) / 2 );
		creditsButton.setY( centerY + 0 * lineHeight);
		// imoverButton.setX( centerX - imoverButton.getWidth( )/2 );
		// imoverButton.setY( centerY - lineHeight );
		exitButton.setX( leftX - exitButton.getWidth( ) / 2 );
		exitButton.setY( centerY + -1 * lineHeight );
		man.setPosition( WereScrewedGame.getWidth() / 2 - 50,  WereScrewedGame.getHeight() / 2 + 50 );
		lady.setPosition( WereScrewedGame.getWidth() / 2 + 200,  WereScrewedGame.getHeight() / 2 - 200 );
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
		buttonTex = WereScrewedGame.manager.getAtlas( "menu-textures" ).findRegion( "button" );
		
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) + 30 );
		// headingLabel = new Label( "We're Screwed!!", fancyFont );

		storyButton = new TextButton( "Start", fancyFont, buttonTex, 
				new ScreenSwitchHandler( ScreenType.LOADING_1 ) );
		levelSelectButton = new TextButton( "Levels", fancyFont, buttonTex, 
				new ScreenSwitchHandler( ScreenType.LEVEL_SELECT ) );
		optionsButton = new TextButton( "Options", fancyFont, buttonTex, 
				new ScreenSwitchHandler( ScreenType.OPTIONS_MENU ) );
		creditsButton = new TextButton( "Credits", fancyFont, buttonTex,
				new ScreenSwitchHandler( ScreenType.CREDITS ) );
		exitButton = new TextButton( "Exit", fancyFont, buttonTex, new ButtonHandler( ) {
			@Override
			public void onClick( ) {
				Gdx.app.exit( );
			}
		} );
		storyButton.setColored( true );
		Buttons.add( storyButton );
		Buttons.add( levelSelectButton );
		Buttons.add( optionsButton );
		Buttons.add( creditsButton );
		Buttons.add( exitButton );
	}

	@Override
	public void show( ) {
		super.show( );
		SoundManager.clearLoops( );
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
		if ( mx > WereScrewedGame.getWidth( ) * 5 / 6 )
			manDir -= 0.15;
		if ( mx < menuBG.getWidth( ) * 1.2 ) 
			manDir += 0.15;
		man.setPosition( mx, my );
		float fx = lady.getX( );// + ( float ) ( Math.cos( time + delta ) *
								// 0.25f );
		float fy = lady.getY( ) + ( float ) Math.sin( time + delta / 6 ) * 0.6f;
		fx = fx + ladyDir * 0.3f;
		if ( fx > WereScrewedGame.getWidth( ) * 5 / 6 ) {
			ladyDir -= 0.15;
		}
		if ( fx < menuBG.getWidth( ) * 1.2 ) {
			ladyDir += 0.15;
		}
		lady.setPosition( fx, fy );
	}
}
