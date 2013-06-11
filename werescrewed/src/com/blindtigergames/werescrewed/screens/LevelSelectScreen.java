package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Falling;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.animator.SimpleSpinemator;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.gui.TextButton;

public class LevelSelectScreen extends MenuScreen {

	public ScreenType screenType;
	// private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont = null;
	private Sprite menuBG = null;
//	private Sprite fade = null;
	private Label screenLabel = null;
//	private TextButton resurrectButton = null;
//	private TextButton hazardButton = null;
//	private TextButton physicsButton = null;
	private int lineHeight = 0;
	private TextButton level1Button;
	private TextButton backButton = null;
	private TextButton dragonButton = null;
	private SimpleSpinemator man = null;
	private SimpleSpinemator lady = null;
	private Array< Falling > debris = null;
	private Array< Falling > gears = null;
	private float time;
	private float manDir = 1;
	private float ladyDir = -1;

	public LevelSelectScreen( ) {
		super( );
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, WereScrewedGame.getWidth(), WereScrewedGame.getHeight() );
		font = new BitmapFont( );
	}

	@Override
	public void load( ){
		super.load( );
		fancyFont = WereScrewedGame.manager.getFont( "longdon" );
		Texture transition = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/transitions/trans-gear.png", Texture.class );
		trans = new Sprite( transition );
		TextureRegion back = WereScrewedGame.manager.getAtlas( "menu-textures" ).findRegion( "menu" );
		menuBG = new Sprite( back );
		//Texture fadeScreen = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
		//		+ "/menu/transition.png", Texture.class );
		//fade = new Sprite( fadeScreen );
		
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) + 50 );
		screenLabel = new Label( "Level Select", fancyFont );
		
		man = new SimpleSpinemator( "red_male_atlas", "male", "fall_idle", true );
		lady = new SimpleSpinemator( "red_female_atlas", "female", "fall_idle", true );
		gears = new Array< Falling >( );
		debris = new Array< Falling >( );
		
		maxScale = trans.getHeight( ) * SCALE_SIZE;
		scale = 1.0f;
		transInEnd = false;
		
		TextureAtlas gearsAtlas = WereScrewedGame.manager.getAtlas( "gears" );
		TextureAtlas common = WereScrewedGame.manager
				.getAtlas( "common-textures" );
		for ( int i = 0; i < 5; i++ )
			createDebris( gearsAtlas, common );
		
		loadButtons( );
		setClearColor( 105f/255f, 208f/255f, 255f/255f, 1f );
	}

	@Override
	public void render( float delta ) {
		super.render( delta );
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
		// screenLabel.draw( batch );
//		physicsButton.draw( batch, camera );
//		resurrectButton.draw( batch, camera );
//		hazardButton.draw( batch, camera );
		level1Button.draw( batch, camera );
		dragonButton.draw( batch, camera );

		backButton.draw( batch, camera );

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

	}

	@Override
	public void resize( int _width, int _height ) {
		super.resize( _width, _height );

		camera = new OrthographicCamera( );
		camera.setToOrtho( false, WereScrewedGame.getWidth( ), WereScrewedGame.getHeight( ) );
		batch.setProjectionMatrix( camera.combined );
		int leftX = ( int ) menuBG.getWidth( ) / 2;// / 5 - 20;
		int centerY = height / 2;

		//fade.setPosition( width / 2 - fade.getWidth( ) / 2, height
		//		/ 2 - fade.getHeight( ) / 2 );
		//fade.setScale( width / fade.getWidth( ), height
		//		/ fade.getHeight( ) );
		// menuBG.setScale( width / menuBG.getWidth( ), width / menuBG.getWidth(
		// ) );
		menuBG.setPosition( 0, WereScrewedGame.getHeight( ) / 2 - menuBG.getHeight( ) / 2 );
		// menuBG.setPosition( width / 2 - menuBG.getWidth( ) / 2, height / 2 -
		// menuBG.getHeight( ) / 2 );
		screenLabel.setX( leftX - screenLabel.getWidth( ) / 2 );
		screenLabel.setY( centerY + 3 * lineHeight );

		// quick fix
//		physicsButton.setX( leftX - physicsButton.getWidth( ) / 2 );
//		physicsButton.setY( centerY + lineHeight * 2 );
//		resurrectButton.setX( leftX - resurrectButton.getWidth( ) / 2 );
//		resurrectButton.setY( centerY + lineHeight * 2 );
//		hazardButton.setX( leftX - hazardButton.getWidth( ) / 2 );
//		hazardButton.setY( centerY + lineHeight * 1 );
		level1Button.setX( leftX - level1Button.getWidth( ) / 2 );
		level1Button.setY( centerY + lineHeight * 2 - 25 );
		dragonButton.setX( leftX - dragonButton.getWidth( ) / 2 );
		dragonButton.setY( centerY + lineHeight * 1 - 25 );

		backButton.setX( leftX - backButton.getWidth( ) / 2 );
		backButton.setY( centerY + lineHeight * 0 - 25 );

		man.setPosition( WereScrewedGame.getWidth( ) / 2 - 50, WereScrewedGame.getHeight( ) / 2 + 50 );
		lady.setPosition( WereScrewedGame.getWidth( ) / 2 + 200, WereScrewedGame.getHeight( ) / 2 - 200 );
		for ( Falling g : gears ) {
			resizeGears( g );
		}
		for ( Falling d : debris ) {
			resizeDebris( d );
		}

	}

	/**
	 * loads buttons appropriately
	 */
	private void loadButtons( ) {		
		buttonTex = WereScrewedGame.manager.getAtlas( "menu-textures" ).findRegion( "button" );
		
//		physicsButton = new TextButton( "Physics Test Screen", fancyFont, buttonTex,
//				new ScreenSwitchHandler( ScreenType.PHYSICS ) );
//		resurrectButton = new TextButton( "Parallax Test Screen", fancyFont,
//				new ScreenSwitchHandler( ScreenType.RESURRECT ) );
//		hazardButton = new TextButton( "Hazard Test Screen", fancyFont,
//				new ScreenSwitchHandler( ScreenType.HAZARD ) );
		level1Button = new TextButton( "AlphaBot", fancyFont, buttonTex,
				new ScreenSwitchHandler( ScreenType.LOADING_1 ) );
		dragonButton = new TextButton( "Dragon", fancyFont, buttonTex,
				new ScreenSwitchHandler( ScreenType.LOADING_2 ) );

		backButton = new TextButton( "Back", fancyFont, buttonTex,
				new ScreenSwitchHandler( ScreenType.MAIN_MENU ) );
		level1Button.setColored( true );

//		Buttons.add( physicsButton );
//		Buttons.add( resurrectButton );
//		Buttons.add( hazardButton );
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
		time += delta / 3;
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
