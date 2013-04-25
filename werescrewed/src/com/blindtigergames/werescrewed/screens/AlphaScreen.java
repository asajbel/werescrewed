package com.blindtigergames.werescrewed.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.RootSkeleton;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.action.DestoryPlatformJointAction;
import com.blindtigergames.werescrewed.entity.action.RemoveEntityAction;
import com.blindtigergames.werescrewed.entity.builders.EventTriggerBuilder;
import com.blindtigergames.werescrewed.entity.builders.GenericEntityBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.mover.AnalogRotateMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.mover.ParallaxMover;
import com.blindtigergames.werescrewed.entity.mover.PuzzleType;
import com.blindtigergames.werescrewed.entity.mover.RotateTweenMover;
import com.blindtigergames.werescrewed.entity.mover.TimelineTweenMover;
import com.blindtigergames.werescrewed.entity.mover.puzzle.PuzzleRotateTweenMover;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.entity.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.entity.screws.StrippedScrew;
import com.blindtigergames.werescrewed.entity.screws.StructureScrew;
import com.blindtigergames.werescrewed.entity.tween.PlatformAccessor;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.eventTrigger.PowerSwitch;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
import com.blindtigergames.werescrewed.joint.PrismaticJointBuilder;
import com.blindtigergames.werescrewed.joint.RevoluteJointBuilder;
import com.blindtigergames.werescrewed.level.CharacterSelect;
import com.blindtigergames.werescrewed.level.LevelFactory;
import com.blindtigergames.werescrewed.util.Util;

public class AlphaScreen extends Screen {

	public ScreenType screenType;

	private CharacterSelect characterSelect;
	private Screw powerScrew1, powerScrew2, powerScrew3, powerScrew4;
	private Skeleton footSkeleton, kneeSkeleton, thighSkeleton, hipSkeleton,
			chestSkeleton, leftShoulderSkeleton;
	private TiledPlatform kneeMovingPlat;

	Platform leftShoulderSideHatch;
	private PuzzleScrew leftArmScrew, rightElbowPuzzleScrew;

	private boolean etTriggered = false;

	private Skeleton rightShoulderSkeleton;

	private Platform rightArmDoor;

	private StrippedScrew rightArmDoorHinge;

	public AlphaScreen( ) {
		super( );

		setClearColor( 79.0f / 255.0f, 82.0f / 255.0f, 104.0f / 255.0f, 1.0f ); // purple-ish

		String filename = "data/levels/alphalevel.xml";
		level = new LevelFactory( ).load( filename );

		// level.camera.position = new Vector3( 0, 0, 0 );

		// death barrier
		EventTriggerBuilder etb = new EventTriggerBuilder( level.world );
		EventTrigger removeTrigger = etb.name( "removeEntity" ).rectangle( )
				.width( 10 ).height( 50000 ).position( new Vector2( 0, -3200 ) )
				.beginAction( new RemoveEntityAction( ) ).build( );
		removeTrigger.setCategoryMask( Util.CATEGORY_PLAYER,
				Util.CATEGORY_EVERYTHING );
		level.root.addEventTrigger( removeTrigger );

		characterSelect = new CharacterSelect( level );

		createFootObjects( );
		createKneeObjects( );
		thighDecals( thighSkeleton );// createFootObjects initializes
										// thighSkeleton

		// start: 512, 256
		// power screws: -700f, 1800f
		// chest entrance : -200f, 3800f
		// upper chest: 1300f, 6000f
		// rope on left side of the robot <- -950f, 5100f
		// top left: -1582f, 6150f <<<< side
		// head: 480f, 6688f
		// right arm: 2600f, 6000f >>>> side
		//left side hand <- -2224, 3008
		
		Vector2 spawnPos = new Vector2(512, 256);

		if ( level.player1 == null ) {
			level.player1 = new PlayerBuilder( ).world( level.world )
					.position( spawnPos.cpy( ) ).name( "player1" )
					.definition( "red_male" ).buildPlayer( );
			level.progressManager.addPlayerOne( level.player1 );
		}
		if ( level.player2 == null ) {
			level.player2 = new PlayerBuilder( ).world( level.world )
					.position( spawnPos.cpy( ) ).name( "player2" )
					.definition( "red_female" ).buildPlayer( );
			level.progressManager.addPlayerTwo( level.player2 );
		}

		// background stuff
		level.backgroundBatch = new SpriteBatch( );
		level.backgroundRootSkeleton = new RootSkeleton( "backgroundroot",
				Vector2.Zero, null, level.world );
		float width = Gdx.graphics.getWidth( ) / 1f;
		float height = Gdx.graphics.getHeight( ) / 1f;
		level.backgroundCam = new OrthographicCamera( 1, width / height );
		level.backgroundCam.viewportWidth = width;
		level.backgroundCam.viewportHeight = height;
		level.backgroundCam.position.set( width * .5f, height * .5f, 0f );
		level.backgroundCam.update( );

		chestObjects( );
		leftArm( );
		rightArm( );
		knee2Objects( );
		buildBackground( );
		initPowerScrews( );

		buildEngineHeart( new Vector2( 0, 5450 ) );
		createChestDecals();
		powerSwitch();
	}

	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );

		// characterSelect.update( );

		// characterSelect.draw( batch, deltaTime );

		powerScrewupdate( );

	}

	private void buildBackground( ) {
		Skeleton bgSkele;
		bgSkele = ( Skeleton ) LevelFactory.entities.get( "stageSkeleton" );
		Skeleton light_skel = new Skeleton( "light_skeleton", new Vector2(), null, level.world );
		//level.skelBGList.put( key, value )
		TextureAtlas floor_seats = WereScrewedGame.manager
				.getAtlas( "alphabot_floor_seats" );
		TextureAtlas stage_pillar = WereScrewedGame.manager
				.getAtlas( "stage_pillar" );
		TextureAtlas stage_upperleft = WereScrewedGame.manager
				.getAtlas( "stage_upperleft" );
		TextureAtlas stage_upperright = WereScrewedGame.manager
				.getAtlas( "stage_upperright" );
		TextureAtlas stage_light = WereScrewedGame.manager
				.getAtlas( "stage_light" );
		TextureAtlas support_left = WereScrewedGame.manager
				.getAtlas( "support_left" );
		TextureAtlas support_middle_right = WereScrewedGame.manager
				.getAtlas( "support_middle_right" );
		TextureAtlas curtains = WereScrewedGame.manager.getAtlas( "curtains" );
		int numDomes = 10;
		TextureAtlas[ ] dome = new TextureAtlas[ numDomes ];
		for ( int i = 1; i <= numDomes; ++i ) {
			dome[ i - 1 ] = WereScrewedGame.manager.getAtlas( "dome" + i );
		}

		int max = 2030;
		int offsetX = 200;
		int offsetY = 0;
		int floorY = -199 + offsetY;
		int seatsY = -583 + offsetY;
		int seatsX = -1180 + offsetX;// -1180
		int floorX = -max + offsetX;
		int stage_pillarY = -202 + offsetY;
		int stage_pillarX = floorX - 530;
		int lightX = offsetX - 1970;
		int lightY = offsetY + 40;

		int domeSliceX = 1234 * 2;
		int domeSliceY = 1638;

		int supportY = 6500 + offsetY;
		int supportX = -max + seatsX;

		int curtainX = seatsX - max + 1230;
		int curtainY = seatsY + 585;

		// support beam
		bgSkele.addBGDecalBack( support_left.createSprite( "support_left" ),
				new Vector2( supportX, supportY ) );
		bgSkele.addBGDecalBack(
				support_middle_right.createSprite( "support_middle" ),
				new Vector2( supportX + max, supportY + 216 ) );
		bgSkele.addBGDecalBack(
				support_middle_right.createSprite( "support_right" ),
				new Vector2( supportX + 2 * max, supportY ) );

		// lights
		bgSkele.addBGDecal( stage_light.createSprite( "light_left" ),
				new Vector2( lightX, lightY ) );
		bgSkele.addBGDecal( stage_light.createSprite( "light_right" ),
				new Vector2( lightX + 2030, lightY ) );

		// floor
		bgSkele.addBGDecal( floor_seats.createSprite( "floor_left" ),
				new Vector2( floorX, floorY ) );
		bgSkele.addBGDecal( floor_seats.createSprite( "floor_right" ),
				new Vector2( floorX + max, floorY ) );

		// curtains
		bgSkele.addFGDecal( curtains.createSprite( "curtains_bottom_left" ),
				new Vector2( curtainX, curtainY ) );
		bgSkele.addFGDecal( curtains.createSprite( "curtains_top_left" ),
				new Vector2( curtainX, curtainY + 830 ) );
		bgSkele.addFGDecal( curtains.createSprite( "curtains_middle" ),
				new Vector2( curtainX + 304, curtainY + 1011 ) );
		bgSkele.addFGDecal( curtains.createSprite( "curtains_top_right" ),
				new Vector2( curtainX + 2333, curtainY + 834 ) );
		bgSkele.addFGDecal( curtains.createSprite( "curtains_bottom_right" ),
				new Vector2( curtainX + 2398, curtainY ) );

		// stage is in between floor & seats
		bgSkele.addFGDecal( stage_pillar.createSprite( "stage_left" ),
				new Vector2( stage_pillarX, stage_pillarY ) );
		bgSkele.addFGDecal( stage_upperleft.createSprite( "stage_upperleft" ),
				new Vector2( stage_pillarX + 2, 1647 + stage_pillarY ) );// 1647
																			// is
																			// height
																			// of
																			// left
																			// pillar

		bgSkele.addFGDecal( stage_pillar.createSprite( "stage_right" ),
				new Vector2( stage_pillarX + 3204, stage_pillarY ) );// 3204 is
																		// difference
																		// between
																		// left
																		// &
																		// right
		bgSkele.addFGDecal(
				stage_upperright.createSprite( "stage_upperright" ),
				new Vector2( stage_pillarX + 2004, stage_pillarY + 1616 ) );// 1617

		// works
		// seats
		bgSkele.addFGDecal( floor_seats.createSprite( "seats_left" ),
				new Vector2( -max + seatsX, seatsY ) );
		bgSkele.addFGDecal( floor_seats.createSprite( "seats_middle" ),
				new Vector2( 0 + seatsX, seatsY ) );
		bgSkele.addFGDecal( floor_seats.createSprite( "seats_right" ),
				new Vector2( max + seatsX, seatsY ) );

		addBackGroundEntity( bgSkele );
		addForeGroundEntity( bgSkele );

		// initBackground( dome, numDomes, domeSliceX, domeSliceY,
		// 100,100);//-max + seatsX, seatsY );
		initParallaxBackground( );

	}

	private void initParallaxBackground( ) {
		TextureAtlas dome = WereScrewedGame.manager.getAtlas( "dome_small" );
		BodyDef screwBodyDef;
		Body body;
		CircleShape screwShape;
		FixtureDef screwFixture;
		Entity e1;

		screwBodyDef = new BodyDef( );
		screwBodyDef.type = BodyType.KinematicBody;
		Vector2 bodyPos = new Vector2( 0, -1028 );
		screwBodyDef.position.set( bodyPos.cpy( ).mul( Util.PIXEL_TO_BOX ) );
		screwBodyDef.fixedRotation = true;
		body = level.world.createBody( screwBodyDef );
		screwShape = new CircleShape( );
		screwShape.setRadius( 64 * Util.PIXEL_TO_BOX );
		screwFixture = new FixtureDef( );
		screwFixture.filter.categoryBits = Util.CATEGORY_IGNORE;
		screwFixture.filter.maskBits = Util.CATEGORY_NOTHING;
		screwFixture.shape = screwShape;
		screwFixture.isSensor = true;
		body.createFixture( screwFixture );
		body.setUserData( this );
		screwShape.dispose( );

		e1 = new Entity( "bg_1", bodyPos, null, body, false );
		e1.sprite = dome.createSprite( "dome_small" );
		e1.setMoverAtCurrentState( new ParallaxMover( new Vector2( e1
				.getPositionPixel( ) ), new Vector2( e1.getPositionPixel( )
				.add( 0f, 2048f ) ), 0.00002f, .5f, level.camera, false,
				LinearAxis.VERTICAL ) );
		level.backgroundRootSkeleton.addLooseEntity( e1 );
	}

	// Called by BuildBackground()
	private void initBackground( TextureAtlas[ ] dome, int numDomes,
			int domeSliceX, int domeSliceY, int startX, int startY ) {

		BodyDef screwBodyDef;
		Body[ ] body = new Body[ 2 ];
		CircleShape screwShape;
		FixtureDef screwFixture;
		Entity e1, e2;
		Vector2 pos;
		Vector2 offset = new Vector2( -4000, -400 );
		for ( int i = numDomes; i > 0; --i ) {
			int yStep = ( int ) ( ( 10 - i ) / 2 );
			pos = new Vector2( startX, startY + domeSliceY * yStep );
			int flipX = 4;
			if ( i % 2 == 0 ) {// even
				pos.x += domeSliceX;
				flipX = 2;
			}
			Sprite a = dome[ i - 1 ].createSprite( "dome" + i );
			a.setScale( 2, 1 );
			Sprite b = dome[ i - 1 ].createSprite( "dome" + i );
			b.setScale( -2, 1 );

			for ( int j = 0; j < 2; ++j ) {
				screwBodyDef = new BodyDef( );
				screwBodyDef.type = BodyType.KinematicBody;
				Vector2 bodyPos = pos.cpy( );
				if ( j == 1 )
					bodyPos.add( flipX * domeSliceX, 0 );
				screwBodyDef.position.set( bodyPos );
				screwBodyDef.fixedRotation = true;
				body[ j ] = level.world.createBody( screwBodyDef );
				screwShape = new CircleShape( );
				screwShape.setRadius( 64 * Util.PIXEL_TO_BOX );
				screwFixture = new FixtureDef( );
				screwFixture.filter.categoryBits = Util.CATEGORY_IGNORE;
				screwFixture.filter.maskBits = Util.CATEGORY_NOTHING;
				screwFixture.shape = screwShape;
				screwFixture.isSensor = true;
				body[ j ].createFixture( screwFixture );
				body[ j ].setUserData( this );
				screwShape.dispose( );
			}
			// the position of each entity and sprite is set at this point.
			e1 = new Entity( "bg_1_" + i, pos, null, body[ 0 ], false );
			e1.sprite = a;
			e1.setMoverAtCurrentState( new ParallaxMover( new Vector2( e1
					.getPositionPixel( ) ), new Vector2( e1.getPositionPixel( )
					.add( 0f, 512f ) ), 0.0002f, .5f, level.camera, false,
					LinearAxis.VERTICAL ) );
			level.backgroundRootSkeleton.addLooseEntity( e1 );

			e2 = new Entity( "bg_2_" + i, pos.cpy( )
					.add( flipX * domeSliceX, 0 ), null, body[ 1 ], false );
			e2.sprite = b;
			e2.setMoverAtCurrentState( new ParallaxMover( new Vector2( e2
					.getPositionPixel( ) ), new Vector2( e2.getPositionPixel( )
					.add( 0f, 512f ) ), 0.0002f, .5f, level.camera, false,
					LinearAxis.VERTICAL ) );
			level.backgroundRootSkeleton.addLooseEntity( e2 );

			// pos.add(offset);
			// level.backgroundRootSkeleton.addBGDecal( a, pos );
			// level.backgroundRootSkeleton.addBGDecal(b, pos.cpy().add( flipX *
			// domeSliceX, 0 ));
		}

		/*
		 * Entity bg_1_0 = new Entity( "bg_1_0", new Vector2( 0, 720 ), null,
		 * null, false ); Entity bg_1_1 = new Entity( "bg_1_1", new Vector2( 0,
		 * 720 ), null, null, false ); for ( int i = 0; i < 2; i++ ) {
		 * screwBodyDef = new BodyDef( ); screwBodyDef.type =
		 * BodyType.KinematicBody; screwBodyDef.position.set( 0, 0 );
		 * screwBodyDef.fixedRotation = true; body = level.world.createBody(
		 * screwBodyDef ); screwShape = new CircleShape( );
		 * screwShape.setRadius( 64 * Util.PIXEL_TO_BOX ); screwFixture = new
		 * FixtureDef( ); screwFixture.filter.categoryBits =
		 * Util.CATEGORY_IGNORE; screwFixture.filter.maskBits =
		 * Util.CATEGORY_NOTHING; screwFixture.shape = screwShape;
		 * screwFixture.isSensor = true; body.createFixture( screwFixture );
		 * body.setUserData( this ); switch ( i ) { case 0: bg_1_0 = new Entity(
		 * "bg_1_0", new Vector2( 0, 720 ), null, body, false ); break; case 1:
		 * bg_1_1 = new Entity( "bg_1_1", new Vector2( 0, 720 ), null, body,
		 * false ); break; } } TextureRegion tex1 =
		 * WereScrewedGame.manager.getAtlas( "bgCloud" ) .findRegion( "bgCloud1"
		 * ); TextureRegion tex2 = WereScrewedGame.manager.getAtlas( "bgCloud" )
		 * .findRegion( "bgCloud2" ); bg_1_0.sprite = bg_1_0.constructSprite(
		 * tex1 ); bg_1_0.sprite.setOrigin( bg_1_0.sprite.getWidth( ) / 4.0f,
		 * bg_1_0.sprite.getHeight( ) / 2.0f ); bg_1_0.offset = new Vector2(
		 * bg_1_0.sprite.getOriginX( ), bg_1_0.sprite.getOriginY( ) ); //
		 * bg_1_0.sprite.setScale( 1.05f, 1.1f ); bg_1_0.setMoverAtCurrentState(
		 * new ParallaxMover( new Vector2( 512, 1530 ), new Vector2( 512, -512
		 * ), 0.0002f, .5f, level.camera, false, LinearAxis.VERTICAL ) );
		 * bg_1_1.sprite = bg_1_1.constructSprite( tex2 );
		 * bg_1_1.sprite.setOrigin( bg_1_1.sprite.getWidth( ) / 4.0f,
		 * bg_1_1.sprite.getHeight( ) / 2.0f ); bg_1_1.offset = new Vector2(
		 * bg_1_1.sprite.getOriginX( ), bg_1_1.sprite.getOriginY( ) ); //
		 * bg_1_1.sprite.setScale( 1.05f, 1.1f ); bg_1_1.setMoverAtCurrentState(
		 * new ParallaxMover( new Vector2( 512, 1530 ), new Vector2( 512, -512
		 * ), 0.0002f, 1f, level.camera, false, LinearAxis.VERTICAL ) );
		 * level.backgroundRootSkeleton.addLooseEntity( bg_1_0 );
		 * level.backgroundRootSkeleton.addLooseEntity( bg_1_1 );
		 */
	}

	private void thighDecals( Skeleton thighSkeleton ) {
		TextureAtlas decals = WereScrewedGame.manager
				.getAtlas( "alphabot_thigh_decal" );
		// level.entityBGList.add(thighSkeleton);
		thighSkeleton.addBGDecalBack(
				decals.createSprite( "thigh_mechanisms_and_pipesNOCOLOR" ),
				new Vector2( -425, -1117 ) );
		// 380,1117
	}

	private void createFootObjects( ) {
		TextureAtlas decals = WereScrewedGame.manager
				.getAtlas( "alphabot_foot_shin_decal" );

		footSkeleton = ( Skeleton ) LevelFactory.entities.get( "footSkeleton" );
		footSkeleton.setFgFade( true );

		kneeSkeleton = ( Skeleton ) LevelFactory.entities.get( "kneeSkeleton" );

		hipSkeleton = ( Skeleton ) LevelFactory.entities.get( "hipSkeleton" );

		thighSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "thighSkeleton" );

		footSkeleton.body.setType( BodyType.KinematicBody );
		kneeSkeleton.body.setType( BodyType.KinematicBody );
		thighSkeleton.body.setType( BodyType.KinematicBody );

		TiledPlatform structurePlat3 = ( TiledPlatform ) LevelFactory.entities
				.get( "structurePlat3" );
		TiledPlatform pivotPlat1 = ( TiledPlatform ) LevelFactory.entities
				.get( "pivotPlat1" );
		TiledPlatform footPlat6 = ( TiledPlatform ) LevelFactory.entities
				.get( "footPlat6" );

		// RevoluteJointDef rjd = new RevoluteJointDef( );
		// rjd.initialize( structurePlat3.body, pivotPlat1.body, pivotPlat1
		// .getPosition( ).add( pivotPlat1.getMeterWidth( ) / 2, 0 ) );
		// rjd.collideConnected = false;
		// level.world.createJoint( rjd );

		// structurePlat3.setGroupIndex( ( short ) -5 );
		footPlat6.setGroupIndex( ( short ) -5 );

		// DECALS for Foot / Shin
		int decalX = -648;// -482;//587
		int decalY = -654;// -558;//536
		Sprite footBG = decals
				.createSprite( "foot_mechanisms_and_pipes_NOCOLOR" );
		Sprite legBG = decals.createSprite( "shin_pipes_NOCOLOR" );
		Skeleton foot = ( Skeleton ) LevelFactory.entities.get( "footSkeleton" );
		foot.addBGDecal( footBG, new Vector2( decalX, decalY ) );
		footBG.setOrigin( 0f, 0f );
		foot.addBGDecal( legBG, new Vector2( 400 + decalX, 424 + decalY ) );

		addBackGroundEntity( footSkeleton );
		addForeGroundEntity( footSkeleton );
		
		Vector2 footFGPos = new Vector2( decalX - 30, decalY + 10 );
		foot.addFGDecal( decals.createSprite( "foot_exterior" ), footFGPos );
		foot.addFGDecal( decals.createSprite( "shin_exterior" ),
				footFGPos.cpy( ).add( 400, 386 ) );

	}

	private void createKneeObjects( ) {
		TextureAtlas decals = WereScrewedGame.manager
				.getAtlas( "alphabot_foot_shin_decal" );
		TextureAtlas knee_exterior = WereScrewedGame.manager
				.getAtlas( "alphabot_knee_decal" );

		kneeMovingPlat = ( TiledPlatform ) LevelFactory.entities
				.get( "kneeMovingPlat" );
		kneeMovingPlat.setActive( false );

		kneeSkeleton = ( Skeleton ) LevelFactory.entities.get( "kneeSkeleton" );

		Vector2 kneeDecalPos = kneeSkeleton.getPositionPixel( )
				.add( 230, -2339 ); // this is horrible I know know why knee is
									// here even
		kneeSkeleton.addFGDecalBack( decals.createSprite( "knee_exterior" ),
				kneeDecalPos.cpy( ) );
		addForeGroundEntity( kneeSkeleton );

		kneeSkeleton
				.addBGDecalBack( knee_exterior
						.createSprite( "knee_mechanisms_and_pipesNOCOLOR" ),
						kneeDecalPos.cpy( ) );
		// removePlayerToScrew( )
	}

	private void initPowerScrews( ) {

		powerScrew1 = ( Screw ) LevelFactory.entities.get( "powerScrew1" );
		powerScrew2 = ( Screw ) LevelFactory.entities.get( "powerScrew2" );

		powerScrew3 = ( Screw ) LevelFactory.entities.get( "powerScrew3" );
		powerScrew4 = ( Screw ) LevelFactory.entities.get( "powerScrew4" );
	}

	private void powerScrewupdate( ) {

		if ( ( powerScrew1.getDepth( ) == powerScrew1.getMaxDepth( ) )
				&& ( powerScrew2.getDepth( ) == powerScrew2.getMaxDepth( ) ) ) {
			kneeMovingPlat.setActive( true );

			footSkeleton.body.setType( BodyType.KinematicBody );
			kneeSkeleton.body.setType( BodyType.KinematicBody );
			thighSkeleton.body.setType( BodyType.KinematicBody );

			if ( hipSkeleton.currentMover( ) == null ) {
				hipSkeleton.addMover( new RotateTweenMover( hipSkeleton, 3f,
						-Util.PI / 45, 1f, true ), RobotState.IDLE );

				// PathBuilder pb = new PathBuilder( );
				// hipSkeleton.addMover( pb.begin( hipSkeleton )
				// .target( 0, 100, 3 ).delay( 1 ).target( 0, -25, 3 )
				// .target( 0, 0, 3 ).build( ), RobotState.IDLE );

			}

		}

		if ( leftArmScrew.getDepth( ) == leftArmScrew.getMaxDepth( )
				&& leftShoulderSkeleton.currentMover( ) == null ) {

			Timeline t = Timeline.createSequence( );

			t.push( Tween
					.to( leftShoulderSkeleton, PlatformAccessor.LOCAL_ROT, 20f )
					.ease( TweenEquations.easeInOutQuad )
					.target( ( -Util.PI / 2 ) ).delay( 1f ).start( ) );

			leftShoulderSkeleton
					.addMover( new TimelineTweenMover( t.start( ) ) );
		}

		// if ( ( powerScrew3.getDepth( ) == powerScrew3.getMaxDepth( ) )
		// && ( powerScrew4.getDepth( ) == powerScrew4.getMaxDepth( ) ) ) {
		//
		// }

		if ( rightElbowPuzzleScrew.getDepth( ) == rightElbowPuzzleScrew
				.getMaxDepth( ) ) {
			Skeleton rightElbowSkeleton = ( Skeleton ) LevelFactory.entities
					.get( "rightElbowSkeleton" );
			Skeleton rightShoulderSkeleton = ( Skeleton ) LevelFactory.entities
					.get( "rightShoulderSkeleton" );

			if ( rightElbowSkeleton.currentMover( ) == null
					&& rightShoulderSkeleton.currentMover( ) == null ) {

				Timeline t = Timeline.createSequence( );

				t.push( Tween
						.to( rightElbowSkeleton, PlatformAccessor.LOCAL_ROT,
								10f ).ease( TweenEquations.easeInOutQuad )
						.target( ( Util.PI / 2 ) ).delay( 0f ).start( ) );

				rightElbowSkeleton
						.addMover( new TimelineTweenMover( t.start( ) ) );

				Timeline t2 = Timeline.createSequence( );

				t2.push( Tween
						.to( rightShoulderSkeleton, PlatformAccessor.LOCAL_ROT,
								10f ).ease( TweenEquations.easeInOutQuad )
						.target( ( Util.PI / 2 ) ).delay( 13f ).start( ) );

				rightShoulderSkeleton.addMover( new TimelineTweenMover( t2
						.start( ) ) );

			}
		}

	}

	private void knee2Objects( ) {

		PuzzleRotateTweenMover m1 = new PuzzleRotateTweenMover( 2, Util.PI / 2,
				true, PuzzleType.ON_OFF_MOVER );
		PuzzleRotateTweenMover m2 = new PuzzleRotateTweenMover( 2,
				-Util.PI / 2, true, PuzzleType.ON_OFF_MOVER );

		PuzzleScrew knee2rotateScrew1 = ( PuzzleScrew ) LevelFactory.entities
				.get( "knee2rotateScrew1" );

		knee2rotateScrew1.puzzleManager.addMover( m1 );

		PuzzleScrew knee2rotateScrew2 = ( PuzzleScrew ) LevelFactory.entities
				.get( "knee2rotateScrew2" );

		knee2rotateScrew2.puzzleManager.addMover( m2 );

	}

	private void chestObjects( ) {
		chestSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "chestSkeleton" );
		PuzzleScrew chestScrew1 = ( PuzzleScrew ) LevelFactory.entities
				.get( "chestPuzzleScrew5" );
		PuzzleScrew chestScrew2 = ( PuzzleScrew ) LevelFactory.entities
				.get( "chestPuzzleScrew6" );

		PuzzleScrew chestScrew3 = ( PuzzleScrew ) LevelFactory.entities
				.get( "chestPuzzleScrew9" );
		PuzzleScrew chestScrew4 = ( PuzzleScrew ) LevelFactory.entities
				.get( "chestPuzzleScrew10" );

		TiledPlatform chestRotatingPlat2 = ( TiledPlatform ) LevelFactory.entities
				.get( "chestRotatePlat2" );

		AnalogRotateMover anlgRot = new AnalogRotateMover( .6f, level.world );

		RotateTweenMover rtm1 = new RotateTweenMover( chestRotatingPlat2, 8f,
				Util.PI, 2f, true );

		chestScrew3.puzzleManager.addMover( anlgRot );
		chestScrew4.puzzleManager.addMover( anlgRot );

		chestScrew3.puzzleManager.addScrew( chestScrew4 );
		chestScrew4.puzzleManager.addScrew( chestScrew3 );

		chestScrew1.puzzleManager.addScrew( chestScrew2 );
		chestScrew2.puzzleManager.addScrew( chestScrew1 );

		StructureScrew stuctureScrew1 = ( StructureScrew ) LevelFactory.entities
				.get( "structureScrew1" );

		EventTrigger etGearFall = ( EventTrigger ) LevelFactory.entities
				.get( "et1" );

	}
	
	private void createChestDecals(){
		TextureAtlas chest_powerscrew = WereScrewedGame.manager.getAtlas( "chest_powerscrew" );
		//Skeleton chestSkeleton = (Skeleton)LevelFactory.entities.get( "chestSkeleton" );
		//chestSkeleton.addBGDecal( 
		//		chest_powerscrew.createSprite( "chest_powerscrew_pipes_to_engineNOCOLOR" ), 
		//		new Vector2(-453,-970) );
	}

	private void leftArm( ) {
		leftArmScrew = ( PuzzleScrew ) LevelFactory.entities
				.get( "leftShoulderPuzzleScrew1" );

		leftShoulderSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "leftShoulderSkeleton" );

		leftShoulderSideHatch = ( Platform ) LevelFactory.entities
				.get( "leftShoulderTopHatch1" );

		RevoluteJointDef rjd = new RevoluteJointDef( );
		rjd.initialize( leftShoulderSideHatch.body, leftShoulderSkeleton.body,
				new Vector2( -1748, 5980 ).mul( Util.PIXEL_TO_BOX ) );
		level.world.createJoint( rjd );
	}

	private void rightArm( ) {
		rightShoulderSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "rightShoulderSkeleton" );

		rightArmDoor = ( Platform ) LevelFactory.entities
				.get( "rightShoulderTopHatch1" );

		// rightArmDoorHinge = ( StrippedScrew ) LevelFactory.entities
		// .get( "rightShoulderTopHatch1" );

		RevoluteJointDef rjd = new RevoluteJointDef( );
		rjd.initialize( rightArmDoor.body, rightShoulderSkeleton.body,
				new Vector2( 2850f, 5908f ).mul( Util.PIXEL_TO_BOX ) );
		level.world.createJoint( rjd );

		rightElbowPuzzleScrew = ( PuzzleScrew ) LevelFactory.entities
				.get( "rightElbowPuzzleScrew" );
	}

	private void buildEngineHeart( Vector2 posPix ) {
		Skeleton engineSkeleton = new Skeleton( "engineSkeleton", posPix, null,
				level.world );
		level.root.addSkeleton( engineSkeleton );
		int pistonDistanceApart = 280;
		float engineSpeed = 2.5f;

		// Got to set the right bits
		PlatformBuilder platBuilder = new PlatformBuilder( level.world );
		Platform chestEngine = platBuilder.name( "chestEngine" )
				.position( -200, 5100 ).texture( null ).type( "chestEngine" )
				.buildComplexPlatform( );
		chestEngine.setCategoryMask( Util.CATEGORY_PLATFORMS,
				Util.CATEGORY_PLAYER );

		engineSkeleton.addPlatform( chestEngine );

		TextureAtlas engineAtlas = WereScrewedGame.manager.getAtlas( "engine" );
		Vector2 decalPos = engineSkeleton.getPositionPixel( ).sub( posPix )
				.add( -230, -360 );
		engineSkeleton.addBGDecal( engineAtlas.createSprite( "chest-engine" ),
				new Vector2( decalPos ) );
		addForeGroundEntity( engineSkeleton );

		for ( int i = 0; i < 3; ++i ) {
			buildPiston( engineSkeleton, engineAtlas,
					posPix.cpy( ).add( pistonDistanceApart * i, 0 ), i,
					engineSpeed );
		}

	}

	private void buildPiston( Skeleton engineSkeleton,
			TextureAtlas engineAtlas, Vector2 posPix, int index,
			float engineSpeed ) {
		Vector2 posMeter = posPix.cpy( ).mul( Util.PIXEL_TO_BOX );
		// Build wheel
		Sprite wheelSprite = engineAtlas.createSprite( "wheel" );
		float radiusPix = wheelSprite.getWidth( ) / 2;
		float radiusMeter = radiusPix * Util.PIXEL_TO_BOX;
		Platform wheel1 = buildWheel( posPix.cpy( ), radiusMeter );
		// Attach wheel decal

		engineSkeleton.addPlatform( wheel1 );
		// Make wheel rotate
		new RevoluteJointBuilder( level.world ).entityA( engineSkeleton )
				.entityB( wheel1 ).motor( true ).motorSpeed( engineSpeed )
				.maxTorque( 5000 ).build( );

		// setup for building girder
		Sprite girderSprite = engineAtlas.createSprite( "girder0" );
		// girderSprite.scale( .8f );
		float girderInset = 0.97f;
		float pistonDistApartMetre = girderSprite.getHeight( ) * girderInset
				* Util.PIXEL_TO_BOX;
		// float pistonDistApartMetre = girderSprite.getHeight(
		// )*Util.PIXEL_TO_BOX;
		Sprite wheelBolt = engineAtlas.createSprite( "bolt0" );
		// Build GIRDER!
		float targetRadiusOnWheelMeter = ( radiusPix - wheelBolt.getHeight( ) / 3 )
				* Util.PIXEL_TO_BOX;
		boolean isDown = ( index % 2 == 0 );
		// wheel1.getPosition().add((wheelBoltOffset+wheelBolt.getWidth(
		// )/2)*Util.PIXEL_TO_BOX,0);

		Vector2 wheelJointPosMeter = new Vector2( posMeter );
		if ( isDown ) {
			wheelJointPosMeter.sub( 0, targetRadiusOnWheelMeter );
		} else {
			wheelJointPosMeter.add( 0, targetRadiusOnWheelMeter );
		}
		// Build girter!!
		Platform girder1 = buildGirder( girderSprite, wheelJointPosMeter,
				pistonDistApartMetre );
		engineSkeleton.addPlatform( girder1 );

		/*
		 * DistanceJointDef dJoint = new DistanceJointDef(); dJoint.initialize(
		 * wheel1.body, piston.body, wheelJointPosMeter, piston.getPosition( )
		 * ); dJoint.collideConnected = false; world.createJoint( dJoint );
		 */

		// Build piston!!
		Vector2 pistonJointPosMeter = wheelJointPosMeter.cpy( ).sub( 0,
				pistonDistApartMetre );
		PlatformBuilder pBuilder = new PlatformBuilder( level.world );
		TiledPlatform piston = pBuilder
				.position( pistonJointPosMeter.cpy( ).mul( Util.BOX_TO_PIXEL ) )
				.dynamic( ).dimensions( 4, 5 )// 3.71,4.75
				.buildTilePlatform( );
		piston.setCrushing( true );
		piston.setVisible( false );// only draw decals, not tiled body!
		engineSkeleton.addDynamicPlatform( piston );

		// Setup prismatic joint for piston!
		new PrismaticJointBuilder( level.world ).bodyA( engineSkeleton )
				.bodyB( piston ).axis( new Vector2( 0, 1 ) ).build( );

		// setup bolt on wheel image
		Vector2 boltPosPix = new Vector2( -wheelBolt.getWidth( ) / 2, 0 );
		if ( isDown ) {
			boltPosPix.sub( 0, targetRadiusOnWheelMeter * Util.BOX_TO_PIXEL
					+ wheelBolt.getHeight( ) / 2 );
		} else {
			boltPosPix.add( 0, targetRadiusOnWheelMeter * Util.BOX_TO_PIXEL
					- wheelBolt.getHeight( ) / 2 );
		}

		// Bolt everything together
		RevoluteJointBuilder rBuilder = new RevoluteJointBuilder( level.world )
				.collideConnected( false );
		rBuilder.entityA( girder1 ).entityB( wheel1 )
				.anchor( wheelJointPosMeter ).build( );
		rBuilder.entityB( piston ).anchor( pistonJointPosMeter ).build( );// entity
																			// a
																			// is
																			// still
																			// girder

		engineSkeleton.addPlatforms( girder1 );

		Sprite boltSprite;
		Sprite pistonSprite;
		switch ( index ) {
		case 0:
			pistonSprite = engineAtlas.createSprite( "piston_left" );
			boltSprite = engineAtlas.createSprite( "bolt" + ( index + 1 ) );
			break;
		case 1:
			pistonSprite = engineAtlas.createSprite( "piston_middle" );
			boltSprite = engineAtlas.createSprite( "bolt" + ( index + 1 ) );
			break;
		case 2:
			pistonSprite = engineAtlas.createSprite( "piston_right" );
			boltSprite = engineAtlas.createSprite( "bolt" + ( index + 1 ) );
			break;
		default:
			pistonSprite = engineAtlas.createSprite( "piston_middle" );
			boltSprite = engineAtlas.createSprite( "bolt1" );
			break;
		}

		// Draw order:
		wheel1.addFGDecal( wheelSprite, new Vector2(
				-wheelSprite.getWidth( ) / 2, -wheelSprite.getHeight( ) / 2 ) );
		girder1.addFGDecal( girderSprite, new Vector2(
				-girderSprite.getWidth( ) / 2, -girderSprite.getHeight( ) / 2 ) );

		piston.addFGDecal( pistonSprite, new Vector2(
				-piston.getPixelWidth( ) / 2, -piston.getPixelHeight( ) / 2 ) );
		piston.addFGDecal( boltSprite, new Vector2(
				-boltSprite.getWidth( ) / 2, -boltSprite.getHeight( ) / 2 ) );
		wheel1.addFGDecal( wheelBolt, boltPosPix );
		

		addForeGroundEntity( wheel1 );
		addForeGroundEntity( piston );
		addForeGroundEntity( girder1 );
	}

	private Platform buildGirder( Sprite girder, Vector2 topMeter,
			float pistonDistApartMeter ) {

		Vector2 pos = topMeter.cpy( ).sub( 0, pistonDistApartMeter / 2 );

		BodyDef girderBodyDef = new BodyDef( );
		girderBodyDef.type = BodyType.DynamicBody;
		girderBodyDef.position.set( pos );
		girderBodyDef.fixedRotation = false;
		girderBodyDef.gravityScale = 1f; // doesn't need gravity
		Body girderBody = level.world.createBody( girderBodyDef );

		PolygonShape girderShape = new PolygonShape( );
		Vector2 shape = new Vector2( 0.01f, pistonDistApartMeter / 2 );
		float distPix = pistonDistApartMeter * Util.BOX_TO_PIXEL;
		girderShape.setAsBox( shape.x, shape.y );
		FixtureDef wheelFixture = new FixtureDef( );
		// wheelFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		// wheelFixture.filter.maskBits = Util.CATEGORY_NOTHING;
		wheelFixture.shape = girderShape;
		wheelFixture.density = 0.1f;
		girderBody.createFixture( wheelFixture );

		Platform out = new Platform( "girder", pos, null, level.world );
		out.body = girderBody;
		return out;

	}

	private Platform buildWheel( Vector2 pos, float radiusMeter ) {
		BodyDef wheelBodyDef = new BodyDef( );
		wheelBodyDef.type = BodyType.DynamicBody;
		wheelBodyDef.position.set( pos.cpy( ).mul( Util.PIXEL_TO_BOX ) );
		wheelBodyDef.fixedRotation = false;
		wheelBodyDef.gravityScale = 0.07f;
		Body wheelBody = level.world.createBody( wheelBodyDef );

		CircleShape wheelShape = new CircleShape( );
		wheelShape.setRadius( radiusMeter );
		FixtureDef wheelFixture = new FixtureDef( );
		// wheelFixture.filter.categoryBits = Util.CATEGORY_SCREWS;
		// wheelFixture.filter.maskBits = Util.CATEGORY_NOTHING;
		wheelFixture.shape = wheelShape;
		wheelFixture.density = 0.1f;
		wheelBody.createFixture( wheelFixture );

		wheelShape.dispose( );

		Platform out = new Platform( "wheel", pos, null, level.world );
		out.body = wheelBody;
		// RotateTweenMover m = new RotateTweenMover(out, 4, Util.PI*2, 0, false
		// );
		// out.setMoverAtCurrentState( new RotateTweenMover( out ) );

		return out;
	}

	private void powerSwitch(){
		Platform fallingGear1 = new PlatformBuilder(level.world ).name( "fallingGear1" )
				.type( EntityDef.getDefinition( "gearSmall" ) )
				.position( 0.0f, 400.0f )
				.texture( EntityDef.getDefinition( "gearSmall" ).getTexture( ) )
				.solid( true ).dynamic( ).buildComplexPlatform( );
		chestSkeleton.addDynamicPlatform( fallingGear1 );
		
		fallingGear1.addJointToSkeleton(chestSkeleton);
		
		PowerSwitch ps = new PowerSwitch( "power1" , new Vector2(0, 100), level.world);
				ps.addEntityToTrigger( fallingGear1 );
				ps.actOnEntity = true;
				ps.addBeginIAction( new DestoryPlatformJointAction( ) );
				ps.addEndIAction ( new DestoryPlatformJointAction( ) );
		// AnchorDeactivateAction
				
		chestSkeleton.addEventTrigger( ps );
	}
}
