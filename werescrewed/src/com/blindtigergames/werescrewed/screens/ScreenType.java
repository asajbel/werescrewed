package com.blindtigergames.werescrewed.screens;

public enum ScreenType {
	CHARACTER_SELECT {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new CharacterSelectScreen( );
		}
	},
	
	CREDITS {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new CreditsScreen( );
		}
	},

	GAME {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new PhysicsTestScreen( );
		}
	},
	GLEED {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			//return new GleedTestScreen( "presentation" );
			return new GleedTestScreen( "PlayTestLevel" );
		}
	},
	INTRO {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new IntroScreen( );
		}
	},
	LEVEL_1 {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new Level1Screen( );
		}
	},
	LEVEL_SELECT {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new LevelSelectScreen( );
		}
	},
	// TODO: Do we need different enums for each of the loading screens,
	// since they'll each load different things depending on the level?
	LOADING {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new LoadingScreen( );
		}
	},
	LOADING_1 {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new LoadingScreen( "level1" );
		}
	},
	LOADING_2 {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new LoadingScreen( "level2" );
		}
	},
	LOADING_3 {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new LoadingScreen( "level3" );
		}
	},
	LOADING_4 {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new LoadingScreen( "level4" );
		}
	},
	LOADING_5 {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new LoadingScreen( "level5" );
		}
	},
	LOADING_TEST {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new LoadingScreen( "testLevel" );
		}
	},
	OPTIONS {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new OptionsScreen( );
		}
	},
	PAUSE {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new PauseScreen( );
		}
	},
	PLAYTEST {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new DebugPlayTestScreen( );
		}
	},
	PHYSICS {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new PhysicsTestScreen( );
		}
	},
	RESURRECT {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new ResurrectScreen( );
		}
	},
	HAZARD {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new HazardScreen( );
		}
	},
	MAIN_MENU {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new MainMenuScreen( );
		}
	},
	
	STORY {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new StoryModeScreen( );
		}
	},
	WIN {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new WinScreen( );
		}
	},

	TROPHY {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new TrophyScreen( );
		}
	};

	protected abstract com.badlogic.gdx.Screen getScreenInstance( );

}
