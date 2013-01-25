package com.blindtigergames.werescrewed.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.blindtigergames.werescrewed.camera.Camera;
import com.blindtigergames.werescrewed.debug.SBox2DDebugRenderer;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.Player;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.mover.PistonMover;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.SlidingMotorMover;
import com.blindtigergames.werescrewed.input.InputHandlerPlayer1;
import com.blindtigergames.werescrewed.joint.JointFactory;
import com.blindtigergames.werescrewed.joint.PrismaticJointBuilder;
import com.blindtigergames.werescrewed.platforms.PlatformBuilder;
import com.blindtigergames.werescrewed.platforms.ShapePlatform;
import com.blindtigergames.werescrewed.platforms.Shapes;
import com.blindtigergames.werescrewed.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.screws.StructureScrew;

/**
 * Screen to test out moving platforms and skeletons
 * 
 * Debug Keys in use:
 * z - move skeleton down
 * x - move skeleton up
 * c - rotate skeleton left
 * v - rotate skeleton right
 * 
 * @author stew
 *
 */
public class IMoverGameScreen implements com.badlogic.gdx.Screen {
    
    /***
     * Box2D to pixels conversion *************
     * 
     * This number means 1 meter equals 256 pixels. That means the biggest
     * in-game object (10 meters) we can use is 2560 pixels wide, which is much
     * bigger than our max screen resolution so it should be enough.
     */
    public static final float BOX_TO_PIXEL = 256f;
    public static final float PIXEL_TO_BOX = 1 / BOX_TO_PIXEL;
    public static final float DEGTORAD = 0.0174532925199432957f;
    public static final float RADTODEG = 57.295779513082320876f;

    OrthographicCamera camera;
    Camera cam;
    SpriteBatch batch;
    Texture texture;
    Texture playerTexture;
    Sprite sprite;
    World world;
    SBox2DDebugRenderer debugRenderer;
    Body playerBody;
    Entity playerEntity;
    Player player;
    TiledPlatform tp, tp2, slidingPlatform;
    // ComplexPlatform cp;
    Skeleton skeleton;
    ShapePlatform sp;
    TiledPlatform piston;

    ArrayList<Body> platforms;

    FPSLogger logger;

    Texture screwTex;
    StructureScrew structScrew;
    InputHandlerPlayer1 inputHandler;

    public IMoverGameScreen() {

        System.out.println( "GameScreen starting" );
        float zoom = 1.0f;
        float w = Gdx.graphics.getWidth() / zoom;
        float h = Gdx.graphics.getHeight() / zoom;

        
        
        inputHandler = new InputHandlerPlayer1();
        texture = new Texture( Gdx.files.internal( "data/rletter.png" ) );
        // takes in width, height
        // cam = new Camera(w, h);
        batch = new SpriteBatch();

        world = new World( new Vector2( 0, -100 ), true );
        // mcl = new MyContactListener();
        // world.setContactListener(mcl);
        String name = "player";

        player = new Player( world, new Vector2( -2.0f, 1.0f ), name );
        cam = new Camera( w, h, player );

        skeleton = new Skeleton( "skeleton1", new Vector2(), null, world );
        
        tp = new TiledPlatform( "plat", new Vector2( 370.0f, 200.0f ), texture,
                10, 1, false, world );
        // cp = new ComplexPlatform( "bottle", new Vector2(0.0f, 3.0f), texture,
        // 1, world, "bottle" );
        sp = new ShapePlatform( "rhom", new Vector2( 1.0f, 1.0f ), texture,
                world, Shapes.rhombus, 1.0f, 1, false );

        screwTex = new Texture( Gdx.files.internal( "data/screw.png" ) );
        structScrew = new StructureScrew( "", sp.body.getPosition(), screwTex,
                25, sp, skeleton, world );

        // tp = new TiledPlatform("plat", new Vector2(200.0f, 100.0f), null, 1,
        // 2, world);
        // tp.setMover(new TimelineMover());
        // BOX_TO_PIXEL, PIXEL_TO_BOX
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set( new Vector2( 0 * PIXEL_TO_BOX,
                0 * PIXEL_TO_BOX ) );
        Body groundBody = world.createBody( groundBodyDef );
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox( Gdx.graphics.getWidth() * PIXEL_TO_BOX,
                1f * PIXEL_TO_BOX );
        groundBody.createFixture( groundBox, 0.0f );
        groundBody.getFixtureList().get( 0 ).setFriction( 0.5f );

        // make sure you uncomment the next two lines debugRenderer = new
        // SBox2DDebugRenderer(BOX_TO_PIXEL); for physics world
        // debugRenderer = new Box2DDebugRenderer();
        debugRenderer = new SBox2DDebugRenderer( BOX_TO_PIXEL );
        Gdx.app.setLogLevel( Application.LOG_DEBUG );

        logger = new FPSLogger();

        //slidingPlatform = new TiledPlatform( "prismaticplat", new Vector2(
        //        -300.0f*PIXEL_TO_BOX, 200.0f*PIXEL_TO_BOX ), null, 10, 1, false, world );
        slidingPlatform = new PlatformBuilder( world )
        					.setWidth( 10 )
        					.setHeight( 1 )
        					.setName( "sliding" )
        					.setOneSided( true )
        					.setPosition( -300*PIXEL_TO_BOX, 200*PIXEL_TO_BOX )
        					.buildTilePlatform( );
        slidingPlatform.body.setType( BodyType.DynamicBody );

        
        // skeleton.mover = new TimelineMover();
        platforms = new ArrayList<Body>();

        
        PrismaticJointDef prismaticJointDef = JointFactory
                .constructSlidingJointDef( skeleton.body, slidingPlatform.body,
                        slidingPlatform.body.getWorldCenter(), new Vector2( 1,
                                0 ), 1.0f, 1f );
        PrismaticJoint j = (PrismaticJoint) world
                .createJoint( prismaticJointDef );
        skeleton.addBoneAndJoint( slidingPlatform, j );
        slidingPlatform.setMover( new SlidingMotorMover(
                PuzzleType.PRISMATIC_SLIDER, j ) );
        
        
        TiledPlatform skeletonTest1 = new PlatformBuilder()
										.setWidth( 10 )
										.setHeight( 1 )
										.setOneSided( false )
										.setPosition( -300*PIXEL_TO_BOX, -200*PIXEL_TO_BOX )
										.setTexture( texture )
										.buildTilePlatform( world );
        			
        skeletonTest1.body.setType( BodyType.DynamicBody );
        skeleton.addPlatformFixed( skeletonTest1 );
        
        TiledPlatform skeletonTest2 = new PlatformBuilder()
										.setWidth( 10 )
										.setHeight( 1 )
										.setOneSided( false )
										.setPosition( 300*PIXEL_TO_BOX, 300*PIXEL_TO_BOX )
										.setTexture( texture )
										.buildTilePlatform( world );
        skeletonTest2.body.setType( BodyType.DynamicBody );
        skeleton.addPlatformRotatingCenter( skeletonTest2 );

        /*
         * TODO: FIX PLATFORM DENSITY
         */
        
        for ( int i = 0; i < 10; ++i ){
            TiledPlatform piston = new PlatformBuilder()
									.setWidth( 1 )
									.setHeight( 3 )
									.setOneSided( false )
									.setPosition( (-500f-i*40)*PIXEL_TO_BOX, 150f*PIXEL_TO_BOX )
									.setTexture( texture )
									.buildTilePlatform( world );

            piston.body.setType( BodyType.DynamicBody );
            PrismaticJoint pistonJoint = new PrismaticJointBuilder( world )
                                        .skeleton( skeleton )
                                        .bodyB( (Entity)piston )
                                        .anchor( piston.body.getWorldCenter() )
                                        .axis( 0, 1 )
                                        .motor( true )
                                        .limit( true )
                                        .upper( 1 )
                                        .motorSpeed( 1 )
                                        .build();
            //Something is still not quite right with this, try replacing 3 with 0.
            piston.setMover( new PistonMover( pistonJoint, 0f, i * 1.0f / 10 ) );
            
            skeleton.addBoneAndJoint( piston, pistonJoint );
            
        }

    }

    @Override
    public void render( float delta ) {
        Gdx.gl20.glClearColor( 0.0f, 0f, 0.0f, 1.0f );
        Gdx.gl20.glClear( GL20.GL_COLOR_BUFFER_BIT );

        float deltaTime = Gdx.graphics.getDeltaTime();

        inputHandler.update();
        cam.update();

        if ( Gdx.input.isKeyPressed( Input.Keys.ESCAPE ) ) {
            ScreenManager.getInstance().show( Screen.PAUSE );
        }
        if ( Gdx.input.isKeyPressed( Keys.P ) ) {
            System.exit( 0 );
        }

        if ( Gdx.input.isKeyPressed( Input.Keys.X ) ) {
            skeleton.body.setTransform( skeleton.body.getTransform()
                    .getPosition().add( 0f, 0.01f ), skeleton.body
                    .getTransform().getRotation() );
            skeleton.wakeSkeleton();
            // groundBody.setTransform(0f, -0.01f, 0);
            // Gdx.app.log("dude", "DUDE!");

        }

       /* if ( Gdx.input.isKeyPressed( Input.Keys.Z ) ) {
            // groundBody.setTransform(0f, 0.01f, 0);
            skeleton.body.setTransform( skeleton.body.getTransform()
                    .getPosition().add( 0f, -0.01f ), skeleton.body
                    .getTransform().getRotation() );
            skeleton.wakeSkeleton();
        }*/

        if ( Gdx.input.isKeyPressed( Input.Keys.C ) ) {
            skeleton.body.setTransform( skeleton.body.getTransform()
                    .getPosition(),
                    skeleton.body.getTransform().getRotation() + 0.01f );
            // groundBody.setTransform(0f, -0.01f, 0);
            // Gdx.app.log("dude", "DUDE!");
            skeleton.wakeSkeleton();
        }

        if ( Gdx.input.isKeyPressed( Input.Keys.V ) ) {
            // groundBody.setTransform(0f, 0.01f, 0);
            skeleton.body.setTransform( skeleton.body.getTransform()
                    .getPosition(),
                    skeleton.body.getTransform().getRotation() - 0.01f );
            // Gdx.app.log("dude", "DUDE!");
            skeleton.wakeSkeleton();
        }

        if ( inputHandler.screwPressed(  ) ) {
            /*
             * for (Fixture f: structScrew.body.getFixtureList()){
             * f.contactListener(); }
             */
            // if(inputHandler.leftPressed( player_t.ONE )){
            structScrew.screwLeft();
            // }
        }

        player.update( deltaTime );
        skeleton.update( deltaTime );
        tp.update( deltaTime );
        // cp.update();
        sp.update( deltaTime );
        

        batch.setProjectionMatrix( cam.combined() );
        // batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // test drawing the texture by uncommenting the next line:
        tp.draw( batch );
        slidingPlatform.draw( batch );
        player.draw( batch );
        structScrew.draw( batch );

        // test drawing the texture by uncommenting the next line:
        // tp.draw(batch);
        player.draw( batch );

        batch.end();

        // logger.log();
        debugRenderer.render( world, cam.combined() );

        world.step( 1 / 60f, 6, 2 ); // step our physics calculations
        // Gdx.app.debug("Physics",
        // "delta = "+Gdx.app.getGraphics().getDeltaTime());
    }

    @Override
    public void resize( int width, int height ) {
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

}
