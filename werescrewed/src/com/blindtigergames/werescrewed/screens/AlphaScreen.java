package com.blindtigergames.werescrewed.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
import com.badlogic.gdx.utils.Array;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.camera.Anchor;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.EntityDef;
import com.blindtigergames.werescrewed.entity.Panel;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.RootSkeleton;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.Sprite;
import com.blindtigergames.werescrewed.entity.action.AnchorActivateAction;
import com.blindtigergames.werescrewed.entity.action.AnchorDeactivateAction;
import com.blindtigergames.werescrewed.entity.action.DestoryPlatformJointAction;
import com.blindtigergames.werescrewed.entity.action.EntityActivateMoverAction;
import com.blindtigergames.werescrewed.entity.action.EntityDeactivateMoverAction;
import com.blindtigergames.werescrewed.entity.action.RemoveEntityAction;
import com.blindtigergames.werescrewed.entity.action.RotateTweenAction;
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
import com.blindtigergames.werescrewed.entity.particles.Steam;
import com.blindtigergames.werescrewed.entity.platforms.Pipe;
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
import com.blindtigergames.werescrewed.sound.SoundManager;
import com.blindtigergames.werescrewed.sound.SoundManager.SoundType;
import com.blindtigergames.werescrewed.util.Util;

public class AlphaScreen extends Screen {

	public ScreenType screenType;

	private CharacterSelect characterSelect;
	private PowerSwitch powerSwitch1, powerSwitch2, powerSwitch3, 
		powerSwitch4, powerSwitch5, powerSwitch6,
		powerSwitch7, powerSwitch8, powerSwitch9,
		powerSwitch10, chestSteamPowerSwitch,
		powerSwitchBrain1, powerSwitchBrain2;
	
	private PowerSwitch powerSwitchPuzzle1, powerSwitchPuzzle2;
	private Skeleton footSkeleton, kneeSkeleton, 
		thighSkeleton, hipSkeleton,
		chestSkeleton, leftShoulderSkeleton, headSkeleton, thighSkeleton2;
	
	private TiledPlatform kneeMovingPlat, chestRotatePlat1, 
		chestRotatePlat3, headEntrancePlatform4,
		headEyebrow1, headEyebrow2;

	Platform leftShoulderSideHatch, ankleHatch;
	private PuzzleScrew leftArmScrew, chestPuzzleScrew2;
	
	private Steam engineSteam;

	private boolean chestSteamTriggered = false, headPlatformCreated = false, headAnchorActivatedOnce = false;
	private boolean rLegTriggered = false, thighSteamTriggered = false;

	private Skeleton rightShoulderSkeleton;

	private Platform rightArmDoor;

	private StrippedScrew rightArmDoorHinge;
	private StructureScrew structureScrew1;
	
	private int rightShoulderSkeletonAnchorCounter = 0;
	
	Array<Panel> panels;

	protected SoundManager bgm;
	
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
		
		Vector2 spawnPos = new Vector2( 0,0 );

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
		//powerSwitch();
		initPanels();
		bgm = new SoundManager();
		bgm.getSound( "bgm" ,WereScrewedGame.dirHandle
				+ "/common/music/waltz.mp3");
		bgm.setSoundVolume( "bgm", SoundManager.globalVolume.get(SoundType.MUSIC));
		bgm.loopSound( "bgm" );
	}

	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );

		// characterSelect.update( );

		// characterSelect.draw( batch, deltaTime );

		powerScrewUpdate( deltaTime );
		

		
		//If everything is on
		if (  powerSwitch1.isTurnedOn( )  && powerSwitch2.isTurnedOn( )
				&&  powerSwitch3.isTurnedOn( )  && powerSwitch4.isTurnedOn( )
				&&  powerSwitch5.isTurnedOn( )  && powerSwitch6.isTurnedOn( )
				&&  powerSwitch7.isTurnedOn( )  && powerSwitch8.isTurnedOn( )
				&&  powerSwitch9.isTurnedOn( )  && powerSwitch10.isTurnedOn( ) ){
			
			//check if players are outside of arms and above half the chest
			if( (level.player1.getPositionPixel( ).x >-1747f 
					&& level.player1.getPositionPixel( ).x < 2848f 
					&& level.player1.getPositionPixel( ).y > 4688)
					&&
					(level.player2.getPositionPixel( ).x >-1747f 
							&& level.player2.getPositionPixel( ).x < 2848f 
							&& level.player2.getPositionPixel( ).y > 4688)) {
				
				if(!headSkeleton.anchors.get(0).activated && !headAnchorActivatedOnce){
					headAnchorActivatedOnce = true;
					headSkeleton.anchors.get(0).setTimer( 30 );
					headSkeleton.anchors.get(0).activate( );
				}
				
				
				if(!headPlatformCreated){
					headPlatformCreated = true;
					EventTriggerBuilder etb = new EventTriggerBuilder( level.world );
					
					etb.name( "head_platform_event").rectangle( ).height( 500f ).width( 300f )
					.position(new Vector2( 900, 6150));
					
					etb.addEntity( headEntrancePlatform4 );
					etb.beginAction(  new EntityActivateMoverAction(  ) );
					
					
					EventTrigger et = etb.repeatable( ).build( );
					chestSkeleton.addEventTrigger( et );
				}
			}
			
			if(powerSwitchBrain1.isTurnedOn( ) && powerSwitchBrain2.isTurnedOn( )){

				if(headEyebrow1.currentMover( ) == null){
					Timeline t = Timeline.createSequence( );
		
					t.push( Tween
							.to( headEyebrow1, PlatformAccessor.LOCAL_POS_XY, 0.5f )
							.delay( 0f ).target( 0, 200 )
							.ease( TweenEquations.easeNone ).start( ) );
					
					t.push( Tween
							.to( headEyebrow2, PlatformAccessor.LOCAL_POS_XY, 0f )
							.delay( 5f ).target( 0, 200 )
							.ease( TweenEquations.easeNone ).start( ) );
					
					headEyebrow1
							.addMover( new TimelineTweenMover( t.start( ) ) );
				}
				
				if(headEyebrow2.currentMover( ) == null){
					Timeline t = Timeline.createSequence( );
		
					t.push( Tween
							.to( headEyebrow2, PlatformAccessor.LOCAL_POS_XY, 0.5f )
							.delay( 0f ).target( 0, 200 )
							.ease( TweenEquations.easeNone ).start( ) );
		
					t.push( Tween
							.to( headEyebrow2, PlatformAccessor.LOCAL_POS_XY, 0f )
							.delay( 5f ).target( 0, 200 )
							.ease( TweenEquations.easeNone ).start( ) );
					
					headEyebrow2
							.addMover( new TimelineTweenMover( t.start( ) ) );
				}
				
				
				if(headEyebrow1.isTimeLineMoverFinished( )
						&& headEyebrow2.isTimeLineMoverFinished( )){
					
					//You win and goto next screen!!!
					ScreenManager.getInstance( ).show( ScreenType.LOADING_2 );
				}
			}
		}

	
		if(Gdx.input.isKeyPressed( Keys.NUM_9 )){
			powerSwitch1.setState( true );
			powerSwitch2.setState( true );
			powerSwitch3.setState( true );
			powerSwitch4.setState( true );
			powerSwitch5.setState( true );
			powerSwitch6.setState( true );
			powerSwitch7.setState( true );
			powerSwitch8.setState( true );
			powerSwitch9.setState( true );
			powerSwitch10.setState( true );
		}

	}

	private void buildBackground( ) {
		Skeleton bgSkele;
		bgSkele = ( Skeleton ) LevelFactory.entities.get( "stageSkeleton" );
		
		Gdx.app.log( "bgSKele", bgSkele.getPositionPixel( ) +"");
		Skeleton light_skel = ( Skeleton ) LevelFactory.entities.get( "lightSkeleton" );
		addBGSkeletonBack( light_skel ); 
		
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

		int max = 2030;
		int offsetX = 200;
		int offsetY = 0;
		int floorY = -199 + offsetY;
		int seatsY = -583 + offsetY;
		int seatsX = -1180 + offsetX;// -1180
		int floorX = -max + offsetX;
		int stage_pillarY = -202 + offsetY;
		int stage_pillarX = floorX - 530;
		int lightX = offsetX - 1986;
		int lightY = offsetY + 24;

		int domeSliceX = 1234 * 2;
		int domeSliceY = 1638;

		int supportY = 6500 + offsetY;
		int supportX = -max + seatsX;

		int curtainX = seatsX - max + 1230;
		int curtainY = seatsY + 585;

		// support beam
		light_skel.addBGDecalBack( support_left.createSprite( "support_left" ),
				new Vector2( supportX, supportY ) );
		light_skel.addBGDecalBack(
				support_middle_right.createSprite( "support_middle" ),
				new Vector2( supportX + max, supportY + 216 ) );
		light_skel.addBGDecalBack(
				support_middle_right.createSprite( "support_right" ),
				new Vector2( supportX + 2 * max, supportY ) );

		// lights
		light_skel.addBGDecal( stage_light.createSprite( "light_left" ),
				new Vector2( lightX, lightY ) );
		light_skel.addBGDecal( stage_light.createSprite( "light_right" ),
				new Vector2( lightX + 2030, lightY ) );

		// floor
		light_skel.addBGDecal( floor_seats.createSprite( "floor_left" ),
				new Vector2( floorX, floorY ) );
		light_skel.addBGDecal( floor_seats.createSprite( "floor_right" ),
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
		light_skel.addFGDecal( floor_seats.createSprite( "seats_left" ),
				new Vector2( -max + seatsX, seatsY ) );
		light_skel.addFGDecal( floor_seats.createSprite( "seats_middle" ),
				new Vector2( 0 + seatsX, seatsY ) );
		light_skel.addFGDecal( floor_seats.createSprite( "seats_right" ),
				new Vector2( max + seatsX, seatsY ) );

		//addBackGroundEntity( bgSkele );
		//addForeGroundEntity( bgSkele );
		

		// initBackground( dome, numDomes, domeSliceX, domeSliceY,
		// 100,100);//-max + seatsX, seatsY );
		initParallaxBackground( ); 
		// level.entityFGList.add(bgSkele);
		// level.entityBGList.add(bgSkele);

		// level.entityBGList.remove( level.root );
		// level.entityFGList.remove( level.root );
		
		
		addBGSkeletonBack( bgSkele );
		addFGSkeleton( bgSkele );
		level.skelFGList.remove( light_skel );
		addFGSkeleton(light_skel);
		for(Skeleton s:level.skelFGList){
			Gdx.app.log( "fgList", s.name );
		}
		light_skel.setFgFade( false );
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
		Vector2 bodyPos = new Vector2( 0, -512 );
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
				.add( 0f, 312f ) ), 0.00005f, .5f, level.camera, false,
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
		thighSkeleton2 = ( Skeleton ) LevelFactory.entities
		.get( "thighSkeleton2" );
		
		footSkeleton.body.setType( BodyType.KinematicBody );
		kneeSkeleton.body.setType( BodyType.KinematicBody );
		thighSkeleton.body.setType( BodyType.KinematicBody );

		structureScrew1 = ( StructureScrew ) LevelFactory.entities
				.get( "structureScrew1" );
		//414, 48
		ankleHatch = ( Platform ) LevelFactory.entities
				.get( "ankle_hatch" );
		RevoluteJointDef rjd = new RevoluteJointDef( );
		rjd.initialize( ankleHatch.body, footSkeleton.body,
				//new Vector2( -414, 48 ).mul( Util.PIXEL_TO_BOX ) );
				new Vector2( -415, 14 ).mul( Util.PIXEL_TO_BOX ) );
		level.world.createJoint( rjd );
		
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
		int decalX = -686;// -482;//587
		int decalY = -614;// -558;//536
		Sprite footBG = decals
				.createSprite( "foot-interior" );
		Sprite legBG = decals.createSprite( "shin-interior" );
		Skeleton foot = ( Skeleton ) LevelFactory.entities.get( "footSkeleton" );
		foot.addBGDecal( footBG, new Vector2( decalX, decalY ) );
		footBG.setOrigin( 0f, 0f );
		foot.addBGDecal( legBG, new Vector2( 410 + decalX, 432 + decalY ) );

		addBGSkeleton( footSkeleton );
		addFGSkeleton( footSkeleton );
		
		Vector2 footFGPos = new Vector2( decalX - 0, decalY -10 );
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
		addFGSkeleton( kneeSkeleton );

		kneeSkeleton
				.addBGDecalBack( knee_exterior
						.createSprite( "knee_mechanisms_and_pipesNOCOLOR" ),
						kneeDecalPos.cpy( ) );
		// removePlayerToScrew( )
	}

	private void initPowerScrews( ) {

		powerSwitchBrain1 = (PowerSwitch) LevelFactory.entities.get( "PowerSwitchBrain1" );
		powerSwitchBrain2 = (PowerSwitch) LevelFactory.entities.get( "PowerSwitchBrain2" );
		
		powerSwitch1 = (PowerSwitch) LevelFactory.entities.get( "powerSwitch1" );
		powerSwitch2 = (PowerSwitch) LevelFactory.entities.get( "powerSwitch2" );
		
		powerSwitch3 = (PowerSwitch) LevelFactory.entities.get( "powerSwitch3" );
		powerSwitch4 = (PowerSwitch) LevelFactory.entities.get( "powerSwitch4" );
		
		powerSwitch5 = (PowerSwitch) LevelFactory.entities.get( "powerSwitch5" );
		powerSwitch6 = (PowerSwitch) LevelFactory.entities.get( "powerSwitch6" );
		
		powerSwitch7 = (PowerSwitch) LevelFactory.entities.get( "powerSwitch7" );
		powerSwitch8 = (PowerSwitch) LevelFactory.entities.get( "powerSwitch8" );
		
		powerSwitch9 = (PowerSwitch) LevelFactory.entities.get( "powerSwitch9" );
		powerSwitch10 = (PowerSwitch) LevelFactory.entities.get( "powerSwitch10" );
		
		chestSteamPowerSwitch = (PowerSwitch) LevelFactory.entities.get( "chestSteamPowerSwitch" );
		chestSteamPowerSwitch.setState( true );
		chestSteamPowerSwitch.addEntityToTrigger( engineSteam );
		chestSteamPowerSwitch.actOnEntity = true;
		chestSteamPowerSwitch.addBeginIAction( new EntityActivateMoverAction() );
		chestSteamPowerSwitch.addEndIAction( new EntityDeactivateMoverAction() );
		
		powerSwitchPuzzle1 = (PowerSwitch) LevelFactory.entities.get( "powerSwitchPuzzle1" );
		powerSwitchPuzzle2 = (PowerSwitch) LevelFactory.entities.get( "powerSwitchPuzzle2" );
		
		powerSwitchPuzzle1.actOnEntity = true;
		powerSwitchPuzzle1.addEntityToTrigger( chestRotatePlat3 );
		powerSwitchPuzzle1.addEntityToTrigger( chestRotatePlat1 );
		powerSwitchPuzzle1.addBeginIAction( new RotateTweenAction(Util.PI / 2) );
		powerSwitchPuzzle1.addEndIAction( new RotateTweenAction( 0) );
		
		powerSwitchPuzzle2.actOnEntity = true;
		powerSwitchPuzzle2.addEntityToTrigger( chestRotatePlat3 );
		powerSwitchPuzzle2.addEntityToTrigger( chestRotatePlat1 );
		powerSwitchPuzzle2.addBeginIAction( new RotateTweenAction(Util.PI / 2) );
		powerSwitchPuzzle2.addEndIAction( new RotateTweenAction( 0) );
		
		
	}

	private void powerScrewUpdate( float deltaTime ) {

		if (  powerSwitch1.isTurnedOn( )  && powerSwitch2.isTurnedOn( ) ) {
			kneeMovingPlat.setActive( true );


			if ( hipSkeleton.currentMover( ) == null ) {
				updatePanels("left_leg");
				hipSkeleton.addMover( new RotateTweenMover( hipSkeleton, 3f,
						-Util.PI / 45, 1f, true ), RobotState.IDLE );

				// PathBuilder pb = new PathBuilder( );
				// hipSkeleton.addMover( pb.begin( hipSkeleton )
				// .target( 0, 100, 3 ).delay( 1 ).target( 0, -25, 3 )
				// .target( 0, 0, 3 ).build( ), RobotState.IDLE );

			}

		}

		if ( powerSwitch5.isTurnedOn( )  && powerSwitch6.isTurnedOn( ) && !thighSteamTriggered) {
			thighSteamTriggered = true;
			Steam steam = new Steam( "steamThigh1", new Vector2( 2015, 2880 ), 25, 120, level.world );
			thighSkeleton2.addSteam(steam);
		}
		if ( powerSwitch7.isTurnedOn( )  && powerSwitch8.isTurnedOn( ) ) {
			if(leftShoulderSkeleton.currentMover( ) == null){
				updatePanels("left_arm");
				Timeline t = Timeline.createSequence( );
	
				t.push( Tween
						.to( leftShoulderSkeleton, PlatformAccessor.LOCAL_ROT, 20f )
						.ease( TweenEquations.easeInOutQuad )
						.target( ( -Util.PI / 2 ) ).delay( 1f ).start( ) );
	
				leftShoulderSkeleton
						.addMover( new TimelineTweenMover( t.start( ) ) );
				
				//activate anchor
				leftShoulderSkeleton.anchors.get( 0 ).activate( );
			} else if (leftShoulderSkeleton.isTimeLineMoverFinished( )){
				//deactivate anchor
				leftShoulderSkeleton.anchors.get( 0 ).deactivate( );
			}
		}
		
		if (  powerSwitch9.isTurnedOn( )  && powerSwitch10.isTurnedOn( ) ) {
			Skeleton rightElbowSkeleton = ( Skeleton ) LevelFactory.entities
					.get( "rightElbowSkeleton" );
			Skeleton rightShoulderSkeleton = ( Skeleton ) LevelFactory.entities
					.get( "rightShoulderSkeleton" );

			if ( rightElbowSkeleton.currentMover( ) == null ) {
				updatePanels("right_arm");
				Timeline t = Timeline.createSequence( );

				t.push( Tween
						.to( rightElbowSkeleton, PlatformAccessor.LOCAL_ROT,
								10f ).ease( TweenEquations.easeInOutQuad )
						.target( ( Util.PI / 2 ) ).delay( 0f ).start( ) );

				rightElbowSkeleton
						.addMover( new TimelineTweenMover( t.start( ) ) );
				
				rightElbowSkeleton.anchors.get( 0 ).activate( );
				
			} else if (rightElbowSkeleton.isTimeLineMoverFinished( )){
				//deactivate anchor
				rightElbowSkeleton.anchors.get( 0 ).deactivate( );
			}
				
			if(rightShoulderSkeleton.currentMover( ) == null 
					&& rightElbowSkeleton.isTimeLineMoverFinished( )){
				Timeline t2 = Timeline.createSequence( );

				t2.delay( 5f );
				t2.push( Tween
						.to( rightShoulderSkeleton, PlatformAccessor.LOCAL_ROT,
								10f ).ease( TweenEquations.easeInOutQuad )
						.target( ( Util.PI / 2 ) ).delay( 0f ).start( ) );

				rightShoulderSkeleton.addMover( new TimelineTweenMover( t2
						.start( ) ) );
				
				rightShoulderSkeleton.anchors.get( 0 ).activate( );
				
			} else if (rightShoulderSkeleton.isTimeLineMoverFinished( )){
				//deactivate anchor
				rightShoulderSkeleton.anchors.get( 0 ).deactivate( );
			}
		}
		
		if (  powerSwitch3.isTurnedOn( )  && powerSwitch4.isTurnedOn( ) && !chestSteamTriggered) {
			updatePanels("chest");
			
			chestSteamTriggered = true;
			EventTriggerBuilder etb = new EventTriggerBuilder( level.world );
			
			etb.name( "chestPuzzle_event_anchor").rectangle( ).height( 400f ).width( 600f )
			.position(new Vector2( 570, 4000));
			
			Anchor anchor = chestPuzzleScrew2.anchors.get( 0 );
			etb.beginAction(  new AnchorActivateAction( anchor ) );
			etb.endAction( new AnchorDeactivateAction(anchor) );
			
			EventTrigger et = etb.repeatable( ).twoPlayersToActivate( ).twoPlayersToDeactivate( ).build( );
			chestSkeleton.addEventTrigger( et );
			
			Steam steam = new Steam( "steamChest1", new Vector2( 576, 4000 ), 25, 225, level.world );
			chestSkeleton.addSteam(steam);
			
			//GET RID OF TEMP STEAM WHEN PARTICLES MATCH THE BODY SIZE
			Steam temp = new Steam( "steamtemp1", new Vector2( 576, 4000 ), 25, 120, level.world );
			temp.setTempCollision( false );
			chestSkeleton.addSteam(temp);
			
			Steam temp2 = new Steam( "steamtemp2", new Vector2( 576, 4100 ), 25, 120, level.world );
			temp2.setTempCollision( false );
			chestSkeleton.addSteam(temp);
			
//			Steam steam2 = new Steam( "steamChest2", new Vector2( 350, 4450 ), 25, 120, level.world );
////			steam2.setLocalRot(  270 * Util.DEG_TO_RAD );
//			chestSkeleton.addSteam(steam2);
		}
		
		if ( !rLegTriggered && powerSwitch5.isTurnedOn( )  && powerSwitch6.isTurnedOn( ) ) {
			//do right leg activation stuff stuff
			rLegTriggered = true;
			updatePanels("right_leg");
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
		headSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "headSkeleton" );
		
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

		headEntrancePlatform4 = ( TiledPlatform ) LevelFactory.entities
				.get( "head_entrance_platform_4" );
		headEntrancePlatform4.setActive( false );
		
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
		
		//it has the anchor I need when power switches 3-4 are on
		chestPuzzleScrew2 = ( PuzzleScrew ) LevelFactory.entities
		.get( "chestPuzzleScrew2" );
		
		
		engineSteam = new Steam( "steamChest3", new Vector2( -420, 5050 ), 25, 120, level.world );
		chestSkeleton.addSteam(engineSteam);
		
//		Pipe chestPipe3 = ( Pipe ) LevelFactory.entities
//				.get( "chestPipe3" );
		
//		chestPipe3.setCategoryMask( Util.CATEGORY_PLATFORMS,
//				Util.CATEGORY_PLAYER );
		
		chestRotatePlat1 = ( TiledPlatform ) LevelFactory.entities
				.get( "chestRotatePlat1" );
		chestRotatePlat1.setActive( true );

		
		chestRotatePlat3 = ( TiledPlatform ) LevelFactory.entities
				.get( "chestRotatePlat3" );
		chestRotatePlat3.setActive( true );
		
		headEyebrow1 = ( TiledPlatform ) LevelFactory.entities
				.get( "headEyebrow1" );
		
		
		headEyebrow2 = ( TiledPlatform ) LevelFactory.entities
				.get( "headEyebrow2" );
		

		Skeleton rightKneeTrapDoorSkeleton1 = ( Skeleton ) LevelFactory.entities
				.get( "rightKneeTrapDoorSkeleton1" );
		Skeleton rightKneeTrapDoorSkeleton2 = ( Skeleton ) LevelFactory.entities
				.get( "rightKneeTrapDoorSkeleton2" );
		
		//rightKneeTrapDoorSkeleton2.setActive( false );
		//rightKneeTrapDoorSkeleton1.setActive( false );
		
		EventTrigger rightKneeTrapDoorEvent1 = ( EventTrigger ) LevelFactory.entities
				.get( "rightKneeTrapDoorEvent1" );
		
		rightKneeTrapDoorEvent1.addEntityToTrigger( rightKneeTrapDoorSkeleton1 );
		rightKneeTrapDoorEvent1.actOnEntity = true;
		rightKneeTrapDoorEvent1.addBeginIAction( new RotateTweenAction(-Util.PI/2, 0.8f));
		
		EventTrigger rightKneeTrapDoorEvent2 = ( EventTrigger ) LevelFactory.entities
				.get( "rightKneeTrapDoorEvent2" );
		
		rightKneeTrapDoorEvent2.addEntityToTrigger( rightKneeTrapDoorSkeleton2 );
		rightKneeTrapDoorEvent2.actOnEntity = true;
		rightKneeTrapDoorEvent2.addBeginIAction( new RotateTweenAction(Util.PI/2, 0.8f));
				
				
	}
	
	private void createChestDecals(){
		TextureAtlas chest_powerscrew = WereScrewedGame.manager.getAtlas( "chest_powerscrew" );
		Skeleton chestSkeleton = (Skeleton)LevelFactory.entities.get( "chestSkeleton" );
		chestSkeleton.addBGDecal( 
				chest_powerscrew.createSprite( "chest_powerscrew_pipes_to_engineNOCOLOR" ), 
				new Vector2(-453,-970) );
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
		
		
		TextureAtlas left_arm = WereScrewedGame.manager.getAtlas( "left-arm" );
		Vector2 pos = new Vector2(-455,-3250);
		leftShoulderSkeleton.addBGDecal( left_arm.createSprite( "left-arm-bottom" ), pos );
		leftShoulderSkeleton.addBGDecal( left_arm.createSprite( "left-arm-top" ), pos.cpy().add(0,1721) );
	}

	private void rightArm( ) {
		rightShoulderSkeleton = ( Skeleton ) LevelFactory.entities
				.get( "rightShoulderSkeleton" );

		rightArmDoor = ( Platform ) LevelFactory.entities
				.get( "rightShoulderTopHatch1" );
		
		RevoluteJointDef rjd = new RevoluteJointDef( );
		rjd.initialize( rightArmDoor.body, rightShoulderSkeleton.body,
				new Vector2( 2851f, 5904f ).mul( Util.PIXEL_TO_BOX ) );
		level.world.createJoint( rjd );
	}

	private void buildEngineHeart( Vector2 posPix ) {
		Skeleton engineSkeleton = new Skeleton( "engineSkeleton", posPix, null,
				level.world );
		level.root.addSkeleton( engineSkeleton );
		engineSkeleton.setFgFade( false );
		int pistonDistanceApart = 280;
		float engineSpeed = 2.5f;
		
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
		addFGEntity( engineSkeleton );

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
		float girderInset = 0.97f;
		float pistonDistApartMetre = girderSprite.getHeight( ) * girderInset
				* Util.PIXEL_TO_BOX;
		Sprite wheelBolt = engineAtlas.createSprite( "bolt0" );
		// Build GIRDER!
		float targetRadiusOnWheelMeter = ( radiusPix - wheelBolt.getHeight( ) / 3 )
				* Util.PIXEL_TO_BOX;
		boolean isDown = ( index % 2 == 0 );

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

		// Build piston!!
		Vector2 pistonJointPosMeter = wheelJointPosMeter.cpy( ).sub( 0,
				pistonDistApartMetre );

		Vector2 finalPos = pistonJointPosMeter.cpy( ).mul( Util.BOX_TO_PIXEL );
		finalPos = finalPos.sub( 58f, 90f );
		PlatformBuilder pBuilder = new PlatformBuilder(level.world ).name( "piston"  )
				.position( finalPos )
				.texture( null )
				.dynamic( );
	
		

		if(index ==0 ){
			pBuilder.type( EntityDef.getDefinition( "pistonLeft" ) );
		} else if (index == 1){
			pBuilder.type( EntityDef.getDefinition( "pistonMiddle" ) );
		} else {
			pBuilder.type( EntityDef.getDefinition( "pistonRight" ) );
		}
		
		Platform piston = pBuilder.buildComplexPlatform( );
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


		piston.addFGDecal( pistonSprite, Vector2.Zero );
		piston.addFGDecal( boltSprite, new Vector2(32f, 48f) );
		wheel1.addFGDecal( wheelBolt, boltPosPix );
		

		addFGEntity( wheel1 );
		addFGEntity( piston );
		addFGEntity( girder1 );
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
	
	private void initPanels(){
		int numPanels = 5;
		String panelAtlas = "alphabot-panel";
		panels = new Array< Panel >(numPanels);
		Panel p;
		//get panels 1-5 (possibly 6, but not yet)
		for(int i = 1; i <= numPanels; ++i ){
			p = (Panel) LevelFactory.entities.get( "panel"+i );
			p.setPanelSprite("alphabot-panel_off");
			panels.add( p );
		}
		
		//Panel p = new Panel( kneeSkeleton.getPositionPixel( ), level.world, panelAtlas, "alphabot-panel_off" );
		//kneeSkeleton.addPlatform( p );
		//panels.add(p);
		
	}
	
	private void updatePanels(String activatedPanel){
		String panelName = "alphabot-panel";
		
		if(activatedPanel.equals( "left_leg" )){
			panelName = "alphabot-panel_left_leg";
		}else if (activatedPanel.equals("chest")){
			panelName = "alphabot-panel_chest";
		}else{
			//rleg
			if( powerSwitch5.isTurnedOn( )  && powerSwitch6.isTurnedOn( ) ){
				panelName = panelName + "_rleg";
			}
			
			//right arm
			if( powerSwitch9.isTurnedOn( )  && powerSwitch10.isTurnedOn( ) ){
				panelName = panelName + "_rarm";
			}
			
			//left arm
			if ( powerSwitch7.isTurnedOn( )  && powerSwitch8.isTurnedOn( )){
				panelName = panelName + "_larm";
			}
		}
		//hip skeleton
		//powerSwitch1.isTurnedOn( )  && powerSwitch2.isTurnedOn( ) 
			
		//chest
		 //powerSwitch3.isTurnedOn( )  && powerSwitch4.isTurnedOn( ) 
		for(Panel p : panels){
			p.setPanelSprite( panelName );
		}
	}
}
