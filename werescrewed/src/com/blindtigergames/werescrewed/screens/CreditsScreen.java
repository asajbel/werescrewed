package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Version;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.animator.SimpleSpinemator;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.gui.Label;
import com.blindtigergames.werescrewed.gui.TextButton;
import com.blindtigergames.werescrewed.util.Util;

class CreditsScreen extends MenuScreen {
	// implements com.badlogic.gdx.Screen
	private SpriteBatch batch = null;
	private OrthographicCamera camera = null;
	private BitmapFont font = null;
	private BitmapFont fancyFont = null;
	//private Texture logo = null;
	private int people = 18;
	private Label screenLabel = null;
	private Label[] authorsLabel = new Label[ people ];
	private Label licenseLabel = null;
	private Label versionLabel = null;
	private Label codeLabel = null;
	private Label artLabel = null;
	private Label soundLabel = null;
	private TextButton backButton = null;
	private int lineHeight = 0;
	
	private SimpleSpinemator man = null;
	private SimpleSpinemator lady = null;
	
	World world;

	public CreditsScreen( ) {
		super( );
		batch = new SpriteBatch( );
		font = new BitmapFont( );
	}

	@Override
	public void load( ){
		super.load( );
		fancyFont = WereScrewedGame.manager.getFont( "longdon" );
		//logo = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
		//		+ "/common/title_background.png", Texture.class );
				
		Texture transition = WereScrewedGame.manager.get( WereScrewedGame.dirHandle
				+ "/transitions/trans-gear.png", Texture.class );
		trans = new Sprite( transition );
		maxScale = trans.getHeight( ) * SCALE_SIZE;
		scale = 1.0f;
		transInEnd = false;
		

		fancyFont.setScale( 1.0f );
		lineHeight = Math.round( 2.5f * font.getCapHeight( ) );
		screenLabel = new Label( "WE'RE SCREWED!! CREDITS", fancyFont );
		licenseLabel = new Label( "Blind Tiger Games", fancyFont );
		versionLabel = new Label( "Ver. " + Version.VERSION, fancyFont );
		codeLabel = new Label( "Programmers: ", fancyFont );
		artLabel = new Label( "Artists: ", fancyFont );
		soundLabel = new Label( "Musicians:", fancyFont );
		
		WereScrewedGame.manager.loadAtlas( "data/menu/menu-textures.pack" );
		
		buttonTex = WereScrewedGame.manager.getAtlas( "menu-textures" ).findRegion( "button" );
		
		backButton = new TextButton( "Back", fancyFont, buttonTex,
				new ScreenSwitchHandler( ScreenType.LOADING_MENU ) );
		backButton.setColored( true );
		 initPeople( );
		 
		 
		 
		initWorld( );
	}
	
	private void initWorld(){
		world = new World( Vector2.Zero, false );
		int radius = ( int ) ( WereScrewedGame.getWidth( )*.46875f );
		Vector2 origin = new Vector2(WereScrewedGame.getWidth() / 2, -radius/1.3f);
		
		Skeleton circle = new Skeleton( "circle", origin.cpy( ), null, world );
		
		man = new SimpleSpinemator( "red_male_atlas", "male", "run_screw_ready", true );
		lady = new SimpleSpinemator( "red_female_atlas", "female", "run", true );
		man.setScale( new Vector2(0.5f,0.5f) );
		lady.setScale( new Vector2(0.5f,0.5f) );
		
		man.setPosition( WereScrewedGame.getWidth() / 2 - 50,  WereScrewedGame.getHeight() / 2 + 50 );
		lady.setPosition( WereScrewedGame.getWidth() / 2 + 200,  WereScrewedGame.getHeight() / 2 - 200 );
		
		Vector2 manPos = Util.PointOnCircle( radius, Util.PI/3f, origin );
		man.setPosition( manPos );
		man.setRotation( Util.angleBetweenPoints( origin, manPos )*Util.RAD_TO_DEG-90 );
		
		Vector2 ladyPos =  Util.PointOnCircle( radius, 2*Util.PI/3f, origin );
		lady.setRotation( Util.angleBetweenPoints( origin, ladyPos )*Util.RAD_TO_DEG-90);
		lady.setPosition( ladyPos );
		
		
	}
	
	private void initPeople ( ) {
		authorsLabel[ 0 ] = new Label( "Anders Sajbel", fancyFont );
		authorsLabel[ 1 ] = new Label( "William Pacini", fancyFont );
		authorsLabel[ 2 ] = new Label( "Dennis Foley", fancyFont );
		authorsLabel[ 3 ] = new Label( "Dan Malear", fancyFont );
		authorsLabel[ 4 ] = new Label( "Edward Boning", fancyFont );
		authorsLabel[ 5 ] = new Label( "Edward Ramirez", fancyFont );
		authorsLabel[ 6 ] = new Label( "Jennifer Makaiwi", fancyFont );
		authorsLabel[ 7 ] = new Label( "Kevin Cameron", fancyFont );
		authorsLabel[ 8 ] = new Label( "Nick Patti", fancyFont );
		authorsLabel[ 9 ] = new Label( "Ranveer Dhaliwal", fancyFont );
		authorsLabel[ 10 ] = new Label( "B. Stewart Bracken", fancyFont );
		authorsLabel[ 11 ] = new Label( "Victor Nguyen", fancyFont );
		authorsLabel[ 12 ] = new Label( "Lindsey Dillon", fancyFont );
		authorsLabel[ 13 ] = new Label( "Melissa Eap", fancyFont );
		authorsLabel[ 14 ] = new Label( "Michael Monterrosa", fancyFont );
		authorsLabel[ 15 ] = new Label( "Rebecca Alto", fancyFont );
		authorsLabel[ 16 ] = new Label( "Augustus Sandage", fancyFont );
		authorsLabel[ 17 ] = new Label( "Daniel Greiner", fancyFont );
	}

	@Override
	public void render( float delta ) {
		super.render( delta );
		Gdx.gl.glClearColor( 0.0f, 0.0f, 0.0f, 1f );
		Gdx.gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
		batch.begin( );
		//batch.draw( logo, 0, 0 );
		screenLabel.draw( batch );
		licenseLabel.draw( batch );
		for ( int i = 0; i < people; i++ ) {
			authorsLabel[ i ].draw( batch );
		}
		codeLabel.draw( batch );
		artLabel.draw( batch );
		soundLabel.draw( batch );
		versionLabel.draw( batch );
		backButton.draw( batch, camera );
		
		if ( !transInEnd ) {
			drawTransIn( batch );
		}
		
		if ( !transOutEnd ) {
			drawTransOut( batch, ScreenType.LOADING_MENU );
		}
		man.update( delta  );
		lady.update( delta  );
		man.draw( batch );
		lady.draw( batch );
		
		batch.end( );
		
		if ( WereScrewedGame.p1Controller != null ) {
			if ( WereScrewedGame.p1ControllerListener.jumpPressed( ) && transOutEnd ) {
				transOutEnd = false;
			}
		}
		if ( WereScrewedGame.p2Controller != null ) {
			if ( WereScrewedGame.p2ControllerListener.jumpPressed( ) && transOutEnd ) {
				transOutEnd = false;
			}
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.ENTER ) && transOutEnd ) {
			transOutEnd = false;
		} 
		
		
		world.step( WereScrewedGame.oneOverTargetFrameRate, 2, 1 );
	}

	@Override
	public void resize( int _width, int _height ) {
		super.resize( _width, _height );
		camera = new OrthographicCamera( );
		camera.setToOrtho( false, width, height );
		batch.setProjectionMatrix( camera.combined );
		int centerX = width / 2;
		int centerY = height / 2;
		int k = 8;
		int pos = width / 7;
		screenLabel.setX( centerX - screenLabel.getWidth( ) / 2 );
		screenLabel.setY( centerY + 12 * lineHeight );
		licenseLabel.setX( centerX - licenseLabel.getWidth( ) / 2 );
		licenseLabel.setY( centerY + ( k + 2 ) * lineHeight);
		codeLabel.setX( centerX - codeLabel.getWidth( ) / 2 );
		codeLabel.setY( centerY + k * lineHeight );
		k--;
		for ( int j = 0; j < 12; j++ ) {
			authorsLabel[ j ].setX( centerX + pos - authorsLabel[ j ].getWidth( ) / 2 );
			authorsLabel[ j ].setY( centerY + k * lineHeight );
			pos = -pos;
			if ( j % 2 != 0 ) 
				k--;
		}
		k--;
		artLabel.setX( centerX - artLabel.getWidth( ) / 2 );
		artLabel.setY( centerY + k * lineHeight );
		k--;
		for ( int j = 12; j < 16; j++ ) {
			authorsLabel[ j ].setX( centerX + pos - authorsLabel[ j ].getWidth( ) / 2 );
			authorsLabel[ j ].setY( centerY + k * lineHeight );
			pos = -pos;
			if ( j % 2 != 0 ) 
				k--;
		}
		k--;
		soundLabel.setX( centerX - soundLabel.getWidth( ) / 2 );
		soundLabel.setY( centerY + k * lineHeight );
		k--;
		for ( int j = 16; j < people; j++ ) {
			authorsLabel[ j ].setX( centerX + pos - authorsLabel[ j ].getWidth( ) / 2 );
			authorsLabel[ j ].setY( centerY + k * lineHeight );
			pos = -pos;
			if ( j % 2 != 0 ) 
				k--;
		}
		k -= 2;
		versionLabel.setX( centerX - versionLabel.getWidth( ) / 2 );
		versionLabel.setY( centerY + k * lineHeight );
		backButton.setX( centerX - backButton.getWidth( ) / 2 );
		backButton.setY( 100 + backButton.getHeight( ) );
	}
}
