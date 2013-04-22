package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.RootSkeleton;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.action.RemoveEntityAction;
import com.blindtigergames.werescrewed.entity.builders.EventTriggerBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlatformBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.mover.AnalogRotateMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.mover.ParallaxMover;
import com.blindtigergames.werescrewed.entity.mover.RotateTweenMover;
import com.blindtigergames.werescrewed.entity.platforms.Platform;
import com.blindtigergames.werescrewed.entity.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.entity.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.entity.screws.StrippedScrew;
import com.blindtigergames.werescrewed.entity.screws.StructureScrew;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
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
	private Screw powerScrew1, powerScrew2;
	private Skeleton footSkeleton, kneeSkeleton, thighSkeleton, hipSkeleton,
			chestSkeleton, leftShoulderSkeleton;
	private TiledPlatform kneeMovingPlat, leftShoulderSideHatch;
	private PuzzleScrew leftArmScrew;
	
	private boolean etTriggered = false;

	private Skeleton rightShoulderSkeleton;

	private TiledPlatform rightArmDoor;

	private StrippedScrew rightArmDoorHinge;

	public AlphaScreen( ) {
		super( );

		setClearColor( 79.0f / 255.0f, 82.0f / 255.0f, 104.0f / 255.0f, 1.0f ); //purple-ish

		String filename = "data/levels/alphalevel.xml";
		level = new LevelFactory( ).load( filename );

		level.camera.position = new Vector3( 0, 0, 0 );

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

		// bottom: 0f, 0f
		// power screws: -700f, 1800f
		// chest entrance : -200f, 3800f
		// upper chest: 1300f, 6000f
		// rope on left side of the robot <- -950f, 5100f
		// top left: -1582f, 6150f
		// head: 480f,  6688f
		// right arm: 2600f, 6000f

		if ( level.player1 == null ) {
			level.player1 = new PlayerBuilder( ).world( level.world )
					.position(2600f, 6000f ).name( "player1" ).definition( "red_male" )
					.buildPlayer( );
			level.progressManager.addPlayerOne( level.player1 );
		}
		if ( level.player2 == null ) {
			level.player2 = new PlayerBuilder( ).world( level.world )
					.position( 2600f, 6000f ).name( "player2" )
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

		buildBackground( );
		//buildEngineHeart();
		// new background stuff
		// initBackground( );
		// initBackground( );
		
		buildEngineHeart(new Vector2(0,5450));
	}

	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );

		// characterSelect.update( );

		// characterSelect.draw( batch, deltaTime );

		powerScrew1and2update( );

		if ( leftArmScrew.getDepth( ) == leftArmScrew.getMaxDepth( ) ) {
			leftShoulderSkeleton.addMover( new RotateTweenMover(
					leftShoulderSkeleton, 5f, -Util.PI / 2, 0, false ),
					RobotState.IDLE );
		}

	}

	private void buildBackground( ) {
		// SkeletonBuilder b = new SkeletonBuilder(level.world);
		Skeleton bgSkele;// = b.name( "stageSkeleton" ).position( 0,0 ).build(
							// );
		bgSkele = ( Skeleton ) LevelFactory.entities.get( "stageSkeleton" );
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
		TextureAtlas decals = WereScrewedGame.manager
				.getAtlas( "alphabot_foot_shin_decal" );
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
		int lightX = offsetX - 1966;
		int lightY = offsetY + 50;

		int domeSliceX = 1234 * 2;
		int domeSliceY = 1638;

		int supportY = 6500 + offsetY;
		int supportX = -max + seatsX;

		int curtainX = seatsX - max + 1230;
		int curtainY = seatsY + 585;
		
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
																		// pillar
															// works
		// seats
		bgSkele.addFGDecal( floor_seats.createSprite( "seats_left" ),
				new Vector2( -max + seatsX, seatsY ) );
		bgSkele.addFGDecal( floor_seats.createSprite( "seats_middle" ),
				new Vector2( 0 + seatsX, seatsY ) );
		bgSkele.addFGDecal( floor_seats.createSprite( "seats_right" ),
				new Vector2( max + seatsX, seatsY ) );

		bgSkele.addFGDecal(
				stage_upperright.createSprite( "stage_upperright" ),
				new Vector2( stage_pillarX + 2004, stage_pillarY + 1616 ) );// 1617
				
		// support beam
		level.root.addBGDecal( support_left.createSprite( "support_left" ),
				new Vector2( supportX, supportY ) );
		level.root.addBGDecal(
				support_middle_right.createSprite( "support_middle" ),
				new Vector2( supportX + max, supportY + 216 ) );
		level.root.addBGDecal(
				support_middle_right.createSprite( "support_right" ),
				new Vector2( supportX + 2 * max, supportY ) );
		
		// floor
		bgSkele.addBGDecal( floor_seats.createSprite( "floor_left" ),
				new Vector2( floorX, floorY ) );
		bgSkele.addBGDecal( floor_seats.createSprite( "floor_right" ),
				new Vector2( floorX + max, floorY ) );
		// lights
		level.root.addBGDecal( stage_light.createSprite( "light_left" ),
				new Vector2( lightX, lightY ) );
		level.root.addBGDecal( stage_light.createSprite( "light_right" ),
				new Vector2( lightX + 2030, lightY ) );
		
		initBackground( dome, numDomes, domeSliceX, domeSliceY, -max + seatsX,
				seatsY );
		
		int decalX = -738;// -482;//587
		int decalY = -714;// -558;//536
		Sprite footBG = decals.createSprite( "foot_mechanisms_and_pipes_NOCOLOR" );
		Sprite legBG = decals.createSprite( "shin_pipes_NOCOLOR" );
		Skeleton foot = ( Skeleton ) LevelFactory.entities.get( "footSkeleton" );
		foot.addBGDecal( footBG,
				new Vector2( decalX, decalY ) );
		footBG.setOrigin( 0f, 0f );
		foot.addBGDecal( legBG,
				new Vector2( 400 + decalX, 424 + decalY ) );
		
		// bgSkele.addBGDecal( decals.createSprite(
		// "foot_support_structureNOCOLOR" ), new Vector2(decalX,decalY) );

		level.skelBGList.add( bgSkele );
		level.skelFGList.add( bgSkele );
		
		Sprite s = WereScrewedGame.manager.getAtlas("common-textures").createSprite( "rail_vert_middle" );
		
		bgSkele.addBGDecal( s , new Vector2(-3000,0) );
	}

	private void initBackground( TextureAtlas[ ] dome, int numDomes,
			int domeSliceX, int domeSliceY, int startX, int startY ) {

		BodyDef screwBodyDef;
		Body body;
		CircleShape screwShape;
		FixtureDef screwFixture;
		Entity e1, e2;
		Vector2 pos;
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
			// bgSkele.addBGDecal( a, pos );
			Sprite b = dome[ i - 1 ].createSprite( "dome" + i );
			b.setScale( -2, 1 );
			// bgSkele.addBGDecal( b, pos.cpy( ).add( flipX * domeSliceX, 0 ) );

			screwBodyDef = new BodyDef( );
			screwBodyDef.type = BodyType.KinematicBody;
			screwBodyDef.position.set( 0, 0 );
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
			// the position of each entity and sprite is set at this point.
			e1 = new Entity( "bg_1_" + i, pos, null, body, false );
			e1.sprite = a;
			e2 = new Entity( "bg_2_" + i, pos.cpy( )
					.add( flipX * domeSliceX, 0 ), null, body, false );
			e2.sprite = b;
			// DENNIS: What should I set all these numbers to??
			// if it helps, each bg piece is 1238x1642
			e1.setMoverAtCurrentState( new ParallaxMover( new Vector2( e1
					.getPositionPixel( ) ), new Vector2( e1.getPositionPixel( )
					.sub( 0f, 512f ) ), 0.0002f, .5f, level.camera, false,
					LinearAxis.VERTICAL ) );
			e2.setMoverAtCurrentState( new ParallaxMover( new Vector2( e2
					.getPositionPixel( ) ), new Vector2( e2.getPositionPixel( )
					.sub( 0f, 512f ) ), 0.0002f, .5f, level.camera, false,
					LinearAxis.VERTICAL ) );
			level.backgroundRootSkeleton.addLooseEntity( e1 );
			level.backgroundRootSkeleton.addLooseEntity( e2 );
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

	private void createFootObjects( ) {
		footSkeleton = ( Skeleton ) LevelFactory.entities.get( "footSkeleton" );

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

		structurePlat3.setGroupIndex( ( short ) -5 );
		footPlat6.setGroupIndex( ( short ) -5 );
	}

	private void createKneeObjects( ) {
		kneeMovingPlat = ( TiledPlatform ) LevelFactory.entities
				.get( "kneeMovingPlat" );
		kneeMovingPlat.setActive( false );

		powerScrew1 = ( Screw ) LevelFactory.entities.get( "powerScrew1" );
		powerScrew2 = ( Screw ) LevelFactory.entities.get( "powerScrew2" );

		// removePlayerToScrew( )
	}

	private void powerScrew1and2update( ) {

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
	}

	private void chestObjects( ) {

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
		
		EventTrigger etGearFall = ( EventTrigger ) LevelFactory.entities.get( "et1" );
		
			
	}

	private void leftArm( ) {
		leftArmScrew = ( PuzzleScrew ) LevelFactory.entities
				.get( "leftShoulderPuzzleScrew1" );

		leftShoulderSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "leftShoulderSkeleton" );

		leftShoulderSideHatch = ( TiledPlatform ) LevelFactory.entities
				.get( "leftShoulderSideHatch" );

		RevoluteJointDef rjd = new RevoluteJointDef( );
		rjd.initialize(
				leftShoulderSideHatch.body,
				leftShoulderSkeleton.body,
				leftShoulderSideHatch.getPosition( ).sub( 0,
						leftShoulderSideHatch.getMeterHeight( ) / 2 ) );
		level.world.createJoint( rjd );
	}

	private void rightArm( ) {
		rightShoulderSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "rightShoulderSkeleton" );

		rightArmDoor = ( TiledPlatform ) LevelFactory.entities
				.get( "rightArmDoor" );

		rightArmDoorHinge = ( StrippedScrew ) LevelFactory.entities
				.get( "rightArmDoorHinge" );

		RevoluteJointDef rjd = new RevoluteJointDef( );
		rjd.initialize( rightArmDoor.body, rightShoulderSkeleton.body,
				rightArmDoorHinge.body.getWorldCenter( ) );
		level.world.createJoint( rjd );
	}

	private void buildEngineHeart( Vector2 posPix ) {
		Skeleton engineSkeleton = new Skeleton( "engineSkeleton", posPix, null,
				level.world );
		level.root.addSkeleton( engineSkeleton );
		int pistonDistanceApart = 280;
		float engineSpeed = 2.5f;
		

		TextureAtlas engineAtlas = WereScrewedGame.manager.getAtlas( "engine" );
		Vector2 decalPos = engineSkeleton.getPositionPixel( ).sub( posPix ).add( -230,-360 );
		engineSkeleton.addBGDecal( engineAtlas.createSprite("chest-engine"), new Vector2(decalPos) );
		level.entityBGList.add(engineSkeleton);

		for ( int i = 0; i < 3; ++i ) {
			buildPiston( engineSkeleton, engineAtlas,
					posPix.cpy( ).add( pistonDistanceApart * i, 0 ), i, engineSpeed );
		}

	}

	private void buildPiston( Skeleton engineSkeleton,
			TextureAtlas engineAtlas, Vector2 posPix, int index, float engineSpeed ) {
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
		level.entityFGList.add( wheel1 );
		level.entityFGList.add( piston );
		level.entityFGList.add( girder1 );
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
	
}
