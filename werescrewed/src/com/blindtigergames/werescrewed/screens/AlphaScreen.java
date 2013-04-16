package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.WereScrewedGame;
import com.blindtigergames.werescrewed.entity.Entity;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.RootSkeleton;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.action.RemoveEntityAction;
import com.blindtigergames.werescrewed.entity.builders.EventTriggerBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.builders.SkeletonBuilder;
import com.blindtigergames.werescrewed.entity.mover.AnalogRotateMover;
import com.blindtigergames.werescrewed.entity.mover.LinearAxis;
import com.blindtigergames.werescrewed.entity.mover.ParallaxMover;
import com.blindtigergames.werescrewed.entity.mover.RotateTweenMover;
import com.blindtigergames.werescrewed.entity.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.entity.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.entity.screws.StructureScrew;
import com.blindtigergames.werescrewed.entity.tween.PathBuilder;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.graphics.SpriteBatch;
import com.blindtigergames.werescrewed.graphics.TextureAtlas;
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

	public AlphaScreen( ) {
		super( );
		String filename = "data/levels/alphalevel.xml";
		level = new LevelFactory( ).load( filename );
		
		level.camera.position = new Vector3(0,0,0);
		
		//death barrier
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

		if ( level.player1 == null ) {
			level.player1 = new PlayerBuilder( ).world( level.world )
					.position( 0, 0 ).name( "player1" ).definition( "red_male" )
					.buildPlayer( );
			level.progressManager.addPlayerOne( level.player1 );
		}
		if ( level.player2 == null ) {
			level.player2 = new PlayerBuilder( ).world( level.world )
					.position( 0, 0 ).name( "player2" )
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

		buildBackground( );
		// new background stuff
		initBackground( );

	}

	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );

		// characterSelect.update( );

		// characterSelect.draw( batch, deltaTime );

		powerScrew1and2update( );

		if ( leftArmScrew.getDepth( ) == leftArmScrew.getMaxDepth( ) ) {
			leftShoulderSkeleton.addMover( new RotateTweenMover(
					leftShoulderSkeleton, 10, -Util.PI / 2, 0, false ),
					RobotState.IDLE );
		}

	}

	private void buildBackground(){
		SkeletonBuilder b = new SkeletonBuilder(level.world);
		Skeleton bgSkele = b.name( "bgSkele" ).position( 0,0 ).build( );
		TextureAtlas floor_seats = WereScrewedGame.manager.getAtlas("alphabot_floor_seats");
		TextureAtlas stage_pillar = WereScrewedGame.manager.getAtlas( "stage_pillar");
		TextureAtlas stage_upperleft = WereScrewedGame.manager.getAtlas("stage_upperleft");
		TextureAtlas stage_upperright = WereScrewedGame.manager.getAtlas("stage_upperright");
		int max = 2030;
		int offsetX = 400;
		int offsetY = 0;
		int floorY = -199 + offsetY;
		int seatsY = -583 + offsetY;
		int seatsX = -1180+offsetX;//-1180
		int floorX = -max+offsetX;
		int stage_pillarY = -202 + offsetY;
		int stage_pillarX = floorX-530;
		
		//floor
		bgSkele.addBGDecal( floor_seats.createSprite( "floor_left" ), new Vector2(floorX,floorY ) );
		bgSkele.addBGDecal( floor_seats.createSprite( "floor_right" ), new Vector2( floorX+max,floorY ) );
		//stage is in between floor & seats
		bgSkele.addFGDecal( stage_pillar.createSprite( "stage_left" ), new Vector2(stage_pillarX,stage_pillarY) );
		bgSkele.addFGDecal(stage_upperleft.createSprite( "stage_upperleft"), new Vector2(stage_pillarX+2, 1647+stage_pillarY ) );//1647 is height of left pillar
		bgSkele.addFGDecal( stage_pillar.createSprite( "stage_right" ), new Vector2(stage_pillarX+3204, stage_pillarY) );//3204 is difference between left & right pillar
		bgSkele.addFGDecal( stage_upperright.createSprite( "stage_upperright" ), new Vector2(stage_pillarX+2004, stage_pillarY+1616) );//1617 works
		//seats
		bgSkele.addFGDecal( floor_seats.createSprite( "seats_left" ), new Vector2(-max+seatsX,seatsY ) );
		bgSkele.addFGDecal( floor_seats.createSprite( "seats_middle" ), new Vector2( 0+seatsX,seatsY ) );
		bgSkele.addFGDecal( floor_seats.createSprite( "seats_right" ), new Vector2( max+seatsX,seatsY ) );
		
		
		
		level.root.addSkeleton(bgSkele);
	}

	private void initBackground( ) {
		BodyDef screwBodyDef;
		Body body;
		CircleShape screwShape;
		FixtureDef screwFixture;
		Entity bg_1_0 = new Entity( "bg_1_0", new Vector2( 0, 720 ), null,
				null, false );
		Entity bg_1_1 = new Entity( "bg_1_1", new Vector2( 0, 720 ), null,
				null, false );
		for ( int i = 0; i < 2; i++ ) {
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
			switch ( i ) {
			case 0:
				bg_1_0 = new Entity( "bg_1_0", new Vector2( 0, 720 ), null,
						body, false );
				break;
			case 1:
				bg_1_1 = new Entity( "bg_1_1", new Vector2( 0, 720 ), null,
						body, false );
				break;
			}
		}
		TextureRegion tex1 = WereScrewedGame.manager.getAtlas( "bgCloud" )
				.findRegion( "bgCloud1" );
		TextureRegion tex2 = WereScrewedGame.manager.getAtlas( "bgCloud" )
				.findRegion( "bgCloud2" );
		bg_1_0.sprite = bg_1_0.constructSprite( tex1 );
		bg_1_0.sprite.setOrigin( 0f, 0f );
		bg_1_0.offset = new Vector2( bg_1_0.sprite.getOriginX( ),
				bg_1_0.sprite.getOriginY( ) );
		bg_1_0.sprite.setScale( 1f, 1.1f );
		bg_1_0.setMoverAtCurrentState( new ParallaxMover(
				new Vector2( 0, 1024 ), new Vector2( 0, -1024 ), 0.0004f, .5f,
				level.camera, false, LinearAxis.VERTICAL ) );
		bg_1_1.sprite = bg_1_1.constructSprite( tex2 );
		bg_1_1.sprite.setOrigin( 0f, 0f );
		bg_1_1.offset = new Vector2( bg_1_1.sprite.getOriginX( ),
				bg_1_1.sprite.getOriginY( ) );
		bg_1_1.sprite.setScale( 1f, 1.1f );
		bg_1_1.setMoverAtCurrentState( new ParallaxMover(
				new Vector2( 0, 1024 ), new Vector2( 0, -1024 ), 0.0004f, 0f,
				level.camera, false, LinearAxis.VERTICAL ) );
		level.backgroundRootSkeleton.addLooseEntity( bg_1_0 );
		level.backgroundRootSkeleton.addLooseEntity( bg_1_1 );
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

		RevoluteJointDef rjd = new RevoluteJointDef( );
		rjd.initialize( structurePlat3.body, pivotPlat1.body, pivotPlat1
				.getPosition( ).add( pivotPlat1.getMeterWidth( ) / 2, 0 ) );
		rjd.collideConnected = false;
		level.world.createJoint( rjd );

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

			footSkeleton.body.setType( BodyType.DynamicBody );
			kneeSkeleton.body.setType( BodyType.DynamicBody );
			thighSkeleton.body.setType( BodyType.DynamicBody );

			if ( hipSkeleton.currentMover( ) == null ) {
				// hipSkeleton.addMover( new RotateTweenMover(hipSkeleton, 3f,
				// -Util.PI / 2, 1f, true),
				// RobotState.IDLE );

				PathBuilder pb = new PathBuilder( );
				hipSkeleton.addMover( pb.begin( hipSkeleton )
						.target( 0, 100, 3 ).delay( 1 ).target( 0, -25, 3 )
						.target( 0, 0, 3 ).build( ), RobotState.IDLE );

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
		// stuctureScrew1.setDetachDirection( 0, -1 );

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

}
