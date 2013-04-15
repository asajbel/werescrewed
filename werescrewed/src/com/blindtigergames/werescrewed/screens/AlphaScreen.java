package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.action.RemoveEntityAction;
import com.blindtigergames.werescrewed.entity.builders.EventTriggerBuilder;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.mover.AnalogRotateMover;
import com.blindtigergames.werescrewed.entity.mover.RotateTweenMover;
import com.blindtigergames.werescrewed.entity.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.entity.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.entity.screws.StructureScrew;
import com.blindtigergames.werescrewed.entity.tween.PathBuilder;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.level.CharacterSelect;
import com.blindtigergames.werescrewed.level.LevelFactory;
import com.blindtigergames.werescrewed.util.Util;

public class AlphaScreen extends Screen {

	public ScreenType screenType;

	private CharacterSelect characterSelect;
	private Screw powerScrew1, powerScrew2;
	private Skeleton footSkeleton, kneeSkeleton, thighSkeleton, hipSkeleton, chestSkeleton,
		leftShoulderSkeleton;
	private TiledPlatform kneeMovingPlat, leftShoulderSideHatch;
	private PuzzleScrew leftArmScrew;
	
	public AlphaScreen( ) {
		super( );
		String filename = "data/levels/alphalevel.xml";
		level = new LevelFactory( ).load( filename );
		
		//death barrier
		EventTriggerBuilder etb = new EventTriggerBuilder( level.world );
		EventTrigger removeTrigger = etb.name( "removeEntity" ).rectangle( )
				.width( 10 ).height( 50000 )
				.position( new Vector2( 0, -3200 ) )
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
					.position( -1650f, 6100f ).name( "player1" ).definition( "red_male" ).buildPlayer( );
			level.progressManager.addPlayerOne( level.player1 );
		}
		if ( level.player2 == null ) {
			level.player2 = new PlayerBuilder( ).world( level.world )
					.position( -1650f, 6100f ).name( "player2" ).definition( "red_female" ).buildPlayer( );
			level.progressManager.addPlayerTwo( level.player2 );
		}

		chestObjects( );
		leftArm();

	}

	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );

		// characterSelect.update( );

		// characterSelect.draw( batch, deltaTime );

		powerScrew1and2update( );
		
		if(leftArmScrew.getDepth( ) == leftArmScrew.getMaxDepth( )){
			leftShoulderSkeleton.addMover( new RotateTweenMover( leftShoulderSkeleton, 10,
					-Util.PI / 2, 0, false ), RobotState.IDLE);
		}


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
		//stuctureScrew1.setDetachDirection( 0, -1 );


	}
	
	private void leftArm(){
		leftArmScrew = ( PuzzleScrew ) LevelFactory.entities.get( "leftShoulderPuzzleScrew1" );
		
		leftShoulderSkeleton = ( Skeleton ) LevelFactory.entities.get( "leftShoulderSkeleton" );
		
		leftShoulderSideHatch = ( TiledPlatform ) LevelFactory.entities
				.get( "leftShoulderSideHatch" );
		
		RevoluteJointDef rjd = new RevoluteJointDef( );
		rjd.initialize( leftShoulderSideHatch.body, leftShoulderSkeleton.body, leftShoulderSideHatch
				.getPosition( ).sub( 0 , leftShoulderSideHatch.getMeterHeight( ) / 2 ) );
		level.world.createJoint( rjd );
	}

}
