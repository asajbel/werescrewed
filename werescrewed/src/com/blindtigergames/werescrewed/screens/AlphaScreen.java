package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.blindtigergames.werescrewed.entity.RobotState;
import com.blindtigergames.werescrewed.entity.Skeleton;
import com.blindtigergames.werescrewed.entity.action.EntityActivateMoverAction;
import com.blindtigergames.werescrewed.entity.builders.PlayerBuilder;
import com.blindtigergames.werescrewed.entity.platforms.TiledPlatform;
import com.blindtigergames.werescrewed.entity.screws.PuzzleScrew;
import com.blindtigergames.werescrewed.entity.screws.Screw;
import com.blindtigergames.werescrewed.entity.tween.PathBuilder;
import com.blindtigergames.werescrewed.eventTrigger.EventTrigger;
import com.blindtigergames.werescrewed.level.CharacterSelect;
import com.blindtigergames.werescrewed.level.LevelFactory;

public class AlphaScreen extends Screen {

	public ScreenType screenType;

	private CharacterSelect characterSelect;
	private Screw powerScrew1, powerScrew2;
	private Skeleton footSkeleton, kneeSkeleton, thighSkeleton, hipSkeleton, chestSkeleton;
	private TiledPlatform kneeMovingPlat;
	private RevoluteJoint fallingBlockJoint, pathBlockingJoint;
	private EventTrigger fallingBlockEvent;
	
	public AlphaScreen( ) {
		super( );
		String filename = "data/levels/alphalevel.xml";
		level = new LevelFactory( ).load( filename );

		characterSelect = new CharacterSelect( level );

		createFootObjects( );
		createKneeObjects( );

		// power screws: -700f, 1800f
		// chest : -200f, 3800f

		if ( level.player1 == null ) {
			level.player1 = new PlayerBuilder( ).world( level.world )
					.position( 600f, 4100f ).name( "player1" ).buildPlayer( );

			level.progressManager.addPlayerOne( level.player1 );
		}
		if ( level.player2 == null ) {
			level.player2 = new PlayerBuilder( ).world( level.world )
					.position( 600f, 4100f ).name( "player2" ).buildPlayer( );

			level.progressManager.addPlayerTwo( level.player2 );
		}

		chestObjects( );

	}

	@Override
	public void render( float deltaTime ) {
		super.render( deltaTime );

		// characterSelect.update( );

		// characterSelect.draw( batch, deltaTime );

		powerScrew1and2update( );
		

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

		PuzzleScrew chestScrew1 = ( PuzzleScrew ) LevelFactory.entities.get( "chestPuzzleScrew5" );
		PuzzleScrew chestScrew2 = ( PuzzleScrew ) LevelFactory.entities.get( "chestPuzzleScrew6" );
		
		chestScrew1.puzzleManager.addScrew( chestScrew2 );
		chestScrew2.puzzleManager.addScrew( chestScrew1 );
	
	}

}
