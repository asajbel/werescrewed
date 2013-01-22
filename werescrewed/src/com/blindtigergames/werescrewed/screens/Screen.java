package com.blindtigergames.werescrewed.screens;

public enum Screen {

	INTRO {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new IntroScreen( );
		}
	},
	PAUSE {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new PauseScreen( );
		}
	},
	LEVELTEST {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new LevelTestScreen( );
		}
	},
	LOADING {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new LoadingScreen( );
		}
	},
	MAIN_MENU {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new MainMenuScreen( );
		}
	},

	GAME {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new GameScreen( );
		}
	},

	CREDITS {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new CreditsScreen( );
		}
	},

	PHYSICS {
		@Override
		protected com.badlogic.gdx.Screen getScreenInstance( ) {
			return new PhysicsTestScreen( );
		}
	};

	protected abstract com.badlogic.gdx.Screen getScreenInstance( );

}
