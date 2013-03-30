package com.blindtigergames.werescrewed.screens;

import com.badlogic.gdx.Screen;

public enum ScreenType {	
	CREDITS {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			CreditsScreen credits = new CreditsScreen( );
			credits.screenType = ScreenType.CREDITS;
			return credits;
		}
	},

//	GAME {
//		@Override
//		protected com.badlogic.gdx.Screen getScreenInstance( ) {
//			PhysicsTestScreen physics = new PhysicsTestScreen( );
//			physics.screenType = ScreenType.GAME;
//			return physics;
//		}
//	},
	GLEED {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			GleedTestScreen gleed = new GleedTestScreen( "FinalPresentationLevel2" );
			gleed.screenType = ScreenType.GLEED;
			return gleed;
		}
	},
	INTRO {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			IntroScreen intro = new IntroScreen( );
			intro.screenType = ScreenType.INTRO;
			return intro;
		}
	},
	LEVEL_1 {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			Level1Screen level1 = new Level1Screen( );
			level1.screenType = ScreenType.LEVEL_1;
			return level1;
		}
	},
	LEVEL_SELECT {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			LevelSelectScreen lss = new LevelSelectScreen( );
			lss.screenType = ScreenType.LEVEL_SELECT;
			return lss;
		}
	},

	LOADING {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			LoadingScreen loading = new LoadingScreen( );
			loading.screenType = ScreenType.LOADING;
			return loading;
		}
	},
	LOADING_1 {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			LoadingScreen loading = new LoadingScreen( "level1" );
			loading.screenType = ScreenType.LOADING;
			return loading;
		}
	},
	LOADING_2 {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			LoadingScreen loading = new LoadingScreen( "level2" );
			loading.screenType = ScreenType.LOADING;
			return loading;
		}
	},
	LOADING_3 {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			LoadingScreen loading = new LoadingScreen( "level3" );
			loading.screenType = ScreenType.LOADING;
			return loading;
		}
	},
	LOADING_4 {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			LoadingScreen loading = new LoadingScreen( "level4" );
			loading.screenType = ScreenType.LOADING;
			return loading;
		}
	},
	LOADING_5 {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			LoadingScreen loading = new LoadingScreen( "level5" );
			loading.screenType = ScreenType.LOADING;
			return loading;
		}
	},
	LOADING_TEST {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			LoadingScreen loading = new LoadingScreen( "testLevel" );
			loading.screenType = ScreenType.LOADING;
			return loading;
		}
	},
	OPTIONS {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			OptionsScreen options = new OptionsScreen( );
			options.screenType = ScreenType.OPTIONS;
			return options;
		}
	},
	PAUSE {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			PauseScreen pause = new PauseScreen( );
			pause.screenType = ScreenType.PAUSE;
			return pause;
		}
	},
	PLAYTEST {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			DebugPlayTestScreen debug = new DebugPlayTestScreen( );
			debug.screenType = ScreenType.PLAYTEST;
			return debug;
		}
	},
	PHYSICS {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			PhysicsTestScreen physics = new PhysicsTestScreen( );
			physics.screenType = ScreenType.PHYSICS;
			return physics;		}
	},
	RESURRECT {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			ResurrectScreen resurrect = new ResurrectScreen( );
			resurrect.screenType = ScreenType.RESURRECT;
			return resurrect;
		}
	},
	HAZARD {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			HazardScreen hazard = new HazardScreen( );
			hazard.screenType = ScreenType.HAZARD;
			return hazard;
		}
	},
	MAIN_MENU {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			MainMenuScreen menu = new MainMenuScreen( );
			menu.screenType = ScreenType.MAIN_MENU;
			return menu;
		}
	},
	
	WIN {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			WinScreen winning = new WinScreen( );
			winning.screenType = ScreenType.WIN;
			return winning;
		}
	},

	TROPHY {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			TrophyScreen trophy = new TrophyScreen( );
			trophy.screenType = ScreenType.TROPHY;
			return trophy;
		}
	}, 
	
	PREVSCREEN {
		
		// USED IN PAUSE SCREEN TO RETURN TO THE LAST SCREEN
		// I COULDN'T THINK OF BETTER FIX, BUT THIS WORKS FINE
		
		@Override
		protected Screen getScreenInstance( ) {
			return null;
		}
	},
	
	POLY {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			PolySpriteTestScreen psts = new PolySpriteTestScreen( "testLevel" );
			psts.screenType = ScreenType.POLY;
			return psts;
		}
	};

	protected abstract com.badlogic.gdx.Screen getScreenInstance( );

}
