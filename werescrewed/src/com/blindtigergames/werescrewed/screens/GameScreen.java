package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.collisionManager.MyContactListener;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.AnimatedSprite;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Player;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.input.InputHandler;
import com.blindtigergames.werescrewed.platforms.ComplexPlatform;
import com.blindtigergames.werescrewed.platforms.PlatformBuilder;
import com.blindtigergames.werescrewed.platforms.RoomPlatform;
import com.blindtigergames.werescrewed.platforms.ShapePlatform;
import com.blindtigergames.werescrewed.platforms.Shapes;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.screws.StructureScrew;
import com.blindtigergames.werescrewed.util.Util;

public class GameScreen implements com.badlogic.gdx.Screen {

	OrthographicCamera camera;
	Camera cam;
	SpriteBatch batch;
	Texture texture;
	Texture playerTexture;
	Sprite sprite;
	World world;
	SBox2DDebugRenderer debugRenderer;
	MyContactListener MCL;
	Body playerBody;
	Entity playerEntity;
	Player player;
	TiledPlatform tp, ground;
	RoomPlatform rp;
	ComplexPlatform cp;
	ShapePlatform sp;
	PlatformBuilder platBuilder;

	// testing screw
	Texture screwTex;
	Texture background;
	StructureScrew structScrew;
	InputHandler inputHandler;
	Skeleton rootSkeleton;
	Skeleton skeleton;

	// testing animations
	// testing animating sprites in here!
	// creating the physics body
	BodyDef animSpriteBDef;
	Body animatingSpriteBody;
	AnimatedSprite animatedSprite;
	AnimatedSprite anotherAnimatedSprite;
	Entity animatingEntity;

	FPSLogger logger;

	@SuppressWarnings( "unused" )
	private final Vector2 dec = new Vector2( .5f, 0 );
	@SuppressWarnings( "unused" )
	private final Vector2 acc = new Vector2( .3f, 0 );
	@SuppressWarnings( "unused" )
	private final Vector2 max = new Vector2( 1f, 0 );

	public GameScreen( ) {
		float zoom = 1.0f;
		float w = Gdx.graphics.getWidth( ) / zoom;
		float h = Gdx.graphics.getHeight( ) / zoom;

		inputHandler = new InputHandler( "player1" );
		texture = new Texture( Gdx.files.internal( "data/rletter.png" ) );
		// takes in width, height
		// cam = new Camera(w, h);
		batch = new SpriteBatch( );

		world = new World( new Vector2( 0, -55 ), true );
		// MCL = new MyContactListener();
		// world.setContactListener(MCL);
		String name = "player";

		player = new Player( name, world, new Vector2( 1.0f, 1.0f ) );

		cam = new Camera( w, h );
		platBuilder = new PlatformBuilder( world );
		tp = platBuilder.setName( "tp" ).setPosition( 200.0f, 100.0f )
				.setDimensions( 10, 1 ).setTexture( texture )
				.setResitituion( 0.0f ).buildTilePlatform( );

		rp = platBuilder.setPosition( -200.0f, 100.0f ).setName( "rp" )
				.setDimensions( 1, 10 ).setTexture( texture )
				.setResitituion( 0.0f ).buildRoomPlatform( );

		cp = new ComplexPlatform( "bottle", new Vector2( -100.0f, 100.0f ),
				new Texture( Gdx.files.internal( "data/bodies/test01.png" ) ),
				1, world, "complexTest" );
		sp = new ShapePlatform( "rhom", new Vector2( 100.0f, 300.0f ), texture,
				world, Shapes.plus, 1.0f, 1.0f, false );

		// testing screws
		screwTex = new Texture( Gdx.files.internal( "data/screw1.png" ) );
		background = new Texture( Gdx.files.internal( "data/libgdx.png" ) );
		skeleton = new Skeleton( "", Vector2.Zero, background, world );

		// testing animating sprites in here!
		// creating the physics body
		animSpriteBDef = new BodyDef( );
		animSpriteBDef.awake = true;
		animSpriteBDef.active = false;
		animatingSpriteBody = world.createBody( animSpriteBDef );

		// defining animated sprite
		int asFrames = 4;
		int asRows = 1;
		int asColumns = 4;
		float asSpeed = 0.25f;
		animatedSprite = new AnimatedSprite( asFrames, asRows, asColumns,
				asSpeed, "player_walking.png", Animation.NORMAL );

		float aasSpeed = 0.05f;
		anotherAnimatedSprite = new AnimatedSprite( asFrames, asRows,
				asColumns, aasSpeed, "jumping_man.png", Animation.LOOP_REVERSED );

		// here's the animating entity!
		// animatingEntity = new Entity("animatingSprite", animatedSprite,
		// animatingSpriteBody);

		// make sure you un-comment the next two lines debugRenderer = new
		// SBox2DDebugRenderer(BOX_TO_PIXEL); for physics world

		skeleton = new Skeleton( "", Vector2.Zero, null, world );
		rootSkeleton = new Skeleton( "root", Vector2.Zero, null, world );
		structScrew = new StructureScrew( "", tp.body.getPosition( ), 25, tp,
				skeleton, world );

		// tp.setMover( new TimelineMover( ) );

		ground = new PlatformBuilder( world ).setPosition( 0.0f, 0.0f )
				.setName( "ground" ).setDimensions( 100, 1 )
				.setTexture( texture ).setResitituion( 0.0f )
				.buildTilePlatform( );

		skeleton.addPlatformFixed( ground );
		skeleton.addPlatformFixed( tp );
		skeleton.addPlatformFixed( sp );
		skeleton.addPlatformFixed( cp );
		skeleton.addPlatformFixed( rp );

		rootSkeleton.addSkeleton( skeleton );
		// make sure you uncomment the next two lines debugRenderer = new
		// SBox2DDebugRenderer(BOX_TO_PIXEL); //for physics world
		// debugRenderer = new Box2DDebugRenderer();
		debugRenderer = new SBox2DDebugRenderer( Util.BOX_TO_PIXEL );
		Gdx.app.setLogLevel( Application.LOG_DEBUG );

		MCL = new MyContactListener( );
		world.setContactListener( MCL );
	}

	@Override
	public void render( float deltaTime ) {
		Gdx.gl10.glClearColor( 0.0f, 0f, 0.0f, 1.0f );
		Gdx.gl10.glClear( GL20.GL_COLOR_BUFFER_BIT );

		// float deltaTime = Gdx.graphics.getDeltaTime( );

		inputHandler.update( );
		cam.update( );

		if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) ) {
			ScreenManager.getInstance( ).show( ScreenType.PAUSE );
		}
		if ( Gdx.input.isKeyPressed( Keys.P ) ) {
			System.exit( 0 );
		}
		if ( Gdx.input.isKeyPressed( Input.Keys.M ) ) {
			ScreenManager.getInstance( ).show( ScreenType.PHYSICS );
		}
		// testing the swapping of animations
		if ( Gdx.input.isKeyPressed( Input.Keys.B ) ) {
			if ( animatingEntity.sprite == animatedSprite )
				animatingEntity.changeSprite( anotherAnimatedSprite );
			else {
				( ( AnimatedSprite ) animatingEntity.sprite ).reset( );
				animatingEntity.changeSprite( animatedSprite );
			}
		}

		rootSkeleton.update( deltaTime );
		player.update( deltaTime );

		structScrew.update( deltaTime );

		if ( inputHandler.unscrewPressed( ) ) {
			structScrew.screwLeft( );
		}

		batch.setProjectionMatrix( cam.combined( ) );
		// batch.setProjectionMatrix(camera.combined);
		batch.begin( );

		// sprite.draw(batch);
		// Drawing the player here
		// playerEntity.draw(batch);
		// player.draw(batch);

		rootSkeleton.draw( batch );
		// tp.draw( batch );
		// cp.draw( batch );
		// ground.draw( batch );
		player.draw( batch );

		structScrew.draw( batch );
		animatingEntity.draw( batch );
		batch.end( );

		// logger.log();
		debugRenderer.render( world, cam.combined( ) );

		world.step( 1 / 60f, 6, 2 ); // step our physics calculations
		// Gdx.app.debug("Physics",
		// "delta = "+Gdx.app.getGraphics().getDeltaTime());
	}

	@Override
	public void resize( int width, int height ) {
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

}
